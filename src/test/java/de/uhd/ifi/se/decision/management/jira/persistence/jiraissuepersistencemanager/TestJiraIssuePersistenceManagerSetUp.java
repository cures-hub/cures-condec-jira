package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import org.junit.Before;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistenceManager;

public class TestJiraIssuePersistenceManagerSetUp extends TestSetUpWithIssues {

	protected JiraIssuePersistenceManager issueStrategy;
	protected int numberOfElements;

	@Before
	public void setUp() {
		initialization();
		issueStrategy = new JiraIssuePersistenceManager("TEST");
		numberOfElements = issueStrategy.getDecisionKnowledgeElements().size();
	}
}
