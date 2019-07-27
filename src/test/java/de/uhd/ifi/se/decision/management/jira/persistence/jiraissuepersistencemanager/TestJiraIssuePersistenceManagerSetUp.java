package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import org.junit.BeforeClass;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUser;

public abstract class TestJiraIssuePersistenceManagerSetUp extends TestSetUpWithIssues {

	protected static JiraIssuePersistenceManager issueStrategy;
	protected static int numberOfElements;
	protected static ApplicationUser user;
	protected static Link link;

	@BeforeClass
	public static void setUp() {
		initialization();
		issueStrategy = new JiraIssuePersistenceManager("TEST");
		numberOfElements = issueStrategy.getDecisionKnowledgeElements().size();
		user = JiraUser.SYS_ADMIN.getApplicationUser();
		link = new LinkImpl();
		link.setSourceElement(1, DocumentationLocation.JIRAISSUE);
		link.setDestinationElement(4, DocumentationLocation.JIRAISSUE);
	}
}
