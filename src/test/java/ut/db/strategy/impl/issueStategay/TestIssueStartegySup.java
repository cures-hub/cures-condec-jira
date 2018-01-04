package ut.db.strategy.impl.issueStategay;

import org.junit.Before;

import com.atlassian.DecisionDocumentation.db.strategy.impl.IssueStrategy;

import ut.testsetup.TestSetUp;

public class TestIssueStartegySup extends TestSetUp{

	protected IssueStrategy issueStrat;
	
	@Before
	public void setUp() {
		initialisation();
		issueStrat=new IssueStrategy();
	}
	
}
