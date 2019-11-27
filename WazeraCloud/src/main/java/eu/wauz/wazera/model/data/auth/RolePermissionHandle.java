package eu.wauz.wazera.model.data.auth;

public class RolePermissionHandle {

	private Permission permission;

	private Boolean hasPermission;

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public Boolean getHasPermission() {
		return hasPermission;
	}

	public void setHasPermission(Boolean hasPermission) {
		this.hasPermission = hasPermission;
	}

}
