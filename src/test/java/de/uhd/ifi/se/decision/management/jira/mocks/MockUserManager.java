package de.uhd.ifi.se.decision.management.jira.mocks;

import java.net.URI;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.user.UserResolutionException;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

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
		return JiraUsers.SYS_ADMIN.getName().equals(username);
	}

	@Override
	public boolean isUserInGroup(String arg0, String arg1) {
		return false;
	}

	@Override
	public Principal resolve(String arg0) throws UserResolutionException {
		return null;
	}

	@Override
	public UserProfile getRemoteUser() {
		return null;
	}

	@Override
	public UserKey getRemoteUserKey() {
		return null;
	}

	@Override
	public UserProfile getRemoteUser(HttpServletRequest request) {
		return new UserProfile() {

			@Override
			public UserKey getUserKey() {
				String username = getRemoteUsername(request);
				if (username == null) {
					username = JiraUsers.BLACK_HEAD.getName();
				}
				return new UserKey(username);
			}

			@Override
			public String getUsername() {
				return getRemoteUsername(request);
			}

			@Override
			public String getFullName() {
				return null;
			}

			@Override
			public String getEmail() {
				return null;
			}

			@Override
			public URI getProfilePictureUri(int width, int height) {
				return null;
			}

			@Override
			public URI getProfilePictureUri() {
				return null;
			}

			@Override
			public URI getProfilePageUri() {
				return null;
			}
		};
	}

	@Override
	public UserKey getRemoteUserKey(HttpServletRequest request) {
		return null;
	}

	@Override
	public UserProfile getUserProfile(UserKey userKey) {
		return null;
	}

	@Override
	public boolean isUserInGroup(UserKey userKey, String group) {
		return false;
	}

	@Override
	public boolean isSystemAdmin(UserKey userKey) {
		return userKey.getStringValue().equals(JiraUsers.SYS_ADMIN.getName());
	}

	@Override
	public boolean isAdmin(UserKey userKey) {
		return userKey.getStringValue().equals(JiraUsers.SYS_ADMIN.getName());
	}

	@Override
	public Iterable<String> findGroupNamesByPrefix(String prefix, int startIndex, int maxResults) {
		return null;
	}
}
