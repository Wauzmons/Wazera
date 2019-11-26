package eu.wauz.wazera.model.data.auth;

import java.util.HashMap;
import java.util.Map;

public class UserRoleHandle {

	private RoleData role;

	private Boolean hasRoleGlobally;

	private Map<Integer, Boolean> hasRoleInGroupMap = new HashMap<>();

	public RoleData getRole() {
		return role;
	}

	public void setRole(RoleData role) {
		this.role = role;
	}

	public boolean isRoleGlobal() {
		return role.getScope() == PermissionScope.GLOBAL.getId();
	}

	public Boolean getHasRoleGlobally() {
		return hasRoleGlobally;
	}

	public void setHasRoleGlobally(Boolean hasRoleGlobally) {
		this.hasRoleGlobally = hasRoleGlobally;
	}

	public Map<Integer, Boolean> getHasRoleInGroupMap() {
		return hasRoleInGroupMap;
	}

	public void setHasRoleInGroupMap(Map<Integer, Boolean> hasRoleInGroupMap) {
		this.hasRoleInGroupMap = hasRoleInGroupMap;
	}

	public void setHasRoleInGroup(Integer groupId, Boolean hasRole) {
		hasRoleInGroupMap.put(groupId, hasRole);
	}

	public Boolean getHasRoleInGroup(Integer groupId) {
		return hasRoleInGroupMap.get(groupId);
	}

}
