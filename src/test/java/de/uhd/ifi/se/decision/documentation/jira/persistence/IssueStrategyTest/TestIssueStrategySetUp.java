package de.uhd.ifi.se.decision.documentation.jira.persistence.IssueStrategyTest;

import org.junit.Before;

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.persistence.IssueStrategy;

public class TestIssueStrategySetUp extends TestSetUp{

	protected IssueStrategy issueStrategy;

	@Before
	public void setUp() {
		initialisation();
		issueStrategy=new IssueStrategy();
	}

}
