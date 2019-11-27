package eu.wauz.wazera.model.data.auth;

public enum Permission {
	
	ADMINISTRATE_ACCOUNTS(100001, PermissionScope.GLOBAL);
	
	private final int id;
	
	private final PermissionScope scope;
	
	private Permission(int id, PermissionScope scope) {
		this.id = id;
		this.scope = scope;
	}

	public int getId() {
		return id;
	}
	
	public PermissionScope getScope() {
		return scope;
	}
	
	@Override
	public String toString() {
		return this.name();
	}

}
