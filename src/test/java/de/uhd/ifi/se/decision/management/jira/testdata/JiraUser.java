package de.uhd.ifi.se.decision.management.jira.testdata;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

/**
 * Enum for the JIRA users used in the unit tests.
 */
public enum JiraUser {
	SYS_ADMIN("SysAdmin"), BLACK_HEAD("BlackHead");

	private String name;

	private JiraUser(String name) {
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

	public static JiraUser valueOf(ApplicationUser applicationUser) {
		if (applicationUser == null) {
			return null;
		}
		for (JiraUser jiraUser : values()) {
			if (applicationUser.getName().equals(jiraUser.getName())) {
				return jiraUser;
			}
		}
		return BLACK_HEAD;
	}
}
