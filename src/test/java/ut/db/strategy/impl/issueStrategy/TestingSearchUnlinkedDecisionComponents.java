package ut.db.strategy.impl.issueStategay;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestingSearchUnlinkedDecisionComponents extends TestIssueStartegySup {

	@Test
	(expected = NullPointerException.class)
	public void testIdZeroProjektKeyNull() {
		issueStrat.searchUnlinkedDecisionComponents(0, null);
	}
	
	@Test
	public void testIdZeroProjektKeyFilled() {
		String key = "TEST";
		issueStrat.searchUnlinkedDecisionComponents(0, key);
	}
	
	@Test
	(expected = NullPointerException.class)
	public void testIdFilledProjektKeyNull() {
		issueStrat.searchUnlinkedDecisionComponents(1, null);
	}
	
	@Test
	public void testIdFilledProjektKeyFilledNotEx() {
		String key = "TESTNot";
		assertNotNull(issueStrat.searchUnlinkedDecisionComponents(1, key));
	}
	
	@Test
	public void testIdFilledProjektKeyFilledEx() {
		String key = "TEST";
		assertTrue(issueStrat.searchUnlinkedDecisionComponents(3, key).size()>0);
	}
}
