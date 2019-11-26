package eu.wauz.wazera.model.repository.docs.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import eu.wauz.wazera.model.QueryBuilder;
import eu.wauz.wazera.model.entity.docs.FolderUserData;

@Repository
public class FolderUserDataJpaRepositoryImpl implements FolderUserDataJpaRepository {

	@PersistenceContext
    private EntityManager em;

	@Override
	public FolderUserData findByFolderAndUser(Integer folderId, String userName) {
		QueryBuilder queryBuilder = new QueryBuilder("select distinct folderUserData from FolderUserData folderUserData");
		queryBuilder.addWhere("folderUserData.folderId = :folderId");
		queryBuilder.addWhere("folderUserData.userName = :userName");

		TypedQuery<FolderUserData> query = em.createQuery(queryBuilder.buildQuery(), FolderUserData.class);
		query.setParameter("folderId", folderId);
		query.setParameter("userName", userName);

		List<FolderUserData> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}

}
