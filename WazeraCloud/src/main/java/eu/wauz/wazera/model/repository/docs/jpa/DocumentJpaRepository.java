package eu.wauz.wazera.model.repository.docs.jpa;

import java.util.List;

import eu.wauz.wazera.model.entity.docs.Document;

public interface DocumentJpaRepository  {

	List<Document> findByTags(List<String> searchTokens);

}
