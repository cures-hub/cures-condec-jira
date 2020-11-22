package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.jira.user.ApplicationUser;

public class MockPermissionManager extends com.atlassian.jira.mock.MockPermissionManager {

	@Override
	public boolean hasPermission(ProjectPermissionKey arg0, Project arg1, ApplicationUser arg2) {
		return true;
	}

}
