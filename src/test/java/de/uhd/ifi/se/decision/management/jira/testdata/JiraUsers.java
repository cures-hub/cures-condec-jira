package de.uhd.ifi.se.decision.management.jira.testdata;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

/**
 * Enum for the JIRA users used in the unit tests. There are two test users
 * called "SysAdmin" and "BlackHead". The "BlackHead" should not be granted
 * access to.
 */
public enum JiraUsers {
	SYS_ADMIN("SysAdmin"), BLACK_HEAD("BlackHead");

	private String name;

	private JiraUsers(String name) {
		this.name = name;
	}

	public ApplicationUser createApplicationUser() {
		return new MockApplicationUser(this.name);
	}

	public ApplicationUser getApplicationUser() {
		return ComponentAccessor.getUserManager().getUserByName(this.name);
	}

	public String getName() {
		return name;
	}

	public static JiraUsers valueOf(ApplicationUser applicationUser) {
		if (applicationUser == null) {
			return null;
		}
		for (JiraUsers jiraUser : values()) {
			if (applicationUser.getName().equals(jiraUser.getName())) {
				return jiraUser;
			}
		}
		return BLACK_HEAD;
	}
}
