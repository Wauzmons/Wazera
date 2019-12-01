package eu.wauz.wazera.model.repository;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.Message;

public interface MessageRepository extends CrudRepository<Message, Integer> {

}
