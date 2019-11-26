package eu.wauz.wazera.model.repository.docs.jpa;

import java.util.List;

import eu.wauz.wazera.model.entity.docs.Folder;

public interface FolderJpaRepository {

	List<Folder> findByTags(List<String> searchTokens);

}
