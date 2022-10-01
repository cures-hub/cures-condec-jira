package de.uhd.ifi.se.decision.management.jira.metric;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCompletenessCalculator extends TestSetUp {

	private RationaleCompletenessCalculator calculator;

	@Before
	public void setUp() {
		init();
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		calculator = new RationaleCompletenessCalculator(filterSettings);
	}

	@Test
	@NonTransactional
	public void testGetIssuesSolvedByDecision() {
		assertEquals(2, calculator.getIssuesSolvedByDecisionMap().size());
	}

	@Test
	@NonTransactional
	public void testGetDecisionsSolvingIssues() {
		assertEquals(2, calculator.getDecisionsSolvingIssuesMap().size());
	}

	@Test
	@NonTransactional
	public void testGetProArgumentDocumentedForAlternative() {
		assertEquals(2, calculator.getProArgumentDocumentedForAlternativeMap().size());
	}

	@Test
	@NonTransactional
	public void testGetConArgumentDocumentedForAlternative() {
		assertEquals(2, calculator.getConArgumentDocumentedForAlternativeMap().size());
	}

	@Test
	@NonTransactional
	public void testGetProArgumentDocumentedForDecision() {
		assertEquals(2, calculator.getProArgumentDocumentedForDecisionMap().size());
	}

	@Test
	@NonTransactional
	public void testGetConArgumentDocumentedForDecision() {
		assertEquals(2, calculator.getConArgumentDocumentedForDecisionMap().size());
	}

	@Test
	@NonTransactional
	public void testGetAlternativeDocumentedForIssue() {
		assertEquals(2, calculator.getAlternativeDocumentedForIssueMap().size());
	}
}