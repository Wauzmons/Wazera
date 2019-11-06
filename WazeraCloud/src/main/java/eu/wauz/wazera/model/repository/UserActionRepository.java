package eu.wauz.wazera.model.repository;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.UserAction;

public interface UserActionRepository extends CrudRepository<UserAction, Integer> {

}
