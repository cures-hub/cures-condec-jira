package de.uhd.ifi.se.decision.management.jira.quality.completeness;

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
		assertEquals(2, calculator.getIssuesSolvedByDecision().size());
	}

	@Test
	@NonTransactional
	public void testGetDecisionsSolvingIssues() {
		assertEquals(2, calculator.getDecisionsSolvingIssues().size());
	}

	@Test
	@NonTransactional
	public void testGetProArgumentDocumentedForAlternative() {
		assertEquals(2, calculator.getProArgumentDocumentedForAlternative().size());
	}

	@Test
	@NonTransactional
	public void testGetConArgumentDocumentedForAlternative() {
		assertEquals(2, calculator.getConArgumentDocumentedForAlternative().size());
	}

	@Test
	@NonTransactional
	public void testGetProArgumentDocumentedForDecision() {
		assertEquals(2, calculator.getProArgumentDocumentedForDecision().size());
	}

	@Test
	@NonTransactional
	public void testGetConArgumentDocumentedForDecision() {
		assertEquals(2, calculator.getConArgumentDocumentedForDecision().size());
	}
}