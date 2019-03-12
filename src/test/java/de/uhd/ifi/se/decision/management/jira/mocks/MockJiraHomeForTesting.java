package de.uhd.ifi.se.decision.management.jira.mocks;

import java.io.File;

import com.atlassian.jira.config.util.JiraHome;
import com.atlassian.jira.config.util.MockJiraHome;

public class MockJiraHomeForTesting extends MockJiraHome implements JiraHome {

	@Override
	public File getHome() {
		return new File(System.getProperty("user.home"));
	}

	@Override
	public File getLocalHome() {
		return new File(System.getProperty("user.home"));
	}
}
