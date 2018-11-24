package de.uhd.ifi.se.decision.management.jira.persistence.issuestrategy;

import org.junit.Before;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssuePersistence;

public class TestIssueStrategySetUp extends TestSetUpWithIssues {

	protected JiraIssuePersistence issueStrategy;
	protected int numberOfElements;

	@Before
	public void setUp() {
		initialization();
		issueStrategy = new JiraIssuePersistence("TEST");
		numberOfElements = issueStrategy.getDecisionKnowledgeElements().size();
	}
}
