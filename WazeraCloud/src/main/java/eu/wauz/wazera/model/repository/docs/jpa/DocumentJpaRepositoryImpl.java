package eu.wauz.wazera.model.repository.docs.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import eu.wauz.wazera.model.QueryBuilder;
import eu.wauz.wazera.model.entity.docs.Document;

@Repository
public class DocumentJpaRepositoryImpl implements DocumentJpaRepository {

    @PersistenceContext
    private EntityManager em;

	@Override
	public List<Document> findByTags(List<String> searchTokens) {
		QueryBuilder queryBuilder = new QueryBuilder("select distinct doc from Document doc");
		for (int i = 0; i < searchTokens.size(); i++) {
			queryBuilder.addWhere("(doc.content like :textSearchToken" + i + " or exists (select documentTag from DocumentTag documentTag where doc.id = documentTag.documentId and documentTag.value like :searchToken" + i + "))");
		}

		TypedQuery<Document> query = em.createQuery(queryBuilder.buildQuery(), Document.class);
		for (int i = 0; i < searchTokens.size(); i++) {
			query.setParameter("textSearchToken" + i, "%" + searchTokens.get(i) + "%");
			query.setParameter("searchToken" + i, "%" + searchTokens.get(i) + "%");
		}

		List<Document> resultList = query.getResultList();
		return resultList;
	}

}
