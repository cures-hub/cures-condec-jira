package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import org.junit.Before;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;

public class TestJiraIssuePersistenceManagerSetUp extends TestSetUpWithIssues {

	protected JiraIssuePersistenceManager issueStrategy;
	protected int numberOfElements;
	protected ApplicationUser user;
	protected Link link;

	@Before
	public void setUp() {
		initialization();
		issueStrategy = new JiraIssuePersistenceManager("TEST");
		numberOfElements = issueStrategy.getDecisionKnowledgeElements().size();
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		link = new LinkImpl();
		link.setSourceElement(1, DocumentationLocation.JIRAISSUE);
		link.setDestinationElement(4, DocumentationLocation.JIRAISSUE);
	}
}
