package eu.wauz.wazera.model.repository.docs.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import eu.wauz.wazera.model.QueryBuilder;
import eu.wauz.wazera.model.entity.docs.Folder;

@Repository
public class FolderJpaRepositoryImpl implements FolderJpaRepository {

	@PersistenceContext
    private EntityManager em;

	@Override
	public List<Folder> findByTags(List<String> searchTokens) {
		QueryBuilder queryBuilder = new QueryBuilder("select distinct folder from Folder folder");
		for (int i = 0; i < searchTokens.size(); i++) {
			queryBuilder.addWhere("(folder.name like :searchToken" + i + ")");
		}

		TypedQuery<Folder> query = em.createQuery(queryBuilder.buildQuery(), Folder.class);
		for (int i = 0; i < searchTokens.size(); i++) {
			query.setParameter("searchToken" + i, "%" + searchTokens.get(i) + "%");
		}

		List<Folder> resultList = query.getResultList();
		return resultList;
	}

}
