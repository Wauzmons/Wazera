package eu.wauz.wazera.controller.auth;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.auth.PermissionScope;
import eu.wauz.wazera.model.data.auth.RoleData;
import eu.wauz.wazera.model.data.auth.RolePermissionHandle;
import eu.wauz.wazera.service.AuthDataService;
import eu.wauz.wazera.service.DocsTool;

@Controller
@Scope("view")
public class RoleController implements Serializable {

	private static final long serialVersionUID = 3277213100357879191L;
	
	@Autowired
	private AuthDataService authService;

	private List<RoleData> roles;

	private RoleData role;

	private List<RolePermissionHandle> rolePermissionHandles;
	
	private DocsTool docsTool;

	@PostConstruct
	private void init() {
		docsTool = new DocsTool();
		
		String roleIdString = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("roleId");
		if(StringUtils.isNotBlank(roleIdString)) {
			role = authService.findRoleById(Integer.parseInt(roleIdString));
		}
		else {
			role = new RoleData();
		}
	}

	public String getEditRoleHeader() {
		return "Role Properties <" + role.getName() + ">";
	}

	public String getDeleteRoleHeader() {
		return "Delete <" + role.getName() + "> permamently?";
	}

	public List<PermissionScope> getPermissionScopes() {
		return Arrays.asList(PermissionScope.values());
	}

	public void createNewRole() {
		if(StringUtils.isNotBlank(role.getName())) {
			authService.saveRole(role);
			docsTool.showInfoMessage("Role <" + role.getName() + "> was successfully created!");
			roles = null;
		}
		else {
			docsTool.showInfoMessage("Role Name cannot be empty!");
		}
	}

	public void updateRole() {
		for(RolePermissionHandle rolePermissionHandle : rolePermissionHandles) {
			if(!isRolePermissionHandleVisible(rolePermissionHandle)) {
				rolePermissionHandle.setHasPermission(false);
			}
		}
		authService.saveRole(role);
		authService.updateRolePermissions(role.getId(), rolePermissionHandles);
		docsTool.showInfoMessage("Role <" + role.getName() + "> was successfully updated!");
	}

	public void deleteRole() {
		authService.deleteRole(role.getId());
		docsTool.showInfoMessage("Role <" + role.getName() + "> was successfully deleted!");
		setNewRole();
		roles = null;
	}

	public List<RoleData> getRoles() {
		if(roles == null) {
			roles = authService.findAllRoles();
		}
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
		if(rolePermissionHandles == null) {
			rolePermissionHandles = authService.getRolePermissions(role.getId());
		}
		return rolePermissionHandles;
	}

	public void setRolePermissionHandles(List<RolePermissionHandle> rolePermissionHandles) {
		this.rolePermissionHandles = rolePermissionHandles;
	}

	public boolean isRolePermissionHandleVisible(RolePermissionHandle rolePermissionHandle) {
		return role.getScope() == rolePermissionHandle.getPermission().getScope().getId();
	}

}
