package eu.wauz.wazera.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.model.data.FolderData;
import eu.wauz.wazera.model.entity.Document;
import eu.wauz.wazera.model.entity.Folder;
import eu.wauz.wazera.model.entity.FolderUserData;
import eu.wauz.wazera.model.repository.DocumentRepository;
import eu.wauz.wazera.model.repository.FolderRepository;
import eu.wauz.wazera.model.repository.FolderUserDataRepository;
import eu.wauz.wazera.model.repository.jpa.FolderUserDataJpaRepository;

@Primary
@Service
@Scope("singleton")
public class FoldersDataService {

    @Autowired
    private DocumentRepository documentRepository;

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

	public FolderData saveFolder(FolderData folderData, Integer index) throws Exception {
		docsTool.checkForValidFileName(folderData.getName());

        Folder folder = folderData.getId() != null ? folderRepository.findById(folderData.getId()).get() : new Folder();
        folder.setName(folderData.getName());
        if(folderData.getParent() != null) {
        	folder.setFolderId(folderData.getParent().getId());
        }
		if(index != null) {
			sortFolders(folder, index);
		}
		saveFolderUserData(folderData);
		folder = folderRepository.save(folder);
        folderData.setId(folder.getId());
        return folderData;
    }
	
	private void sortFolders(Folder folder, Integer index) throws Exception {
		List<Folder> allFoldersInFolder = folderRepository.findByFolderIdOrderBySortOrder(folder.getFolderId());
		if(allFoldersInFolder.isEmpty()) {
			return;
		}
		Integer folderId = folder.getId();

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
			if(sortFolder.getId().equals(folderId)) {
				folder.setSortOrder(sortFolder.getSortOrder());
			}
		}
		folderRepository.saveAll(sortFolders);
	}

	private void saveFolderUserData(FolderData folderData) {
		FolderUserData folderUserDataFromRepo = folderUserDataJpaRepository.findByFolderAndUser(folderData.getId(), docsTool.getUsername());
		FolderUserData folderUserData = folderUserDataFromRepo != null ? folderUserDataFromRepo : new FolderUserData();
		folderUserData.setUserName(docsTool.getUsername());
		folderUserData.setFolderId(folderData.getId());
		folderUserData.setExpanded(folderData.isExpanded());
		folderUserDataRepository.save(folderUserData);
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
	
	public String getFolderName(Integer documentId) {
		return folderRepository.findById(documentId).get().getName();
	}

	public FolderData getRootFolder() {
        Folder rootFolder = folderRepository.findRootFolder();
        if(rootFolder == null) {
        	rootFolder = new Folder();
        	rootFolder.setName("Document Tree");
        	rootFolder = folderRepository.save(rootFolder);
        }
        return readFolderData(rootFolder);
	}

	public void deleteTree(int treeId) throws Exception {
        deleteSubtree(treeId);
	}

	public void deleteFolder(FolderData folderData) throws Exception {
        deleteSubtree(folderData.getId());
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

}
