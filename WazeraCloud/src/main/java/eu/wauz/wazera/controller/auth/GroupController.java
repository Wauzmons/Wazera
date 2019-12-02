package eu.wauz.wazera.controller.auth;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.auth.GroupData;
import eu.wauz.wazera.service.AuthDataService;
import eu.wauz.wazera.service.DocsTool;

@Controller
@Scope("view")
public class GroupController implements Serializable {

	private static final long serialVersionUID = 4720610118791412634L;
	
	@Autowired
	private AuthDataService authService;

	private List<GroupData> groups;

	private GroupData group;
	
	private DocsTool docsTool;

	@PostConstruct
	private void init() {
		docsTool = new DocsTool();
		group = new GroupData();
	}

	public String getEditGroupHeader() {
		return "Group Properties <" + group.getName() + ">";
	}

	public String getDeleteGroupHeader() {
		return "Delete <" + group.getName() + "> permanently?";
	}

	public void createNewGroup() {
		if(StringUtils.isNotBlank(group.getName())) {
			authService.saveGroup(group);
			docsTool.showInfoMessage("Group <" + group.getName() + "> was successfully created!");
			groups = null;
		}
		else {
			docsTool.showInfoMessage("Group Name cannot be empty!");
		}
	}

	public void updateGroup() {
		if(StringUtils.isNotBlank(group.getName())) {
			authService.saveGroup(group);
			docsTool.showInfoMessage("Group <" + group.getName() + "> was successfully updated!");
			groups = null;
		}
		else {
			docsTool.showInfoMessage("Group Name cannot be empty!");
		}
	}

	public void deleteGroup() {
		authService.deleteGroup(group.getId());
		docsTool.showInfoMessage("Gruppe <" + group.getName() + "> was successfully deleted!");
		setNewGroup();
		groups = null;
	}

	public List<GroupData> getGroups() {
		if(groups == null) {
			groups = authService.findAllGroups();
		}
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
