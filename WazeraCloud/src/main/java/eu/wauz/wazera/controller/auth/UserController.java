package eu.wauz.wazera.controller.auth;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.auth.Permission;
import eu.wauz.wazera.model.data.auth.UserData;
import eu.wauz.wazera.model.data.auth.UserRoleHandle;
import eu.wauz.wazera.service.AuthDataService;
import eu.wauz.wazera.service.DocsTool;

@Controller
@Scope("view")
public class UserController {

	@Autowired
	private AuthDataService authService;

	private List<UserRoleHandle> userRoleHandles;
	
	private List<UserData> users;

	private UserData user;

	private String passwordInput1;

	private String passwordInput2;
	
	private DocsTool docsTool;

	@PostConstruct
	private void init() {
		this.docsTool = new DocsTool();

		String username = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("username");
		if(StringUtils.isNotBlank(username)) {
			user = authService.findUserByName(username);
		}
		else {
			this.user = new UserData();
			user.setId(0);
			user.setUsername("admin");
		}
	}

	public String getEditUserHeader() {
		return "Benutzereinstellungen <" + user.getUsername() + ">";
	}

	public String getDeleteUserHeader() {
		return "Benutzer <" + user.getUsername() + "> wirklich löschen?";
	}

	public void changePassword() {
		if(StringUtils.isBlank(passwordInput1)) {
			docsTool.showInfoMessage("Passwort kann nicht leer sein!");
			return;
		}
		if(!StringUtils.equals(passwordInput1, passwordInput2)) {
			docsTool.showInfoMessage("Passwörter stimmen nicht überein!");
			return;
		}
		user.setPassword(passwordInput1);
		authService.saveUser(user);
		docsTool.showInfoMessage("Das Passwort wurde erfolgreich gändert!");
	}

	public void createNewUser() {
		String validationMessage = authService.validate(user);
		if(validationMessage.equals("Success")) {
			authService.saveUser(user);
			docsTool.showInfoMessage("User <" + user.getUsername() + "> wurde erfolgreich angelegt!");
			users = null;
		}
		else {
			docsTool.showInfoMessage(validationMessage);
		}
	}

	public void deleteUser() {
		authService.deleteUser(user.getId());
		docsTool.showInfoMessage("User <" + user.getUsername() + "> wurde erfolgreich gelöscht!");
		setNewUser();
		users = null;
	}

	public List<UserData> getUsers() {
		if(users == null)
			users = authService.findAllUsers();
		return users;
	}

	public void setUsers(List<UserData> users) {
		this.users = users;
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public void setNewUser() {
		user = new UserData();
	}

	public String getPasswordInput1() {
		return passwordInput1;
	}

	public void setPasswordInput1(String passwordInput1) {
		this.passwordInput1 = passwordInput1;
	}

	public String getPasswordInput2() {
		return passwordInput2;
	}

	public void setPasswordInput2(String passwordInput2) {
		this.passwordInput2 = passwordInput2;
	}

	public List<UserRoleHandle> getUserRoleHandles() {
		if(userRoleHandles == null) {
			userRoleHandles = authService.getUserRoles(user.getId());
		}
		return userRoleHandles;
	}

	public void setUserRoleHandles(List<UserRoleHandle> userRoleHandles) {
		this.userRoleHandles = userRoleHandles;
	}
	
	public void updateRoles() {
		authService.updateUserRoles(user.getId(), userRoleHandles);
		docsTool.showInfoMessage("Die Rollen von '" + user.getUsername() + "' wurden gespeichert.");
	}

	public boolean isAuthAdmin() {
		return authService.hasPermission(docsTool.getUsername(), Permission.ADMINISTRATE_ACCOUNTS.getId());
	}

	public boolean mayChangePassword() {
		return docsTool.getUsername().equals(user.getUsername()) || isAuthAdmin();
	}

}
