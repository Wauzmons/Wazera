package eu.wauz.wazera.model.data.auth;

public class RolePermissionHandle {

	private PermissionData permission;

	private Boolean hasPermission;

	public PermissionData getPermission() {
		return permission;
	}

	public void setPermission(PermissionData permission) {
		this.permission = permission;
	}

	public Boolean getHasPermission() {
		return hasPermission;
	}

	public void setHasPermission(Boolean hasPermission) {
		this.hasPermission = hasPermission;
	}

}
