package eu.wauz.wazera.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.model.data.DocumentData;
import eu.wauz.wazera.model.data.FolderData;
import eu.wauz.wazera.model.entity.Document;
import eu.wauz.wazera.model.entity.DocumentTag;
import eu.wauz.wazera.model.entity.Folder;
import eu.wauz.wazera.model.entity.FolderUserData;
import eu.wauz.wazera.model.entity.UserAction;
import eu.wauz.wazera.model.repository.DocumentRepository;
import eu.wauz.wazera.model.repository.DocumentTagRepository;
import eu.wauz.wazera.model.repository.FolderRepository;
import eu.wauz.wazera.model.repository.FolderUserDataRepository;
import eu.wauz.wazera.model.repository.jpa.FolderUserDataJpaRepository;

@Primary
@Service
@Scope("singleton")
public class FoldersDataService {

	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(FoldersDataService.class);

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentTagRepository documentTagRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderUserDataRepository folderUserDataRepository;

    @Autowired
    private FolderUserDataJpaRepository folderUserDataJpaRepository;

    private DocsTool docsTool;

    @PostConstruct
    public void init() {
    	docsTool = new DocsTool();
    }

	private void deleteSubtree(int rootNodeId) throws Exception {
		List<Folder> foldersToSearch = new ArrayList<>();
    	List<Folder> foldersToDelete = new ArrayList<>();
    	List<Document> documentsToDelete = new ArrayList<>();
    	
		foldersToSearch.add(folderRepository.findById(rootNodeId).get());
		while(!foldersToSearch.isEmpty()) {
			Folder folder = foldersToSearch.remove(foldersToSearch.size() - 1);
			foldersToDelete.add(folder);
			
			List<Folder> subFolders = folderRepository.findByFolderIdOrderByName(folder.getId());
			foldersToSearch.addAll(subFolders);
			
			List<Document> documents = documentRepository.findByFolderIdOrderByName(folder.getId());
			documentsToDelete.addAll(documents);
		}
		
		for (Document document : documentsToDelete) {
			documentRepository.delete(document);
		}
		
		for (Folder folder : foldersToDelete) {
			folderRepository.delete(folder);
		}
	}

	private DocumentData readDocumentData(Document document) {
		DocumentData documentData = new DocumentData();
		try {
			documentData.setContent(document.getContent());
			documentData.setId(document.getId());
			documentData.setName(document.getName());

			List<DocumentTag> documentTags = documentTagRepository.findByDocumentId(document.getId());
			if(documentTags != null) {
				List<String> documentTagValues = documentTags.stream()
						.map(documentTag -> documentTag.getValue())
						.collect(Collectors.toList());
				documentData.setTags(documentTagValues);
			} else
				documentData.setTags(new ArrayList<>());
		} catch(Exception e) {
			log.error("", e);
		}
		return documentData;
	}

	private FolderData readFolderData(Folder folder) {
		FolderData folderData = new FolderData();
		if(folder != null) {
			folderData.setId(folder.getId());
			folderData.setName(folder.getName());
			folderData.setDirectory(folder.getDirectory());
			FolderUserData folderUserData = folderUserDataJpaRepository.findByFolderAndUser(folder.getId(), getUsername());
			folderData.setExpanded(folderUserData != null ? folderUserData.getExpanded() : false);
		}
		return folderData;
	}

	private void validate(FolderData folderData) throws Exception {
		String folderName = folderData.getName();
		String path = folderData.getDirectory();
		if(StringUtils.isBlank(path))
			path = folderData.getParent() == null ? docsTool.getPath("", folderRepository.findById(folderData.getId()).get())
					: docsTool.getPath("", folderRepository.findById(folderData.getParent().getId()).get()) + folderData.getName() + "/";

		List<Folder> foldersWithSameName = folderRepository.findByName(folderData.getName());
		boolean sameFolder = false;
		if(folderData.getId() != null) {
			Folder oldFolder = folderRepository.findById(folderData.getId()).get();
			String oldFolderPath = docsTool.getPath("", oldFolder);
			if(oldFolderPath.equals(path))
				sameFolder = true;
		}

		if(!sameFolder) {
			for(Folder folder : foldersWithSameName) {
				String existingFolderPath = docsTool.getPath("", folder);
				if(existingFolderPath.equals(path)) {
					throw new DocsInvalidNameException("Eine gleichnamige Datei existiert bereits!");
				}
			}
		}

		List<Folder> rootFolders = folderRepository.findRootFolders();
		for(Folder rootFolder : rootFolders) {
			if(!(folderData.getId() != null && folderRepository.findById(folderData.getId()).get().equals(rootFolder)) && folderData.getDirectory() != null) {
				if(rootFolder.getName().equals(folderName)) {
					throw new DocsInvalidNameException("Dieser Name wird bereits von einem Baum verwendet!");
				}
				else if(rootFolder.getDirectory().equals(folderData.getDirectory())) {
					throw new DocsInvalidNameException("Ein anderer Baum verweist bereits auf diesen Pfad!");
				}
			}
		}

		docsTool.checkForValidFileName(folderName);

	}

	public FolderData saveFolder(FolderData folderData, Integer index, Boolean validate) throws Exception {
		validate(folderData);

        Folder folder = null;
        if(folderData.getId() != null)
        	folder = folderRepository.findById(folderData.getId()).get();
        else
        	folder = new Folder();

        folder.setDirectory(folderData.getDirectory());

        /** rename folder in fs */
        String oldDir = docsTool.getPath("", folder);
        if(oldDir != null)
        	new File(oldDir).mkdirs();
        String newDir = null;
        System.out.println();
        if(folderData.getParent() != null) {
        	folder.setName(folderData.getName());
        	folder.setFolderId(folderData.getParent().getId());
        	newDir = docsTool.getPath("", folder);
        	new File(newDir).mkdirs();
        }
        else {
        	folder.setName(folderData.getName());
        	newDir = docsTool.getPath("", folder);
        }
        if(oldDir != null && newDir != null && !StringUtils.equals(oldDir, newDir)) {
        	try {
				Path oldPath = Paths.get(oldDir);
				Path newPath = Paths.get(newDir);
				new File(newDir).delete();
				Files.move(oldPath, newPath);
			}
			catch (Exception e) {
				log.error("", e);
			}
        }

        /** sort */
		if(index != null) {
			List<Folder> allFoldersInFolder = folderRepository.findByFolderIdOrderBySortOrder(folder.getFolderId());
			if(!allFoldersInFolder.isEmpty()) {
				Integer folderId = folder.getId();

				System.out.println("");
				System.out.println("----- Vorher -----");
				for(Folder sortFolder : allFoldersInFolder) {
					System.out.println(sortFolder.getSortOrder() + " " + sortFolder.getName());
				}
				System.out.println("");

				List<Folder> sortFolders = allFoldersInFolder.stream()
						.filter(sortFolder -> folderId == null && sortFolder.getId() != null || !folderId.equals(sortFolder.getId()))
						.collect(Collectors.toList());
				index = index > sortFolders.size() ? sortFolders.size() : index;
				sortFolders.add(index, folder);

				sortFolders = docsTool.sortFolders(sortFolders);

				Integer oldIndex = folder.getSortOrder();

				for(Folder sortFolder : sortFolders) {
					if(sortFolder.getId().equals(folderId)) {
						sortFolder.setSortOrder(index);
					} else if(index < oldIndex && sortFolder.getSortOrder() >= index) {
						sortFolder.setSortOrder(sortFolder.getSortOrder() + 1);
					} else if(index > oldIndex && sortFolder.getSortOrder() <= index) {
						sortFolder.setSortOrder(sortFolder.getSortOrder() - 1);
					}
				}

				sortFolders = docsTool.sortFolders(sortFolders);

				for(Folder sortFolder : sortFolders) {
					if(sortFolder.getId().equals(folderId))
						folder.setSortOrder(sortFolder.getSortOrder());
				}

				System.out.println("----- Nachher  -----");
				for(Folder sortFolder : allFoldersInFolder) {
					System.out.println(sortFolder.getSortOrder() + " " + sortFolder.getName());

					if(folderId == null && sortFolder.getId() == null || folderId.equals(sortFolder.getId()))
						sortFolders.remove(sortFolder);
				}
				System.out.println("");

				folderRepository.saveAll(sortFolders);
			}
		}
		saveFolderUserData(folderData);
		folder = folderRepository.save(folder);
        folderData.setId(folder.getId());
        return folderData;
    }

	private void saveFolderUserData(FolderData folderData) {
		FolderUserData folderUserDataFromRepo = folderUserDataJpaRepository.findByFolderAndUser(folderData.getId(), getUsername());
		FolderUserData folderUserData = folderUserDataFromRepo != null ? folderUserDataFromRepo : new FolderUserData();
		folderUserData.setUserName(getUsername());
		folderUserData.setFolderId(folderData.getId());
		folderUserData.setExpanded(folderData.isExpanded());
		folderUserDataRepository.save(folderUserData);
	}

	private String getUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

	public List<FolderData> getTreeDatas() {
        List<Folder> rootFolders = folderRepository.findRootFolders();
        return rootFolders.stream()
        		.map(folder -> readFolderData(folder))
        		.collect(Collectors.toList());
	}

	public void deleteTree(int treeId) throws Exception {
        deleteSubtree(treeId);
	}

	public void deleteFolder(FolderData folderData) throws Exception {
        deleteSubtree(folderData.getId());
	}

	public String getFolderName(Integer documentId) {
		return folderRepository.findById(documentId).get().getName();
	}

}
