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
	public void testIdZeroProjektKeyNull() {
		issueStrategy.getUnlinkedDecisionComponents(0, null);
	}

	@Test
	public void testIdZeroProjektKeyFilled() {
		String key = "TEST";
		issueStrategy.getUnlinkedDecisionComponents(0, key);
	}

	@Test
	(expected = NullPointerException.class)
	public void testIdFilledProjektKeyNull() {
		issueStrategy.getUnlinkedDecisionComponents(1, null);
	}

	@Test
	public void testIdFilledProjektKeyFilledNotEx() {
		String key = "TESTNot";
		assertNotNull(issueStrategy.getUnlinkedDecisionComponents(1, key));
	}

	@Test
	public void testIdFilledProjektKeyFilledEx() {
		String key = "TEST";
		assertTrue(issueStrategy.getUnlinkedDecisionComponents(3, key).size()>0);
	}
}
