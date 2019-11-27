package eu.wauz.wazera.model.data.auth;

public enum PermissionScope {

	GLOBAL(1),

	GROUP(2);

	private final int id;

	PermissionScope(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return this.name();
	}

}
