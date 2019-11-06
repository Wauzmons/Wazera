package eu.wauz.wazera.model.repository.jpa;

import java.util.List;

import eu.wauz.wazera.model.entity.Folder;

public interface FolderJpaRepository {

	List<Folder> findByTags(List<String> searchTokens);

}
