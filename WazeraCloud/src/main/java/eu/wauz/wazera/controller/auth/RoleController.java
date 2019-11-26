package eu.wauz.wazera.controller.auth;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.auth.PermissionScope;
import eu.wauz.wazera.model.data.auth.RoleData;
import eu.wauz.wazera.model.data.auth.RolePermissionHandle;
import eu.wauz.wazera.service.AuthDataService;

@Controller
@Scope("view")
public class RoleController {

	private List<RoleData> roles;

	private RoleData role;

	private List<RolePermissionHandle> rolePermissionHandles;

	@Autowired
	private AuthDataService authService;

	@PostConstruct
	private void init() {
		String roleIdString = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("roleId");
		if(StringUtils.isNotBlank(roleIdString))
			role = authService.findRoleById(Integer.parseInt(roleIdString));
		else
			role = new RoleData();
	}

	public String getEditRoleHeader() {
		return "Rolleneinstellungen <" + role.getName() + ">";
	}

	public String getDeleteRoleHeader() {
		return "Rolle <" + role.getName() + "> wirklich löschen?";
	}

	public List<PermissionScope> getPermissionScopes() {
		return Arrays.asList(PermissionScope.values());
	}

	public void showGrowlMessage(String title, String message) {
		FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        context.addMessage(null, new FacesMessage(title, message));
	}

	public void createNewRole() {
		if(StringUtils.isNotBlank(role.getName())) {
			authService.saveRole(role);
			showGrowlMessage("Gespeichert", "Rolle <" + role.getName() + "> wurde erfolgreich angelegt!");
			roles = null;
		}
		else {
			showGrowlMessage("Nicht gespeichert", "Rollenname darf nicht leer sein!");
		}
	}

	public void updateRole() {
		for(RolePermissionHandle rolePermissionHandle : rolePermissionHandles)
			if(!isRolePermissionHandleVisible(rolePermissionHandle))
				rolePermissionHandle.setHasPermission(false);
		authService.saveRole(role);
		authService.updateRolePermissions(role.getId(), rolePermissionHandles);
		showGrowlMessage("Gespeichert", "Rolle <" + role.getName() + "> wurde erfolgreich gespeichert!");
	}

	public void deleteRole() {
		authService.deleteRole(role.getId());
		showGrowlMessage("Gelöscht", "Rolle <" + role.getName() + "> wurde erfolgreich gelöscht!");
		setNewRole();
		roles = null;
	}



	public List<RoleData> getRoles() {
		if(roles == null)
			roles = authService.findAllRoles();
		return roles;
	}

	public void setRoles(List<RoleData> roles) {
		this.roles = roles;
	}

	public RoleData getRole() {
		return role;
	}

	public void setRole(RoleData role) {
		this.role = role;
	}

	public void setNewRole() {
		role = new RoleData();
	}

	public List<RolePermissionHandle> getRolePermissionHandles() {
		if(rolePermissionHandles == null)
			rolePermissionHandles = authService.getRolePermissions(role.getId());
		return rolePermissionHandles;
	}

	public void setRolePermissionHandles(List<RolePermissionHandle> rolePermissionHandles) {
		this.rolePermissionHandles = rolePermissionHandles;
	}

	public boolean isRolePermissionHandleVisible(RolePermissionHandle rolePermissionHandle) {
		return role.getScope() == rolePermissionHandle.getPermission().getScope().getId();
	}

}
