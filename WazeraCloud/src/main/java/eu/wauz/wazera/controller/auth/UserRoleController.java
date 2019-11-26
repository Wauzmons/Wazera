package eu.wauz.wazera.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.service.AuthDataService;

@Controller
@Scope("view")
public class UserRoleController {

	@Autowired
	private AuthDataService authService;

//	private UserData user;

//	private List<UserRoleHandle> userRoleHandles;
//
//	@PostConstruct
//	private void init() {
//		userFormController.registerUserFormExtensionController(this);
//	}
//
//	public void postSave(UserData savedUser) {
//		authService.updateUserRoles(savedUser.getId(), userRoleHandles);
//		showGrowlMessage("Gespeichert", "Die Rollen von '" + savedUser.getUsername() + "' wurden gespeichert.");
//	}
//
//	public List<UserRoleHandle> getUserRoleHandles() {
//		if(userRoleHandles == null) {
//			Integer userId = userFormController.getUserId();
//			userRoleHandles = authService.getUserRoles(userId);
//		}
//		return userRoleHandles;
//	}
//
//	public void setUserRoleHandles(List<UserRoleHandle> userRoleHandles) {
//		this.userRoleHandles = userRoleHandles;
//	}
//
//	public void showGrowlMessage(String title, String message) {
//		FacesContext context = FacesContext.getCurrentInstance();
//        context.getExternalContext().getFlash().setKeepMessages(true);
//        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, title, message));
//	}





//
//
//
//
//
//	private List<UserData> users;
////
////	private UserData user;
//
//	private String passwordInput1;
//
//	private String passwordInput2;
//
//
//	public String getEditUserHeader() {
//		return "Benutzereinstellungen <" + user.getUsername() + ">";
//	}
//
//	public String getDeleteUserHeader() {
//		return "Benutzer <" + user.getUsername() + "> wirklich löschen?";
//	}
//
//	public String getUsername() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		return authentication.getName();
//	}
//
//	public void changePassword() {
//		if(StringUtils.isBlank(passwordInput1)) {
//			showGrowlMessage("Nicht gespeichert", "Passwort kann nicht leer sein!");
//			return;
//		}
//		if(!StringUtils.equals(passwordInput1, passwordInput2)) {
//			showGrowlMessage("Nicht gespeichert", "Passwörter stimmen nicht überein!");
//			return;
//		}
//		user.setPassword(passwordInput1);
//		authServiceProxy.saveUser(user);
//		showGrowlMessage("Gespeichert", "Das Passwort wurde erfolgreich gändert!");
//	}
//
//	public void createNewUser() {
//		String validationMessage = authServiceProxy.validate(user);
//		if(validationMessage.equals("Success")) {
//			authServiceProxy.saveUser(user);
//			showGrowlMessage("Gespeichert", "User <" + user.getUsername() + "> wurde erfolgreich angelegt!");
//			users = null;
//		}
//		else {
//			showGrowlMessage("Nicht gespeichert", validationMessage);
//		}
//	}
//
//	public void deleteUser() {
//		authServiceProxy.deleteUser(user.getId());
//		showGrowlMessage("Gelöscht", "User <" + user.getUsername() + "> wurde erfolgreich gelöscht!");
//		setNewUser();
//		users = null;
//	}
//
//
//
//	public List<UserData> getUsers() {
//		if(users == null)
//			users = authServiceProxy.findAllUsers();
//		return users;
//	}
//
//	public void setUsers(List<UserData> users) {
//		this.users = users;
//	}
//
//	public UserData getUser() {
//		return user;
//	}
//
//	public void setUser(UserData user) {
//		this.user = user;
//	}
//
//	public void setNewUser() {
//		user = new UserData();
//	}
//
//	public String getPasswordInput1() {
//		return passwordInput1;
//	}
//
//	public void setPasswordInput1(String passwordInput1) {
//		this.passwordInput1 = passwordInput1;
//	}
//
//	public String getPasswordInput2() {
//		return passwordInput2;
//	}
//
//	public void setPasswordInput2(String passwordInput2) {
//		this.passwordInput2 = passwordInput2;
//	}
//
//	public List<UserRoleHandle> getUserRoleHandles() {
//		if(userRoleHandles == null)
//			userRoleHandles = authServiceProxy.getUserRoles(user.getId());
//		return userRoleHandles;
//	}
//
//	public void setUserRoleHandles(List<UserRoleHandle> userRoleHandles) {
//		this.userRoleHandles = userRoleHandles;
//	}
//
//
//
//	public boolean isAuthAdmin() {
//		// TODO
////		return authServiceProxy.authorize(getUsername(), PermissionData.ADMINISTRATE_AUTH.getPermissionId());
//		return true;
//	}
//
//	public boolean mayChangePassword() {
//		return getUsername().equals(user.getUsername()) || isAuthAdmin();
//	}


}
