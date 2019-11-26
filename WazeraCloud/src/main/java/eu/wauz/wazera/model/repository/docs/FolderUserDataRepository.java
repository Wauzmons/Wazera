package eu.wauz.wazera.model.repository.docs;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.docs.FolderUserData;

public interface FolderUserDataRepository extends CrudRepository<FolderUserData, Integer> {
	
	List<FolderUserData> findByFolderId(Integer folderId);

}
