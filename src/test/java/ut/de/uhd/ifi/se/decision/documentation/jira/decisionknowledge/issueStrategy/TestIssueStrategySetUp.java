package ut.de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.issueStrategy;

import org.junit.Before;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.IssueStrategy;
import ut.TestSetUp;

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
