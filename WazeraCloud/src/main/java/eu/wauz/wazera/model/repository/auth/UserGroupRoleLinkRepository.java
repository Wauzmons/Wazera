package eu.wauz.wazera.model.repository.auth;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.auth.UserGroupRoleLink;

public interface UserGroupRoleLinkRepository extends CrudRepository<UserGroupRoleLink, Integer> {

	UserGroupRoleLink findByUserIdAndGroupIdAndRoleId(Integer userId, Integer groupId, Integer roleId);

	List<UserGroupRoleLink> findByUserIdAndRoleId(Integer userId, Integer roleId);

}
