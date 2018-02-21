package ut.de.uhd.ifi.se.decdoc.jira.db.strategy.impl.issueStrategy;

import org.junit.Before;

import de.uhd.ifi.se.decdoc.jira.db.strategy.impl.IssueStrategy;
import ut.de.uhd.ifi.se.decdoc.jira.TestSetUp;

/**
 * @author Tim Kuchenbuch
 */
public class TestIssueStrategySetUp extends TestSetUp{

	protected IssueStrategy issueStrategy;
	
	@Before
	public void setUp() {
		initialisation();
		issueStrategy=new IssueStrategy();
	}
	
}
