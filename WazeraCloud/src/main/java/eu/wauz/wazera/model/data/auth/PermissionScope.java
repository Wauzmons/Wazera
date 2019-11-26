package eu.wauz.wazera.model.data.auth;

public enum PermissionScope {

	GLOBAL(1, "GLOBAL"),

	GROUP(2, "GROUP");

	private int id;

	private String scopeString;

	PermissionScope(int id, String scopeString) {
		this.id = id;
		this.scopeString = scopeString;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return scopeString;
	}

}
