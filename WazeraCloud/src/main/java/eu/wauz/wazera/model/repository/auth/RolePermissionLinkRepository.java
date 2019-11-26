package eu.wauz.wazera.model.repository.auth;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.auth.RolePermissionLink;

public interface RolePermissionLinkRepository extends CrudRepository<RolePermissionLink, Integer> {

	List<RolePermissionLink> findByPermissionId(int permissionId);

	RolePermissionLink findByRoleIdAndPermissionId(int roleId, int permissionId);

}
