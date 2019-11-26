package eu.wauz.wazera.model.repository.docs;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.docs.Folder;

public interface FolderRepository extends CrudRepository<Folder, Integer> {

	@Override
	List<Folder> findAll();

	List<Folder> findByName(String name);

	List<Folder> findByFolderIdOrderByName(Integer folderId);

	@Query("select folder from Folder folder where folder.folderId is null")
	Folder findRootFolder();

	Folder findByNameAndFolderId(String documentTreeName, Integer folderId);

	List<Folder> findByFolderIdOrderBySortOrder(Integer folderId);
	
}
