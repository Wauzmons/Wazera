package eu.wauz.wazera.model.repository.docs;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.docs.DocumentTag;

public interface DocumentTagRepository extends CrudRepository<DocumentTag, Integer> {

	List<DocumentTag> findByDocumentId(Integer documentId);

}
