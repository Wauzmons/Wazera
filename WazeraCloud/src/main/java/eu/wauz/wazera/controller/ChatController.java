package eu.wauz.wazera.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.MessageData;
import eu.wauz.wazera.service.DocsTool;
import eu.wauz.wazera.service.MessagesDataService;

@Controller
@Scope("view")
public class ChatController implements Serializable {
	
	private static final long serialVersionUID = 7995872877463798362L;

	@Autowired
	MessagesDataService messagesService;
	
	private String inputMessage;
	
	private DocsTool docsTool;
	
	public ChatController() {
		docsTool = new DocsTool();
	}
	
	public List<MessageData> getMessages() {
		return messagesService.getAllMessages();
	}
	
	public void sendMessage() {
		if(StringUtils.isNoneBlank(inputMessage)) {
			MessageData message = new MessageData();
			message.setSender(docsTool.getUsername());
			message.setText(inputMessage);
			message.setTime(new Date());
			messagesService.saveMessage(message);
			inputMessage = "";
		}
	}

	public String getInputMessage() {
		return inputMessage;
	}

	public void setInputMessage(String inputMessage) {
		this.inputMessage = inputMessage;
	}

}
