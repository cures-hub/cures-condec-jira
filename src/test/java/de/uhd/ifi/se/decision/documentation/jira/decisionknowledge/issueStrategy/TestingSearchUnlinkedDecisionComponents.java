package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.issueStrategy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Tim Kuchenbuch
 */
public class TestingSearchUnlinkedDecisionComponents extends TestIssueStrategySetUp {

	@Test
	(expected = NullPointerException.class)
	public void testIdZeroProjectKeyNull() {
		issueStrategy.getUnlinkedDecisionComponents(0, null);
	}
	
//	@Test
//	public void testIdZeroProjectKeyFilled() {
//		String key = "TEST";
//		issueStrategy.getUnlinkedDecisionComponents(0, key);
//	}
	
	@Test
	(expected = NullPointerException.class)
	public void testIdFilledProjectKeyNull() {
		issueStrategy.getUnlinkedDecisionComponents(1, null);
	}
	
//	@Test
//	public void testIdFilledProjectKeyFilledNotEx() {
//		String key = "TESTNot";
//		assertNotNull(issueStrategy.getUnlinkedDecisionComponents(1, key));
//	}
	
	@Test
	public void testIdFilledProjectKeyFilledEx() {
		String key = "TEST";
		assertTrue(issueStrategy.getUnlinkedDecisionComponents(3, key).size()>0);
	}
}
