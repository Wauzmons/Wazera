package eu.wauz.wazera.model.data.auth;

public class PermissionData {

	private int id;

	private PermissionScope scope;

	private String name;

	private String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PermissionScope getScope() {
		return scope;
	}

	public void setScope(PermissionScope scope) {
		this.scope = scope;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
