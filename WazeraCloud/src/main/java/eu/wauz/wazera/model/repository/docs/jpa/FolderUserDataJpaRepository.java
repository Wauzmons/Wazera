package eu.wauz.wazera.model.repository.docs.jpa;

import eu.wauz.wazera.model.entity.docs.FolderUserData;

public interface FolderUserDataJpaRepository {

	FolderUserData findByFolderAndUser(Integer folderId, String userName);

}
