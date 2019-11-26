package eu.wauz.wazera.model.repository.auth;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.auth.Permission;

public interface PermissionRepository extends CrudRepository<Permission, Integer> {

	Optional<Permission> findByName(String permissionNameLogin);

}
