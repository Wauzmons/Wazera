package eu.wauz.wazera.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.model.data.auth.GroupData;
import eu.wauz.wazera.model.data.auth.Permission;
import eu.wauz.wazera.model.data.auth.PermissionScope;
import eu.wauz.wazera.model.data.auth.RoleData;
import eu.wauz.wazera.model.data.auth.RolePermissionHandle;
import eu.wauz.wazera.model.data.auth.UserData;
import eu.wauz.wazera.model.data.auth.UserRoleHandle;
import eu.wauz.wazera.model.entity.auth.Group;
import eu.wauz.wazera.model.entity.auth.Role;
import eu.wauz.wazera.model.entity.auth.RolePermissionLink;
import eu.wauz.wazera.model.entity.auth.User;
import eu.wauz.wazera.model.entity.auth.UserGroupRoleLink;
import eu.wauz.wazera.model.repository.auth.AuthUserRepository;
import eu.wauz.wazera.model.repository.auth.GroupRepository;
import eu.wauz.wazera.model.repository.auth.RolePermissionLinkRepository;
import eu.wauz.wazera.model.repository.auth.RoleRepository;
import eu.wauz.wazera.model.repository.auth.UserGroupRoleLinkRepository;

@Service
@Scope("singleton")
public class AuthDataService {

    @Autowired
    private AuthUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRoleLinkRepository ugrlRepository;

    @Autowired
    private RolePermissionLinkRepository rplRepository;

	public List<UserData> findAllUsers() {
		List<UserData> result = new ArrayList<>();
		for(User user : userRepository.findAll()) {
			result.add(readUserData(user));
		}
		return result;
	}

	public UserData findUserByName(String username) {
		User user = userRepository.findByUsername(username);
		return readUserData(user);
	}

	public boolean authenticate(String username, String password) {
		User user = userRepository.findByUsername(username);
		if(user == null && StringUtils.equals(username, "root")) {
			user = new User();
			user.setUsername(username);
			user.setPassword(password);
			userRepository.save(user);
			return true;
		}
		else if(user != null && user.getPassword().equals(password)) {
			return true;
		}
		return false;
	}

	public boolean hasPermission(String username, Integer permissionId) {
		if(username.equals("root")) {
			return true;
		}
		
		User user = userRepository.findByUsername(username);
		Set<Integer> roleIds = rplRepository.findByPermissionId(permissionId).stream()
			.filter(rpLink -> rpLink.getEnabled())
			.map(rpLink -> rpLink.getRoleId())
			.collect(Collectors.toSet());

		for(Integer roleId : roleIds) {
			for(UserGroupRoleLink userGroupRoleLink : ugrlRepository.findByUserIdAndRoleId(user.getId(), roleId)) {
				if(userGroupRoleLink.getEnabled()) {
					return true;
				}
			}
		}
		return false;
	}

	public String validate(UserData twUserData) {
		if(StringUtils.isBlank(twUserData.getUsername())) {
			return "Username cannot be empty!";
		}
		User user = userRepository.findByUsername(twUserData.getUsername());
		if(user != null) {
			return "Username already exists!";
		}
		if(StringUtils.isBlank(twUserData.getPassword())) {
			return "Password cannot be empty!";
		}
		return "Success";
	}
	
	public void saveUser(UserData userData) {
		User user = findOrCreateUser(userData);
		userRepository.save(user);
	}

	public void deleteUser(int userId) {
		userRepository.deleteById(userId);
	}

	private UserData readUserData(User user) {
		UserData twUserData = new UserData();
		twUserData.setId(user.getId());
		twUserData.setUsername(user.getUsername());
		twUserData.setPassword(user.getPassword());
		return twUserData;
	}

	private User findOrCreateUser(UserData twUserData) {
		User user = null;
		if(twUserData.getId() != null) {
			user = userRepository.findById(twUserData.getId()).get();
		}
		if(user == null) {
			user = new User();
		}
		user.setId(twUserData.getId());
		user.setUsername(twUserData.getUsername());
		user.setPassword(twUserData.getPassword());
		return user;
	}

	public List<UserRoleHandle> getUserRoles(Integer userId) {
		List<UserRoleHandle> result = new ArrayList<>();

		Iterable<Role> roles = roleRepository.findAll();
		Iterable<Group> groups = groupRepository.findAll();

		for(Role role : roles) {
			UserRoleHandle userRoleHandle = new UserRoleHandle();
			userRoleHandle.setRole(readRoleData(role));

			UserGroupRoleLink userGroupRoleLink = ugrlRepository.findByUserIdAndGroupIdAndRoleId(userId, -1, role.getId());
			userRoleHandle.setHasRoleGlobally(userGroupRoleLink != null ? userGroupRoleLink.getEnabled() : false);

			if(role.getScope() == PermissionScope.GROUP.getId()) {
				for(Group group : groups) {
					userGroupRoleLink = ugrlRepository.findByUserIdAndGroupIdAndRoleId(userId, group.getId(), role.getId());
					userRoleHandle.setHasRoleInGroup(group.getId(), userGroupRoleLink != null ? userGroupRoleLink.getEnabled() : false);
				}
			}
			result.add(userRoleHandle);
		}
		return result;
	}

	public void updateUserRoles(int userId, List<UserRoleHandle> userRoleHandles) {
		Iterable<Group> groups = groupRepository.findAll();
		for(UserRoleHandle userRoleHandle : userRoleHandles) {
			Integer roleId = userRoleHandle.getRole().getId();
			saveUserGroupRoleLink(userId, -1, roleId, userRoleHandle.getHasRoleGlobally());

			if(!userRoleHandle.isRoleGlobal()) {
				for(Group group : groups) {
					saveUserGroupRoleLink(userId, group.getId(), roleId, userRoleHandle.getHasRoleInGroup(group.getId()));
				}
			}
		}
	}

	private void saveUserGroupRoleLink(Integer userId, Integer groupId, Integer roleId, Boolean enabled) {
		UserGroupRoleLink userGroupRoleLink = ugrlRepository.findByUserIdAndGroupIdAndRoleId(userId, groupId, roleId);
		if(userGroupRoleLink == null) {
			userGroupRoleLink = new UserGroupRoleLink();
			userGroupRoleLink.setUserId(userId);
			userGroupRoleLink.setGroupId(groupId);
			userGroupRoleLink.setRoleId(roleId);
		}
		userGroupRoleLink.setEnabled(enabled != null ? enabled : false);
		ugrlRepository.save(userGroupRoleLink);
	}

	public List<RoleData> findAllRoles() {
		List<RoleData> result = new ArrayList<>();
		for(Role role : roleRepository.findAll()) {
			result.add(readRoleData(role));
		}
		return result;
	}

	public RoleData findRoleById(int roleId) {
		Role role = roleRepository.findById(roleId).get();
		return readRoleData(role);
	}

	public void saveRole(RoleData twRoleData) {
		Role role = findOrCreateRole(twRoleData);
		roleRepository.save(role);
	}

	public void deleteRole(int roleId) {
		roleRepository.deleteById(roleId);
	}

	private RoleData readRoleData(Role role) {
		RoleData twRoleData = new RoleData();
		twRoleData.setId(role.getId());
		twRoleData.setName(role.getName());
		twRoleData.setScope(role.getScope());
		return twRoleData;
	}

	private Role findOrCreateRole(RoleData twRoleData) {
		Role role = null;
		if(twRoleData.getId() != null) {
			role = roleRepository.findById(twRoleData.getId()).get();
		}
		if(role == null) {
			role = new Role();
		}
		role.setId(twRoleData.getId());
		role.setName(twRoleData.getName());
		role.setScope(twRoleData.getScope());
		return role;
	}

	public List<RolePermissionHandle> getRolePermissions(int roleId) {
		List<RolePermissionHandle> result = new ArrayList<>();
		List<Permission> permissions = Arrays.asList(Permission.values());
		
		for(Permission permission : permissions) {
			RolePermissionHandle rolePermissionHandle = new RolePermissionHandle();
			rolePermissionHandle.setPermission(permission);

			RolePermissionLink rolePermissionLink = rplRepository.findByRoleIdAndPermissionId(roleId, permission.getId());
			rolePermissionHandle.setHasPermission(rolePermissionLink != null ? rolePermissionLink.getEnabled() : false);

			result.add(rolePermissionHandle);
		}
		return result;
	}

	public void updateRolePermissions(int roleId, List<RolePermissionHandle> rolePermissionHandles) {
		for(RolePermissionHandle rolePermissionHandle : rolePermissionHandles) {
			RolePermissionLink rolePermissionLink = rplRepository.findByRoleIdAndPermissionId(roleId, rolePermissionHandle.getPermission().getId());
			if(rolePermissionLink == null) {
				rolePermissionLink = new RolePermissionLink();
				rolePermissionLink.setRoleId(roleId);
				rolePermissionLink.setPermissionId(rolePermissionHandle.getPermission().getId());
			}
			rolePermissionLink.setEnabled(rolePermissionHandle.getHasPermission());
			rplRepository.save(rolePermissionLink);
		}
	}

	public List<GroupData> findAllGroups() {
		List<GroupData> result = new ArrayList<>();
		for(Group group : groupRepository.findAll()) {
			result.add(readGroupData(group));
		}
		return result;
	}

	public GroupData findGroupById(int groupId) {
		Group group = groupRepository.findById(groupId).get();
		return readGroupData(group);
	}

	public void saveGroup(GroupData twGroupData) {
		Group group = findOrCreateGroup(twGroupData);
		groupRepository.save(group);
	}

	public void deleteGroup(int groupId) {
		groupRepository.deleteById(groupId);
	}

	private GroupData readGroupData(Group group) {
		GroupData twGroupData = new GroupData();
		twGroupData.setId(group.getId());
		twGroupData.setName(group.getName());
		return twGroupData;
	}

	private Group findOrCreateGroup(GroupData twGroupData) {
		Group group = null;
		if(twGroupData.getId() != null) {
			group = groupRepository.findById(twGroupData.getId()).get();
		}
		if(group == null) {
			group = new Group();
		}
		group.setId(twGroupData.getId());
		group.setName(twGroupData.getName());
		return group;
	}

}
