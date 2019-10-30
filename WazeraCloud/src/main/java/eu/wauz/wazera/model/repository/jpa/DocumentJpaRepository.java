package eu.wauz.wazera.model.repository.jpa;

import java.util.List;

import eu.wauz.wazera.model.entity.Document;

public interface DocumentJpaRepository  {

	List<Document> findByTags(List<String> searchTokens);

}
