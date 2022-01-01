package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
	public void testRationaleCompletenessCalculator() {
		assertNotNull(calculator);
	}

	@Test
	@NonTransactional
	public void testGetIssuesSolvedByDecision() {
		assertEquals(2, calculator.getIssuesSolvedByDecision().getCompleteElements().size());
	}

	@Test
	@NonTransactional
	public void testGetDecisionsSolvingIssues() {
		assertEquals(1, calculator.getDecisionsSolvingIssues().getCompleteElements().size());
	}

	@Test
	@NonTransactional
	public void testGetProArgumentDocumentedForAlternative() {
		assertEquals(0, calculator.getProArgumentDocumentedForAlternative().getCompleteElements().size());
	}

	@Test
	@NonTransactional
	public void testGetConArgumentDocumentedForAlternative() {
		assertEquals(0, calculator.getConArgumentDocumentedForAlternative().getCompleteElements().size());
	}

	@Test
	@NonTransactional
	public void testGetProArgumentDocumentedForDecision() {
		assertEquals(0, calculator.getProArgumentDocumentedForDecision().getCompleteElements().size());
	}

	@Test
	@NonTransactional
	public void testGetConArgumentDocumentedForDecision() {
		assertEquals(0, calculator.getConArgumentDocumentedForDecision().getCompleteElements().size());
	}

}
