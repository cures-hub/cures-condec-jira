package de.uhd.ifi.se.decision.management.jira.mocks;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.user.UserResolutionException;

public class MockUserManager implements UserManager {

	@Override
	public boolean authenticate(String arg0, String arg1) {
		return false;
	}

	@Override
	public String getRemoteUsername() {
		return null;
	}

	@Override
	public String getRemoteUsername(HttpServletRequest request) {
		if (request.getAttribute("WithFails") != null && (boolean) request.getAttribute("WithFails")) {
			return "WithFails";
		}
		if (request.getAttribute("NoFails") != null && (boolean) request.getAttribute("NoFails")) {
			return "NoFails";
		}
		if (request.getAttribute("NoSysAdmin") != null && (boolean) request.getAttribute("NoSysAdmin")) {
			return "NoSysAdmin";
		}
		if (request.getAttribute("SysAdmin") != null && (boolean) request.getAttribute("SysAdmin")) {
			return "SysAdmin";
		}
		return null;
	}

	@Override
	public UserProfile getUserProfile(String arg0) {
		return null;
	}

	@Override
	public boolean isAdmin(String arg0) {
		return false;
	}

	@Override
	public boolean isSystemAdmin(String username) {
		return "SysAdmin".equals(username) || "NoFails".equals(username);
	}

	@Override
	public boolean isUserInGroup(String arg0, String arg1) {
		return false;
	}

	@Override
	public Principal resolve(String arg0) throws UserResolutionException {
		return null;
	}
}
