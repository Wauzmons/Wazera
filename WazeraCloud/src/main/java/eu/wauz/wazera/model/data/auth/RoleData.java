package eu.wauz.wazera.model.data.auth;

public class RoleData {

	private Integer id;

	private String name;

	private int scope;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		return "Role(" + (getId() != null ? String.valueOf(getId()) : "transient") + ")";
	}

}
