package eu.wauz.wazera.service;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import eu.wauz.wazera.model.data.FolderData;
import eu.wauz.wazera.model.entity.Document;
import eu.wauz.wazera.model.entity.Folder;
import eu.wauz.wazera.model.entity.FolderUserData;
import eu.wauz.wazera.model.repository.DocumentRepository;
import eu.wauz.wazera.model.repository.DocumentTagRepository;
import eu.wauz.wazera.model.repository.FolderRepository;
import eu.wauz.wazera.model.repository.FolderUserDataRepository;
import eu.wauz.wazera.model.repository.jpa.DocumentJpaRepository;
import eu.wauz.wazera.model.repository.jpa.FolderJpaRepository;
import eu.wauz.wazera.model.repository.jpa.FolderUserDataJpaRepository;

@Primary
@Service
@Scope("singleton")
public class DocumentsDataService {

    private static final int TYPE_DOCUMENT = 0;

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(DocumentsDataService.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentJpaRepository documentJpaRepository;

    @Autowired
    private DocumentTagRepository documentTagRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderJpaRepository folderJpaRepository;

    @Autowired
    private FolderUserDataRepository folderUserDataRepository;

    @Autowired
    private FolderUserDataJpaRepository folderUserDataJpaRepository;

	private DocsTool docsTool;

    @PostConstruct
    public void init() {
    	docsTool = new DocsTool();
    }

    public FolderData getDocuments(int treeId, Integer docId, List<String> searchTokens, UUID progressBarUUID) throws Exception {
    	//System.out.println("getDocuments start: " + System.currentTimeMillis());
        FolderData rootNode = null;

        Folder rootFolder = folderRepository.findById(treeId).get();
        String rootFolderPath = docsTool.getPath("", rootFolder);

        if(!new File(rootFolderPath).exists())
        	throw new Exception("Dateisystem nicht erreichbar!");

        if(docId != null)
        	expandParentFolders(docId);

        rootNode = readFolderData(rootFolder);

        Map<Integer, FolderData> folderDataMap = new HashMap<>();
        Map<Integer, Folder> folderMap = new HashMap<>();

        Set<String> addedFiles = new HashSet<>();
        if(!searchTokens.isEmpty()) {
        	Set<Folder> matchingFolders = new HashSet<>(folderJpaRepository.findByTags(searchTokens));
	        Set<Document> matchingDocuments = new HashSet<>(documentJpaRepository.findByTags(searchTokens));
	        System.out.println("Found Folders: " + matchingFolders);
	        System.out.println("Found Documents: " + matchingDocuments);

	        Queue<Folder> queue = new ArrayDeque<>();
	        queue.addAll(matchingFolders);

	        while(!queue.isEmpty()) {
        		Folder folder = queue.poll();
        		getFolderData(folderDataMap, folderMap, folder.getId());
	        	documentRepository.findByFolderIdOrderBySortOrder(folder.getId()).stream()
	        		.forEach(doc -> matchingDocuments.add(doc));

	        	queue.addAll(folderRepository.findByFolderIdOrderBySortOrder(folder.getId()));
        	}

	        for(Document document : matchingDocuments) {
	        	if(document == null)
	        		continue;
	        	System.out.println(document);
	        	if(document.getType() == TYPE_DOCUMENT) {
		        	FolderData documentFolderData = getFolderData(folderDataMap, folderMap, document.getFolderId());
		        	if(documentFolderData != null && documentFolderData.getDocuments() != null) {
		        		documentFolderData.getDocuments().add(readDocumentData(document));
		        		System.out.println("added document " + document + " to folder " + documentFolderData);
		        	}
	        	}
	        	else if (document.getType() == TYPE_FILE) {
	        		FileData fileData = readFileData(document);
	        		addedFiles.add(fileData.getDirectory());

		        	FolderData documentFolderData = getFolderData(folderDataMap, folderMap, document.getFolderId());
		        	documentFolderData.getFiles().add(fileData);
		        	System.out.println("added file " + document + " to folder " + documentFolderData);
	        	}
			}

	        System.out.println();
	        queue.addAll(folderMap.values());

	        while(!queue.isEmpty()) {
	        	System.out.println();
	        	Folder folder = queue.poll();
	        	Folder parent = null;
	        	if(folder.getFolderId() != null)
	        		parent = folderRepository.findById(folder.getFolderId()).get();
	        	if(parent != null) {
	        		FolderData parentFolderData = getFolderData(folderDataMap, folderMap, parent.getId());
	        		FolderData folderData = getFolderData(folderDataMap, folderMap, folder.getId());
	        		folderData.setExpanded(true);
	        		if(!parent.getId().equals(rootFolder.getId())) {
	        			if(!parentFolderData.getFolders().contains(folderData)) {
		        			parentFolderData.getFolders().add(folderData);
		        			System.out.println("added folder " + folderData + " to parent " + parentFolderData);
		        			queue.offer(parent);
		        			System.out.println("add parent " + parentFolderData + " to queue");
	        			}
	        			else {
		        			System.out.println("folder " + folderData + " already contained in parent " + parentFolderData);
	        			}
	        		}
	        		else {
	        			if(!rootNode.getFolders().contains(folderData)) {
		        			rootNode.getFolders().add(folderData);
		        			System.out.println("added folder " + folderData + " to rootNode " + rootNode);
	        			}
	        			else {
		        			System.out.println("folder " + folderData + " already contained in rootNode " + rootNode);

	        			}
	        		}
	        	}
	        }
        }
        else {
        	System.out.println("getting amount of files to process");
//        	int filesInFS = getAmountOfFilesInFileSystem(twDocsTool.getPath("", rootFolder));
//        	int filesInDB = getAmountOfFilesInDatabase(rootFolder);
        	progressMap.put(progressBarUUID, new Progress(0));
//        	System.out.println(filesInFS + " files in FS and " + filesInDB + " files in DB");

        	System.out.println("synchronizing with file system");
        	syncWithFileSystem(docsTool.getPath("", rootFolder), rootNode, progressBarUUID);

        	System.out.println("reading documents and building tree");
        	addFolders(rootFolder, rootNode, searchTokens, addedFiles, progressBarUUID);
        	addDocuments(rootFolder, rootNode, searchTokens, addedFiles, progressBarUUID);
            addFiles(rootFolder, rootNode, addedFiles, progressBarUUID);

            //System.out.println(progressMap.get(progressBarUUID).getPercentage());
            progressMap.get(progressBarUUID).reset();
        }

        return rootNode ;
    }

    private void expandParentFolders(int docId) {
    	Document document = documentRepository.findById(docId).get();
    	if(document == null)
    		return;

    	Integer folderId = document.getFolderId();
    	while(folderId != null) {
    		Folder parentFolder = folderRepository.findById(folderId).get();
    		if(parentFolder != null) {
    			FolderData parentFolderData = readFolderData(parentFolder);
    			parentFolderData.setExpanded(true);
    			saveFolderUserData(parentFolderData);
    			folderId = parentFolder.getFolderId();
    		} else
    			folderId = null;
    	}
    }

    private void saveFolderUserData(FolderData folderData) {
		FolderUserData folderUserDataFromRepo = folderUserDataJpaRepository.findByFolderAndUser(folderData.getId(), getUsername());
		FolderUserData folderUserData = folderUserDataFromRepo != null ? folderUserDataFromRepo : new FolderUserData();
		folderUserData.setUserName(getUsername());
		folderUserData.setFolderId(folderData.getId());
		folderUserData.setExpanded(folderData.isExpanded());
		folderUserDataRepository.save(folderUserData);
	}

    private int getAmountOfFilesInFileSystem(String path) {
    	int result = 0;

    	File folderFile = new File(path);
    	File[] children = folderFile.listFiles();

    	for(File childFile : children) {
    		String childPath = convertWindowsToLinuxPath(childFile.getAbsolutePath());
    		result++;
    		if(childFile.isDirectory()) {
    			result += getAmountOfFilesInFileSystem(childPath);
    		}
    	}
    	return result;
    }

    private int getAmountOfFilesInDatabase(Folder folder) {
    	int result = 0;
    	result += documentRepository.findByFolderIdOrderBySortOrder(folder.getId()).size();
    	for(Folder subFolder : folderRepository.findByFolderIdOrderBySortOrder(folder.getId())) {
    		FolderUserData subFolderUserData = folderUserDataJpaRepository.findByFolderAndUser(subFolder.getId(), getUsername());
    		if(subFolderUserData == null || !subFolderUserData.getExpanded())
    			result++;
    		else
    			result += getAmountOfFilesInDatabase(subFolder) + 1;
    	}

    	return result;
    }

    private void syncWithFileSystem(String path, FolderData folderData, UUID progressBarUUID) throws Exception {
    	File folderFile = new File(path);
    	File[] children = folderFile.listFiles();

    	/** build path,folderDataMap */
    	Map<String, FolderData> childrenInDatabase = new HashMap<>();
    	for(Folder folder : folderRepository.findByFolderIdOrderByName(folderData.getId()))
    		childrenInDatabase.put(docsTool.getPath("", folder), readFolderData(folder));

    	/** read and create all folders recursively */
    	for(File childFile : children) {
    		String childPath = convertWindowsToLinuxPath(childFile.getAbsolutePath());

    		if(childFile.isDirectory()) {
    			if(!childrenInDatabase.keySet().contains(childPath + "/")) {
    				FolderData childFolderData = createFolderData(folderRepository.findById(folderData.getId()).get(), childFile);
    				syncWithFileSystem(childPath, childFolderData, progressBarUUID);
    			} else {
    				FolderUserData childFolderUserData = folderUserDataJpaRepository.findByFolderAndUser(childrenInDatabase.get(childPath + "/").getId(), getUsername());
    		    	if(childFolderUserData == null || !childFolderUserData.getExpanded())
    		    		continue;
    		    	else
    		    		syncWithFileSystem(childPath, childrenInDatabase.get(childPath + "/"), progressBarUUID);
    			}
    		}
    	}

    	FolderUserData folderUserData = folderUserDataJpaRepository.findByFolderAndUser(folderData.getId(), getUsername());
    	if(folderUserData == null || !folderUserData.getExpanded())
    		return;

    	/** update document.deleted from file system */
    	path = docsTool.getPath("", folderRepository.findById(folderData.getId()).get());
    	List<Document> documents = documentRepository.findByFolderIdOrderBySortOrder(folderData.getId());
    	for(Document document : documents) {

    		String absoluteFilename = path + document.getName();
    		if(document.getType() == TYPE_DOCUMENT) {
    			absoluteFilename += ".pdf";
    		}

    		File documentFile = new File(absoluteFilename);
    		if(!documentFile.exists()) {
    			System.err.println(document.getName() + " not found at " + absoluteFilename);
    			document.setDeleted(true);
    			documentRepository.save(document);
    		}
    		else {
    			document.setDeleted(false);
    			documentRepository.save(document);
    		}
    	}

    	/** update folder.deleted from file system */
    	List<Folder> folders = folderRepository.findByFolderIdOrderByName(folderData.getId());
    	for(Folder folder : folders) {
    		folderFile = new File(path + folder.getName());
    		if(!folderFile.exists()) {
    			System.err.println(folder.getName() + " not found at " + path + folder.getName());
    			folder.setDeleted(true);
    			folderRepository.save(folder);
    		}
    		else {
    			folder.setDeleted(false);
    			folderRepository.save(folder);
    		}
    	}
    }

    private void addFiles(Folder folder, FolderData node, Set<String> addedFiles, UUID progressBarUUID) throws Exception {
    	String path = docsTool.getPath("", folder);
//    	System.out.println("Scanning " + folder.getName() + ": '" + path + "'");

    	/** get docnames */
    	List<Document> documents = documentRepository.findByFolderIdOrderBySortOrder(folder.getId());
    	List<String> docNames = new ArrayList<>();
    	for(Document document : documents) {

    		if(document.getType() == TYPE_DOCUMENT) {
    			docNames.add(document.getName() + ".pdf");
    		} else {
    			docNames.add(document.getName());
    			progressMap.get(progressBarUUID).increaseCurrentValue();
    		}
    	}

    	File[] files = new File(path).listFiles();
    	if(files != null) {
	    	for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if(file.isFile()) {
					if(!addedFiles.contains(convertWindowsToLinuxPath(file.getAbsolutePath())) && !docNames.contains(file.getName())) {
						progressMap.get(progressBarUUID).increaseMaximumValue();
						progressMap.get(progressBarUUID).increaseCurrentValue();
						FileData fileData = findOrCreateFileData(folder, file);
						node.getFiles().add(fileData);
					}
				}
			}
    	}
    	for (FolderData subFolder : node.getFolders()) {
    		if(subFolder.getId() != null) {
    			FolderUserData subFolderUserData = folderUserDataJpaRepository.findByFolderAndUser(subFolder.getId(), getUsername());

    			boolean isEmpty = subFolder.getDocuments().size() == 0 && subFolder.getFiles().size() == 0 && subFolder.getFolders().size() == 0;
        		if((subFolderUserData != null && subFolderUserData.getExpanded()) || isEmpty)
        			addFiles(folderRepository.findById(subFolder.getId()).get(), subFolder, addedFiles, progressBarUUID);
    		}
		}
    }

    private String convertWindowsToLinuxPath(String path) {
    	if(path.contains("p:")) {
    		path = path.replace("p:", "");
    		path = path.replace("\\", "/");
		}
    	return path;
    }

	private FolderData createFolderData(Folder folder, File file) {
		Folder fileFolder = new Folder();
		fileFolder.setName(file.getName());
		fileFolder.setTreeId(folder.getTreeId());
		fileFolder.setFolderId(folder.getId());
		fileFolder = folderRepository.save(fileFolder);

		FolderData fileData = readFolderData(fileFolder);
		return fileData;
	}

	private String parseTextContent(File file) {
		String textContent = "";

		try {
			BodyContentHandler handler = new BodyContentHandler();

			AutoDetectParser parser = new AutoDetectParser();
			Metadata metadata = new Metadata();
			try (InputStream stream = new FileInputStream(file)) {
				if(stream.available() != 0) {
					parser.parse(stream, handler, metadata);
					textContent = StringUtils.trimToEmpty(handler.toString());
				}
			}
		}
		catch (Throwable e) {
			log.error("", e);
		}
		System.out.println("textContent of " + file.getName() + ": '" + textContent + "'");
		return textContent;
	}

	private FolderData getFolderData(Map<Integer, FolderData> folderDataMap, Map<Integer, Folder> folderMap, Integer folderId) {
    	FolderData folderData = folderDataMap.get(folderId);
    	if(folderData == null) {
    		Folder folder = folderRepository.findById(folderId).get();
    		if(folder != null) {
    			folderMap.put(folderId, folder);
    			folderData = readFolderData(folder);
    			folderDataMap.put(folderId, folderData);
    			System.out.println("added folderData " + folderData + " to map");
    		}
    	}
    	return folderData;
    }

	private FolderData readFolderData(Folder folder) {
		FolderData folderData = new FolderData();
		if(folder != null) {
			folderData.setId(folder.getId());
			folderData.setName(folder.getName());
			folderData.setDirectory(folder.getDirectory());
			FolderUserData folderUserData = folderUserDataJpaRepository.findByFolderAndUser(folder.getId(), getUsername());
			folderData.setExpanded(folderUserData != null ? folderUserData.getExpanded() : false);
			folderData.setDeleted(folder.getDeleted() != null ? folder.getDeleted() : false);
		}
		return folderData;
	}

	private String getUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : null;
	}

    private void addFolders(Folder folder, FolderData node, List<String> tags, Set<String> addedFiles, UUID progressBarUUID) throws Exception {
    	List<Folder> childFolders = folderRepository.findByFolderIdOrderBySortOrder(folder.getId());
    	for (Folder childFolder : childFolders) {
    		FolderUserData childFolderUserData = folderUserDataJpaRepository.findByFolderAndUser(childFolder.getId(), getUsername());

            FolderData childNode = new FolderData();
            childNode.setId(childFolder.getId());
            childNode.setName(childFolder.getName());
            childNode.setExpanded(childFolderUserData != null ? childFolderUserData.getExpanded() : false);
            childNode.setDeleted(childFolder.getDeleted() != null ? childFolder.getDeleted() : false);

            node.getFolders().add(childNode);
            progressMap.get(progressBarUUID).increaseCurrentValue();

            if(childFolderUserData == null || !childFolderUserData.getExpanded()) {
            	int contentAmount = 0;
            	contentAmount += folderRepository.findByFolderIdOrderBySortOrder(childFolder.getId()).size();
            	contentAmount += documentRepository.findByFolderIdOrderBySortOrder(childFolder.getId()).size();
            	if(contentAmount > 0) {
            		FolderData loadingNode = new FolderData();
            		loadingNode.setName("Lade Inhalt...");
            		loadingNode.setDeleted(true);
            		loadingNode.setExpanded(false);
            		childNode.getFolders().add(loadingNode);
            	}
            }
            else {
            	addFolders(childFolder, childNode, tags, addedFiles, progressBarUUID);
            	addDocuments(childFolder, childNode, tags, addedFiles, progressBarUUID);
            }
		}
	}

	private void addDocuments(Folder folder, FolderData node, List<String> tags, Set<String> addedFiles, UUID progressBarUUID) throws Exception {
		FolderUserData folderUserData = folderUserDataJpaRepository.findByFolderAndUser(folder.getId(), getUsername());
		if(folderUserData == null || !folderUserData.getExpanded())
			return;

		List<Document> documents = documentRepository.findByFolderIdOrderBySortOrder(folder.getId());
		for (Document document : documents) {
        	if(document.getType() == TYPE_DOCUMENT) {
				DocumentData documentData = readDocumentData(document);
				progressMap.get(progressBarUUID).increaseCurrentValue();
				node.getDocuments().add(documentData);
        	}
        	else if (document.getType() == TYPE_FILE) {
        		FileData fileData = readFileData(document);
        		addedFiles.add(fileData.getDirectory());

	        	node.getFiles().add(fileData);
        	}
		}
	}

	private void validate(DocumentData documentData) throws Exception {
		String documentName = documentData.getName();
		String documentPath = documentData.getParent() == null ? getFilePath(documentData)
				: docsTool.getPath("", folderRepository.findById(documentData.getParent().getId()).get()) + documentName + ".pdf";

		if(new File(documentPath).exists()) {
			boolean sameFolder = false;
			if(documentData.getId() != null) {
				Document document = documentRepository.findById(documentData.getId()).get();
				Folder oldFolder = folderRepository.findById(document.getFolderId()).get();
				String oldFolderPath = docsTool.getPath("", oldFolder);
				String newfolderPath = documentPath.replace(documentName + ".pdf", "");
				if(oldFolderPath.equals(newfolderPath))
					sameFolder = true;
			}

			if(!sameFolder) {
				throw new DocsInvalidNameException("Eine gleichnamige Datei existiert bereits!");
			}
		}

		docsTool.checkForValidFileName(documentName);

	}

	@Override
    public DocumentData saveDocument(DocumentData documentData, Integer index, String username, Boolean validate) throws Exception {
    	validate(documentData);

        Document document = null;
		if (documentData.getId() != null) {

			document = documentRepository.findById(documentData.getId()).get();

			if(document.getCreationDate() == null) {
				document.setCreationDate(new Date());
			}

			if(documentData.getVersionNumber() == null) {
				documentData.setVersionNumber(0);
			}

			if(index == null) {
				/** Ermittle aktuelle Version und setze neue Versionsnummer*/
				Integer currentVersionNumber = document.getVersionNumber();
				if(currentVersionNumber == null)
					currentVersionNumber = 0;
				document.setVersionNumber(currentVersionNumber+1);

				/** Speichere Vorgängerversion*/
				DocumentVersionHistoryDocument documentVersionHistoryDocument = new DocumentVersionHistoryDocument();

				BeanUtils.copyProperties(document, documentVersionHistoryDocument);

				documentVersionHistoryDocument.setId(null);
				documentVersionHistoryDocument.setDocumentId(document.getId());
				documentVersionHistoryDocument.setVersionNumber(currentVersionNumber+1);
				documentVersionHistoryDocument.setUser(username);
				documentVersionHistoryDocument.setLastModified(new Date());
				System.out.println(documentVersionHistoryDocument);
				documentVersionHistoryDocument = documentVersionHistoryRepository.save(documentVersionHistoryDocument);

				System.out.println(documentVersionHistoryDocument);
			}
		}
		else {
			document = new Document();
			document.setVersionNumber(0);
			document.setCreationDate(new Date());
			index = 0;
		}

		Folder oldFolder = null;
		String oldFolderPath = null;
		if(documentData.getId() != null) {
			oldFolder = folderRepository.findById(document.getFolderId()).get();
			oldFolderPath = docsTool.getPath("", oldFolder);
		}

		if(documentData.getParent() != null)
        	document.setFolderId(documentData.getParent().getId());
        String content = documentData.getContent();
		document.setContent(content);
		document.setType(TYPE_DOCUMENT);

		Folder newFolder = folderRepository.findById(document.getFolderId()).get();
		String newFolderPath = docsTool.getPath("", newFolder);

        /** rename file in fs */
		String folderPath = docsTool.getPath("", newFolder);
		if(documentData.getId() != null) {
			new File(folderPath).mkdirs();
            String oldFile = folderPath + "/" + document.getName() + ".pdf";
            String newFile = folderPath + "/" + documentData.getName() + ".pdf";
            if(!StringUtils.equals(oldFile, newFile)) {
            	try {
					Path oldPath = Paths.get(oldFile);
					Path newPath = Paths.get(newFile);
					Files.move(oldPath, newPath);
				}
				catch (Exception e) {
					log.error("", e);
				}
            }
		}

        document.setName(documentData.getName());

        String path = newFolderPath;

        List<String> imageFileNames = createPdf(document, path);

        /** delete outdated pdfs in fs */
		if(documentData.getId() != null) {
			new File(oldFolderPath).mkdirs();
            String oldFile = oldFolderPath + "/" + document.getName() + ".pdf";
            String newFile = newFolderPath + "/" + documentData.getName() + ".pdf";
            if(!StringUtils.equals(oldFile, newFile)) {
            	try {
					Path oldPath = Paths.get(oldFile);
					Files.delete(oldPath);
				}
				catch (Exception e) {
					log.error("", e);
				}
            }
		}

        for (String imageFileName : imageFileNames) {
			Files.delete(Paths.get(imageFileName));
		}

        String textContent = "<html><head><title>" + document.getName() + "</title></head><body>" + "<h1>" + document.getName() + "</h1>" + content + "</body></html>";

        try {
			BodyContentHandler handler = new BodyContentHandler();

			AutoDetectParser parser = new AutoDetectParser();
			Metadata metadata = new Metadata();
			try (InputStream stream = new ByteArrayInputStream(textContent.getBytes())) {
			    parser.parse(stream, handler, metadata);
			    textContent = handler.toString();
			}
		}
		catch (Throwable e) {
			log.error("", e);
		}
//            System.out.println("fullText: '" + textContent + "'");
        document.setTextContent(StringUtils.trimToEmpty(textContent));

        Document savedDocument = documentRepository.save(document);
        documentData.setId(document.getId());

        /** sort */
		if(index != null) {
			List<Document> allDocumentsInFolder = documentRepository.findByFolderIdOrderBySortOrder(document.getFolderId());
			List<Folder> allFoldersInFolder = folderRepository.findByFolderIdOrderBySortOrder(document.getFolderId());
			if(!allDocumentsInFolder.isEmpty()) {

				System.out.println("");
				System.out.println("----- Vorher -----");
				for(Document doc : allDocumentsInFolder) {
					System.out.println(doc.getSortOrder() + " " + doc.getName());
				}
				System.out.println("");

				Integer documentId = document.getId();
				Integer oldIndex = (document.getSortOrder() == null ? 0 : document.getSortOrder()) + allFoldersInFolder.size();

				List<Document> docs = new ArrayList<>();
				List<Document> files = new ArrayList<>();

				for(Document doc : allDocumentsInFolder) {
					if(doc.getType() == 1)
						files.add(doc);
					else
						docs.add(doc);
				}

				docs = docsTool.sortDocuments(docs);
				files = docsTool.sortDocuments(files);

				for(Document doc : docs) {
					if(doc.getId().equals(documentId))
						doc.setSortOrder(index);
					else
						doc.setSortOrder(doc.getSortOrder() + allFoldersInFolder.size());
				}

				for(Document doc : docs) {
					if(doc.getId().equals(documentId)) {

					} else if(index < oldIndex && doc.getSortOrder() >= index) {
						doc.setSortOrder(doc.getSortOrder() + 1);
					} else if(index > oldIndex && doc.getSortOrder() <= index) {
						doc.setSortOrder(doc.getSortOrder() - 1);
					}
				}

				docs = docsTool.sortDocuments(docs);

				for(Document doc : files) {
					doc.setSortOrder(doc.getSortOrder() + docs.size());
				}

				System.out.println("----- Nachher -----");
				for(Document doc : allDocumentsInFolder) {
					System.out.println(doc.getSortOrder() + " " + doc.getName());

					if(documentId == null && doc.getId() == null || documentId.equals(doc.getId()))
						docs.remove(doc);
				}
				System.out.println("");

				documentRepository.saveAll(docs);
				documentRepository.saveAll(files);
			}
		}

        List<DocumentTag> existingTags = documentTagRepository.findByDocumentId(savedDocument.getId());
        List<String> existingTagValues = existingTags.stream()
        		.map(documentTag -> documentTag.getValue())
        		.collect(Collectors.toList());

        /** delete obsolete tags */
        List<DocumentTag> documentTagsToDelete = documentData.getTags() == null ? Collections.emptyList() : existingTags.stream()
        		.filter(documentTag -> !documentData.getTags().contains(documentTag.getValue()))
        		.collect(Collectors.toList());
        documentTagRepository.deleteAll(documentTagsToDelete);

        /** add new tags */
        List<DocumentTag> documentTagsToAdd = documentData.getTags() == null ? Collections.emptyList() : documentData.getTags().stream()
        		.filter(documentDataTag -> !existingTagValues.contains(documentDataTag))
        		.map(documentDataTag -> new DocumentTag(savedDocument.getId(), documentDataTag))
        		.collect(Collectors.toList());
        documentTagRepository.saveAll(documentTagsToAdd);

        return documentData;
    }

	private List<String> createPdf(Document document, String path) throws FileNotFoundException, DocumentException, IOException {
		String content = document.getContent();
		List<String> imageFileNames = new ArrayList<>();
		saveImages(path, content, 1, imageFileNames);
		System.out.println(content);
		String pdfContent = replaceImages(path, content, 1);

		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();

		ITextRenderer renderer = new ITextRenderer();
		renderer.getSharedContext().setReplacedElementFactory(new MediaReplacedElementFactory(renderer.getSharedContext().getReplacedElementFactory()));
		String htmlContent = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">";
		htmlContent += "<head>  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />  ";

		htmlContent += "<title>" + document.getName() + "</title>";
		htmlContent += "</head>  <body>";
		htmlContent += "<h1>" + document.getName() + "</h1>";
		htmlContent += pdfContent;
		System.out.println(pdfContent);
		htmlContent += "</body></html>";
		TagNode node = cleaner.clean(htmlContent);
		htmlContent = new PrettyXmlSerializer(props).getAsString(node);// writeToStream(node, System.out);
		renderer.setDocumentFromString(htmlContent);
		renderer.layout();

		new java.io.File(path).mkdirs();

		FileOutputStream fout = new FileOutputStream(path + "/" + document.getName() + ".pdf");
		renderer.createPDF(fout);
		fout.close();
		return imageFileNames;
	}

	@Override
	public DocumentData createDocumentFromFile(FileData fileData, String username) throws Exception {
		DocumentData result = null;

		String fileDirectory = fileData.getDirectory();

    	if(StringUtils.endsWith(fileDirectory, ".pdf")) {
    		PdfReader pdfReader = new PdfReader(fileDirectory);
    		String content = "";
    		for(int page = 1; page <= pdfReader.getNumberOfPages(); page++) {
    			content += new PdfTextExtractor(pdfReader).getTextFromPage(page);
    		}

    		DocumentData documentData = new DocumentData();
    		documentData.setName(fileData.getName().replaceAll(".pdf", ""));
    		documentData.setContent(content);
    		documentData.setParent(readFolderData(folderRepository.findById(documentRepository.findById(fileData.getId()).get().getFolderId()).get()));
    		result = saveDocument(documentData, 0, username, false);
    	}

		return result;
	}

	@Override
	public List<HistoryDocumentData> getHistory(int docId) throws Exception {
		List<HistoryDocumentData> historyDocumentDatas = new ArrayList<>();
		List<DocumentVersionHistoryDocument> historyDocuments = documentVersionHistoryRepository.findByDocumentId(docId);
		historyDocuments.stream()
			.map(doc -> readHistoryDocumentData(doc))
			.sorted((doc1, doc2) -> doc2.getId().compareTo(doc1.getId()))
			.forEach(docData -> historyDocumentDatas.add(docData));
		return historyDocumentDatas;
	}

	private String replaceImages(String path, String content, int count) {
		while(StringUtils.contains(content, startSearchSeq)) {
			content = replaceImage(path, content, count);
			System.out.println(count + " - " + content.length());
			count = count + 1;
		}
		return content;
	}

	String startSearchSeq = "<img src=\"data:image/png;base64,";
	String endSearchSeq = "\" />";

	private String replaceImage(String path, String content, int count) {
		String prefix = StringUtils.substringBefore(content, startSearchSeq);
		String suffix = StringUtils.substringAfter(StringUtils.substringAfter(content, startSearchSeq), endSearchSeq);

		String infix = "<div id=\"logo\" class=\"media\" data-src=\"" + path + "pic" + count + ".jpg\" />";

		content = prefix + infix + suffix;
		return content;
	}

    private void saveImages(String path, String content, int count, List<String> imageFileNames) {
		String startSearchSeq = "<img src=\"data:image/png;base64,";
		String endSearchSeq = "\"";

		content = StringUtils.substringAfter(content, startSearchSeq);
		if(content.length() > 0) {
			String base64String = StringUtils.substringBefore(content, endSearchSeq);

			/** get image size */
			String imageAttributesString = StringUtils.substringBefore(content, "/>");
			endSearchSeq = "px";

			Integer width = 0;
			if(imageAttributesString.contains("width")) {
				String widthString = StringUtils.substringAfter(imageAttributesString, "\"width:");
				widthString = StringUtils.substringBefore(widthString, endSearchSeq);
				if(StringUtils.isNotBlank(widthString))
					width = Integer.parseInt(widthString);
			}

			Integer height = 0;
			if(imageAttributesString.contains("height")) {
				String heightString = StringUtils.substringAfter(imageAttributesString, "\"height:");
				heightString = StringUtils.substringBefore(heightString, endSearchSeq);
				if(StringUtils.isNotBlank(heightString))
					height = Integer.parseInt(heightString);
			}

			/** decode and save image */
			byte[] data = Base64.getDecoder().decode(base64String);
			if(width != 0 || height != 0)
				data = scaleImage(data, width, height);

			try {
				(new File(path)).mkdirs();
			}
			catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				String imageFileName = path + "pic" + count + ".jpg";
				imageFileNames.add(imageFileName);
				FileOutputStream fout = new FileOutputStream(imageFileName);
				fout.write(data);
				fout.close();
			}
			catch (Exception e) {
				log.error("", e);
			}

			saveImages(path, StringUtils.substringAfter(content, endSearchSeq), count + 1, imageFileNames);
		}
	}

    public byte[] scaleImage(byte[] fileData, int width, int height) {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        try {
            BufferedImage img = ImageIO.read(in);
            if(height == 0) {
                height = (width * img.getHeight())/ img.getWidth();
            }
            if(width == 0) {
                width = (height * img.getWidth())/ img.getHeight();
            }
            Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage imageBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            imageBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0,0,0), null);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            ImageIO.write(imageBuff, "jpg", buffer);

            return buffer.toByteArray();
        }
        catch (IOException e) {
        	log.error("", e);
        }
        return fileData;
    }

	@Override
	public void deleteDocument(DocumentData documentData) throws Exception {
		File file = new File(docsTool.getFilePath(documentData));
		file.delete();
		Document documenttoDelete = documentRepository.findById(documentData.getId()).get();
		documentRepository.delete(documenttoDelete);
	}

	private DocumentData readDocumentData(Document document) throws Exception {
		DocumentData documentData = new DocumentData();

		documentData.setContent(document.getContent());
		documentData.setId(document.getId());
		documentData.setName(document.getName());
		documentData.setVersionNumber(document.getVersionNumber());
		documentData.setDeleted(document.getDeleted() != null ? document.getDeleted() : false);

		List<DocumentTag> documentTags = documentTagRepository.findByDocumentId(document.getId());
		if(documentTags != null) {
			List<String> documentTagValues = documentTags.stream()
					.map(documentTag -> documentTag.getValue())
					.collect(Collectors.toList());
			documentData.setTags(documentTagValues);
		} else
			documentData.setTags(new ArrayList<>());

		return documentData;
	}

	private HistoryDocumentData readHistoryDocumentData(DocumentVersionHistoryDocument historyDocument) {
		HistoryDocumentData historyDocumentData = new HistoryDocumentData();
		try {
			historyDocumentData.setId(historyDocument.getId());
			historyDocumentData.setDocumentId(historyDocument.getDocumentId());
			historyDocumentData.setVersionNumber(historyDocument.getVersionNumber());
			historyDocumentData.setUser(historyDocument.getUser());
			historyDocumentData.setLastModified(historyDocument.getLastModified());
			historyDocumentData.setContent(historyDocument.getContent());
		} catch(Exception e) {
			log.error("", e);
		}
		return historyDocumentData;
	}

	@Override
	public String getFilePath(DocumentData documentData) throws Exception {
		Document document = documentRepository.findById(documentData.getId()).get();
		Folder folder = folderRepository.findById(document.getFolderId()).get();

		String path = docsTool.getPath("", folder) + "/" + documentData.getName() + ".pdf";

		return path;
	}

	@Override
	public void mergeDocuments(FolderData folderData, String username) throws Exception {
		String documentContent = getMergedDocumentContent(folderRepository.findById(folderData.getId()).get(), folderData.getId());

		Document document  = documentRepository.findByNameAndFolderId(folderData.getName(), folderData.getId());

		DocumentData documentData = new DocumentData();
		if(document != null)
			documentData = readDocumentData(document);
		documentData.setContent(documentContent);
		documentData.setCreationDate(new Date());
		documentData.setName(folderData.getName());
		documentData.setParent(folderData);

		saveDocument(documentData, 0, username, true);
	}

	private String getMergedDocumentContent(Folder folder, Integer rootFolderId) {
		String documentContent = "";

		List<Folder> subFolders = folderRepository.findByFolderIdOrderBySortOrder(folder.getId());
		for (Folder subFolder : subFolders) {
			documentContent += getMergedDocumentContent(subFolder, rootFolderId);
		}

		List<Document> documents = documentRepository.findByFolderIdOrderBySortOrder(folder.getId());
		for (Document document : documents) {
			if(document.getType() == TYPE_DOCUMENT && !document.getName().equals(folder.getName())) {
				documentContent += "<h1>" + getFolderNameRecursive(folder, rootFolderId) + " - " + document.getName() + "</h1>";
				documentContent += document.getContent();
			}
		}

		return documentContent;
	}

	private String getFolderNameRecursive(Folder folder, Integer rootFolderId) {
		if(folder.getFolderId() != null && !folder.getFolderId().equals(rootFolderId)) {
			Folder parent = folderRepository.findById(folder.getFolderId()).get();
			return getFolderNameRecursive(parent, rootFolderId) + " - " + folder.getName();
		}
		return folder.getName();
	}

}
