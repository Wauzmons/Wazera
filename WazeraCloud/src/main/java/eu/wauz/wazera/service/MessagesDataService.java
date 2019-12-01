package eu.wauz.wazera.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.model.data.MessageData;
import eu.wauz.wazera.model.entity.Message;
import eu.wauz.wazera.model.repository.MessageRepository;

@Service
@Scope("singleton")
public class MessagesDataService {
	
	@Autowired
	private MessageRepository messageRepository;
	
	public List<MessageData> getAllMessages() {
		List<MessageData> messages = new ArrayList<>();
		for(Message message : messageRepository.findAll()) {
			messages.add(readMessageData(message));
		}
		return messages;
	}
	
	public MessageData saveMessage(MessageData messageData) {
		Message message = new Message();
		if(messageData != null) {
			message.setId(messageData.getId());
			message.setSender(messageData.getSender());
			message.setText(messageData.getText());
			message.setTime(messageData.getTime());
			message = messageRepository.save(message);
		}
		return readMessageData(message);
	}
	
	public MessageData readMessageData(Message message) {
		MessageData messageData = new MessageData();
		if(message != null) {
			messageData.setId(message.getId());
			messageData.setSender(message.getSender());
			messageData.setText(message.getText());
			messageData.setTime(message.getTime());
		}
		return messageData;
	}

}
