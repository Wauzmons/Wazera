package eu.wauz.wazera.controller.auth;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.auth.GroupData;
import eu.wauz.wazera.service.AuthDataService;

@Controller
@Scope("view")
public class GroupController {

	private List<GroupData> groups;

	private GroupData group;

	@Autowired
	private AuthDataService authService;

	@PostConstruct
	private void init() {
		group = new GroupData();
	}

	public String getEditGroupHeader() {
		return "Gruppeneinstellungen <" + group.getName() + ">";
	}

	public String getDeleteGroupHeader() {
		return "Gruppe <" + group.getName() + "> wirklich löschen?";
	}

	public void showGrowlMessage(String title, String message) {
		FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(title, message));
	}

	public void createNewGroup() {
		if(StringUtils.isNotBlank(group.getName())) {
			authService.saveGroup(group);
			showGrowlMessage("Gespeichert", "Gruppe <" + group.getName() + "> wurde erfolgreich angelegt!");
			groups = null;
		}
		else {
			showGrowlMessage("Nicht gespeichert", "Gruppenname darf nicht leer sein!");
		}
	}

	public void updateGroup() {
		if(StringUtils.isNotBlank(group.getName())) {
			authService.saveGroup(group);
			showGrowlMessage("Gespeichert", "Gruppe <" + group.getName() + "> wurde erfolgreich gespeichert!");
			groups = null;
		}
		else {
			showGrowlMessage("Nicht gespeichert", "Gruppenname darf nicht leer sein!");
		}
	}

	public void deleteGroup() {
		authService.deleteGroup(group.getId());
		showGrowlMessage("Gelöscht", "Gruppe <" + group.getName() + "> wurde erfolgreich gelöscht!");
		setNewGroup();
		groups = null;
	}

	public List<GroupData> getGroups() {
		if(groups == null)
			groups = authService.findAllGroups();
		return groups;
	}

	public void setGroups(List<GroupData> groups) {
		this.groups = groups;
	}

	public GroupData getGroup() {
		return group;
	}

	public void setGroup(GroupData group) {
		this.group = group;
	}

	public void setNewGroup() {
		group = new GroupData();
	}

}
