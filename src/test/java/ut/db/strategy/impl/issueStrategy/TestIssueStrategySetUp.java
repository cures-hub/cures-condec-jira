package ut.db.strategy.impl.issueStrategy;

import org.junit.Before;

import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;

import ut.testsetup.TestSetUp;

public class TestIssueStrategySetUp extends TestSetUp{

	protected IssueStrategy issueStrategy;
	
	@Before
	public void setUp() {
		initialisation();
		issueStrategy=new IssueStrategy();
	}
	
}
