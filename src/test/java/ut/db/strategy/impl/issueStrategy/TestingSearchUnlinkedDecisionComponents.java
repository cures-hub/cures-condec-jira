package ut.db.strategy.impl.issueStrategy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Tim Kuchenbuch
 */
public class TestingSearchUnlinkedDecisionComponents extends TestIssueStrategySetUp {

	@Test
	(expected = NullPointerException.class)
	public void testIdZeroProjektKeyNull() {
		issueStrategy.searchUnlinkedDecisionComponents(0, null);
	}
	
	@Test
	public void testIdZeroProjektKeyFilled() {
		String key = "TEST";
		issueStrategy.searchUnlinkedDecisionComponents(0, key);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testIdFilledProjektKeyNull() {
		issueStrategy.searchUnlinkedDecisionComponents(1, null);
	}
	
	@Test
	public void testIdFilledProjektKeyFilledNotEx() {
		String key = "TESTNot";
		assertNotNull(issueStrategy.searchUnlinkedDecisionComponents(1, key));
	}
	
	@Test
	public void testIdFilledProjektKeyFilledEx() {
		String key = "TEST";
		assertTrue(issueStrategy.searchUnlinkedDecisionComponents(3, key).size()>0);
	}
}
