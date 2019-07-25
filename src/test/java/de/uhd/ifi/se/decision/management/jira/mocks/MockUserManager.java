package de.uhd.ifi.se.decision.management.jira.mocks;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.user.UserResolutionException;

/**
 * Mocks the JIRA user manager and adds mock users. This is a different user
 * manager than provided by the ComponentAccessor.getUserManager().
 */
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
		Object userObject = request.getAttribute("user");
		if (userObject == null || !(userObject instanceof ApplicationUser)) {
			return null;
		}
		ApplicationUser user = (MockApplicationUser) userObject;
		return user.getUsername();
	}

	@Override
	public UserProfile getUserProfile(String arg0) {
		return null;
	}

	@Override
	public boolean isAdmin(String username) {
		return isSystemAdmin(username);
	}

	@Override
	public boolean isSystemAdmin(String username) {
		return "SysAdmin".equals(username);
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
