package de.uhd.ifi.se.decision.management.jira.rest.consistencyrest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.rest.ConsistencyRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class TestConsistencyRestSuper extends TestSetUp {
	protected HttpServletRequest request;
	protected ConsistencyRest consistencyRest;
	protected List<Issue> issues;
	protected Project project;

	@Before
	public void setUp() {
		init();
		project = JiraProjects.getTestProject();

		consistencyRest = new ConsistencyRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
		issues = JiraIssues.getTestJiraIssues();
	}

}
