package eu.wauz.wazera.model.repository.jpa;

import eu.wauz.wazera.model.entity.FolderUserData;

public interface FolderUserDataJpaRepository {

	FolderUserData findByFolderAndUser(Integer folderId, String userName);

}
