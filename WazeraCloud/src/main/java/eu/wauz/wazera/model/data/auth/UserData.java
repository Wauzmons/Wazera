package eu.wauz.wazera.model.data.auth;

import java.io.Serializable;

public class UserData implements Serializable {

	private static final long serialVersionUID = -4321214393671480406L;

	private Integer id;

	private String username;

	private String password;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String toString() {
		return "UserData(" + (id != null ? id : "new") + ")";
	}
	
}
