package eu.wauz.wazera.model.repository.auth;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.auth.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {

}
