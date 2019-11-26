package eu.wauz.wazera.model.data.auth;

public class GroupData {

	private Integer id;

	private String name;

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

	@Override
	public String toString() {
		return "Group(" + (getId() != null ? String.valueOf(getId()) : "transient") + ")";
	}

}
