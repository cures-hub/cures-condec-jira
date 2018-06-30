package de.uhd.ifi.se.decision.documentation.jira.persistence.issuestrategy;

import de.uhd.ifi.se.decision.documentation.jira.persistence.IssueStrategy;
import org.junit.Before;

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;

public class TestIssueStrategySetUp extends TestSetUp{

	protected IssueStrategy issueStrategy;

	@Before
	public void setUp() {
		initialization();
		issueStrategy=new IssueStrategy();
	}

}
