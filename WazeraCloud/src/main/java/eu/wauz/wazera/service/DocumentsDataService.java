package eu.wauz.wazera.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.docs.FolderData;
import eu.wauz.wazera.model.entity.docs.Document;
import eu.wauz.wazera.model.entity.docs.DocumentTag;
import eu.wauz.wazera.model.entity.docs.Folder;
import eu.wauz.wazera.model.entity.docs.FolderUserData;
import eu.wauz.wazera.model.repository.docs.DocumentRepository;
import eu.wauz.wazera.model.repository.docs.DocumentTagRepository;
import eu.wauz.wazera.model.repository.docs.FolderRepository;
import eu.wauz.wazera.model.repository.docs.FolderUserDataRepository;
import eu.wauz.wazera.model.repository.docs.jpa.DocumentJpaRepository;
import eu.wauz.wazera.model.repository.docs.jpa.FolderJpaRepository;
import eu.wauz.wazera.model.repository.docs.jpa.FolderUserDataJpaRepository;

@Service
@Scope("singleton")
public class DocumentsDataService {

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

    public FolderData getDocuments(int treeId, Integer docId, List<String> searchTokens) throws Exception {
        FolderData rootNode = null;
        Folder rootFolder = folderRepository.findById(treeId).orElse(null);
        if(docId != null) {
        	expandParentFolders(docId);
        }
        rootNode = readFolderData(rootFolder);

        Map<Integer, FolderData> folderDataMap = new HashMap<>();
        Map<Integer, Folder> folderMap = new HashMap<>();

        Set<String> addedFiles = new HashSet<>();
        if(!searchTokens.isEmpty()) {
        	Set<Folder> matchingFolders = new HashSet<>(folderJpaRepository.findByTags(searchTokens));
	        Set<Document> matchingDocuments = new HashSet<>(documentJpaRepository.findByTags(searchTokens));

	        Queue<Folder> queue = new ArrayDeque<>();
	        queue.addAll(matchingFolders);
	        
	        while(!queue.isEmpty()) {
        		Folder folder = queue.poll();
        		addFolderData(folderDataMap, folderMap, folder.getId());
	        	documentRepository.findByFolderIdOrderBySortOrder(folder.getId()).stream()
	        		.forEach(doc -> matchingDocuments.add(doc));

	        	queue.addAll(folderRepository.findByFolderIdOrderBySortOrder(folder.getId()));
        	}
	        
	        for(Document document : matchingDocuments) {
	        	FolderData documentFolderData = addFolderData(folderDataMap, folderMap, document.getFolderId());
	        	if(documentFolderData != null && documentFolderData.getDocuments() != null) {
	        		documentFolderData.getDocuments().add(readDocumentData(document));
	        	}
			}

	        queue.addAll(folderMap.values());
	        
	        while(!queue.isEmpty()) {
	        	Folder folder = queue.poll();
	        	Folder parent = null;
	        	if(folder.getFolderId() != null) {
	        		parent = folderRepository.findById(folder.getFolderId()).orElse(null);
	        	}
	        	if(parent == null) {
	        		continue;
	        	}
	        	
        		FolderData parentFolderData = addFolderData(folderDataMap, folderMap, parent.getId());
        		FolderData folderData = addFolderData(folderDataMap, folderMap, folder.getId());
        		folderData.setExpanded(true);
        		
        		if(!parent.getId().equals(rootFolder.getId()) && !parentFolderData.getFolders().contains(folderData)) {
        			parentFolderData.getFolders().add(folderData);
        			queue.offer(parent);
        		}
        		else if(!rootNode.getFolders().contains(folderData)) {
        			rootNode.getFolders().add(folderData);
    			}
	        }
        }
        else {
        	addFolders(rootFolder, rootNode, searchTokens, addedFiles);
        	addDocuments(rootFolder, rootNode, searchTokens, addedFiles);
        }

        return rootNode ;
    }

    private void expandParentFolders(int docId) {
    	Document document = documentRepository.findById(docId).orElse(null);
    	if(document == null) {
    		return;
    	}

    	Integer folderId = document.getFolderId();
    	while(folderId != null) {
    		Folder parentFolder = folderRepository.findById(folderId).orElse(null);
    		if(parentFolder != null) {
    			FolderData parentFolderData = readFolderData(parentFolder);
    			parentFolderData.setExpanded(true);
    			saveFolderUserData(parentFolderData);
    			folderId = parentFolder.getFolderId();
    		}
    		else {
    			folderId = null;
    		}
    	}
    }

    private void saveFolderUserData(FolderData folderData) {
		FolderUserData folderUserDataFromRepo = folderUserDataJpaRepository.findByFolderAndUser(folderData.getId(), docsTool.getUsername());
		FolderUserData folderUserData = folderUserDataFromRepo != null ? folderUserDataFromRepo : new FolderUserData();
		folderUserData.setUserName(docsTool.getUsername());
		folderUserData.setFolderId(folderData.getId());
		folderUserData.setExpanded(folderData.isExpanded() != null ? folderData.isExpanded() : false);
		folderUserDataRepository.save(folderUserData);
	}

	private FolderData addFolderData(Map<Integer, FolderData> folderDataMap, Map<Integer, Folder> folderMap, Integer folderId) {
    	FolderData folderData = folderDataMap.get(folderId);
    	if(folderData == null) {
    		Folder folder = folderRepository.findById(folderId).orElse(null);
    		if(folder != null) {
    			folderMap.put(folderId, folder);
    			folderData = readFolderData(folder);
    			folderDataMap.put(folderId, folderData);
    		}
    	}
    	return folderData;
    }

	private FolderData readFolderData(Folder folder) {
		FolderData folderData = new FolderData();
		if(folder != null) {
			folderData.setId(folder.getId());
			folderData.setName(folder.getName());
			FolderUserData folderUserData = folderUserDataJpaRepository.findByFolderAndUser(folder.getId(), docsTool.getUsername());
			folderData.setExpanded(folderUserData != null ? folderUserData.getExpanded() : false);
		}
		return folderData;
	}

    private void addFolders(Folder folder, FolderData node, List<String> tags, Set<String> addedFiles) throws Exception {
    	List<Folder> childFolders = folderRepository.findByFolderIdOrderBySortOrder(folder.getId());
    	for (Folder childFolder : childFolders) {
    		FolderUserData childFolderUserData = folderUserDataJpaRepository.findByFolderAndUser(childFolder.getId(), docsTool.getUsername());

            FolderData childNode = new FolderData();
            childNode.setId(childFolder.getId());
            childNode.setName(childFolder.getName());
            childNode.setExpanded(childFolderUserData != null ? childFolderUserData.getExpanded() : false);

            node.getFolders().add(childNode);

            if(childFolderUserData == null || !childFolderUserData.getExpanded()) {
            	int contentAmount = 0;
            	contentAmount += folderRepository.findByFolderIdOrderBySortOrder(childFolder.getId()).size();
            	contentAmount += documentRepository.findByFolderIdOrderBySortOrder(childFolder.getId()).size();
            	if(contentAmount > 0) {
            		FolderData loadingNode = new FolderData();
            		loadingNode.setName("Loading...");
            		loadingNode.setExpanded(false);
            		childNode.getFolders().add(loadingNode);
            	}
            }
            else {
            	addFolders(childFolder, childNode, tags, addedFiles);
            	addDocuments(childFolder, childNode, tags, addedFiles);
            }
		}
	}

	private void addDocuments(Folder folder, FolderData node, List<String> tags, Set<String> addedFiles) throws Exception {
		FolderUserData folderUserData = folderUserDataJpaRepository.findByFolderAndUser(folder.getId(), docsTool.getUsername());
		if(folderUserData == null || !folderUserData.getExpanded()) {
			return;
		}

		List<Document> documents = documentRepository.findByFolderIdOrderBySortOrder(folder.getId());
		for (Document document : documents) {
			DocumentData documentData = readDocumentData(document);
			node.getDocuments().add(documentData);
		}
	}

	public DocumentData saveDocument(DocumentData documentData, Integer index, String username) throws Exception {
		docsTool.checkForValidFileName(documentData.getName());

        Document document = null;
		if (documentData.getId() != null) {
			document = documentRepository.findById(documentData.getId()).orElse(null);
		}
		else {
			document = new Document();
			index = 0;
		}
		document.setName(documentData.getName());
		document.setUser(username);
		document.setContent(documentData.getContent());
		document.setCreationDate(new Date());
		if(documentData.getParent() != null) {
			document.setFolderId(documentData.getParent().getId());
		}
        document = documentRepository.save(document);
        final int documentId = document.getId();
        documentData.setId(documentId);

		if(index != null) {
			sortDocuments(document, index);
		}

        List<DocumentTag> existingTags = documentTagRepository.findByDocumentId(documentId);
        List<String> existingTagValues = existingTags.stream()
        		.map(documentTag -> documentTag.getValue())
        		.collect(Collectors.toList());

        List<DocumentTag> documentTagsToDelete = documentData.getTags() == null ? Collections.emptyList() : existingTags.stream()
        		.filter(documentTag -> !documentData.getTags().contains(documentTag.getValue()))
        		.collect(Collectors.toList());
        documentTagRepository.deleteAll(documentTagsToDelete);

        List<DocumentTag> documentTagsToAdd = documentData.getTags() == null ? Collections.emptyList() : documentData.getTags().stream()
        		.filter(documentDataTag -> !existingTagValues.contains(documentDataTag))
        		.map(documentDataTag -> new DocumentTag(documentId, documentDataTag))
        		.collect(Collectors.toList());
        documentTagRepository.saveAll(documentTagsToAdd);

        return documentData;
    }
	
	private void sortDocuments(Document document, Integer index) throws Exception {
		List<Folder> allFoldersInFolder = folderRepository.findByFolderIdOrderBySortOrder(document.getFolderId());
		List<Document> sortDocs = documentRepository.findByFolderIdOrderBySortOrder(document.getFolderId());
		if(sortDocs.isEmpty()) {
			return;
		}
		Integer documentId = document.getId();
		Integer oldIndex = (document.getSortOrder() == null ? 0 : document.getSortOrder()) + allFoldersInFolder.size();

		sortDocs = sortDocuments(sortDocs);

		for(Document doc : sortDocs) {
			if(doc.getId().equals(documentId)) {
				doc.setSortOrder(index);
			}
			else {
				doc.setSortOrder(doc.getSortOrder() + allFoldersInFolder.size());
			}
		}

		for(Document doc : sortDocs) {
			if(doc.getId().equals(documentId)) {
				continue;
			}
			else if(index < oldIndex && doc.getSortOrder() >= index) {
				doc.setSortOrder(doc.getSortOrder() + 1);
			}
			else if(index > oldIndex && doc.getSortOrder() <= index) {
				doc.setSortOrder(doc.getSortOrder() - 1);
			}
		}
		sortDocs = sortDocuments(sortDocs);
		documentRepository.saveAll(sortDocs);
	}
	
	private List<Document> sortDocuments(List<Document> sortDocs) throws Exception {
		for(Document doc : sortDocs)
			if(doc.getSortOrder() == null)
				doc.setSortOrder(0);

		sortDocs.sort(Comparator.comparingInt(Document::getSortOrder));
		for(int i = 0; i < sortDocs.size(); i++)
			sortDocs.get(i).setSortOrder(i);

		return sortDocs;
	}

	public void deleteDocument(DocumentData documentData) throws Exception {
		Document documenttoDelete = documentRepository.findById(documentData.getId()).orElse(null);
		documentRepository.delete(documenttoDelete);
	}

	private DocumentData readDocumentData(Document document) throws Exception {
		DocumentData documentData = new DocumentData();

		documentData.setId(document.getId());
		documentData.setName(document.getName());
		documentData.setUser(document.getUser());
		documentData.setContent(document.getContent());
		documentData.setCreationDate(document.getCreationDate());

		List<DocumentTag> documentTags = documentTagRepository.findByDocumentId(document.getId());
		if(documentTags != null) {
			List<String> documentTagValues = documentTags.stream()
					.map(documentTag -> documentTag.getValue())
					.collect(Collectors.toList());
			documentData.setTags(documentTagValues);
		}
		else {
			documentData.setTags(new ArrayList<>());
		}

		return documentData;
	}

}
