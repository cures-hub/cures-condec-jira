package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;

public class MockJiraHelper extends JiraHelper {
	@Override
	public Project getProject() {
		return JiraProjects.getTestProject();
	}
}
