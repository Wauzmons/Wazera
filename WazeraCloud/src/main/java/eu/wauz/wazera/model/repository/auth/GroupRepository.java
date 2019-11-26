package eu.wauz.wazera.model.repository.auth;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.auth.Group;

public interface GroupRepository extends CrudRepository<Group, Integer> {

}
