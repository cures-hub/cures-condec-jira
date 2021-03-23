package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCompletenessCalculator extends TestSetUp {

	protected RationaleCompletenessCalculator calculator;

	@Before
	public void setUp() {
		init();
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		calculator = new RationaleCompletenessCalculator(projectKey, filterSettings);
	}

	@Test
	@NonTransactional
	public void testElementsWithNeighborsOfOtherTypeFromArgumentToDecision() {
		Map<String, String> calculation = calculator.getElementsWithNeighborsOfOtherType(KnowledgeType.ARGUMENT,
				KnowledgeType.DECISION);

		assertTrue(calculation.containsKey("Argument has no Decision"));
		assertFalse(calculation.get("Argument has no Decision").isEmpty());
		assertTrue(calculation.containsKey("Argument has Decision"));
		assertEquals("TEST-5", calculation.get("Argument has Decision"));
	}

	@Test
	@NonTransactional
	public void testElementsWithNeighborsOfOtherTypeFromDecisionToArgument() {
		Map<String, String> calculation = calculator.getElementsWithNeighborsOfOtherType(KnowledgeType.DECISION,
				KnowledgeType.ARGUMENT);

		assertTrue(calculation.containsKey("Decision has no Argument"));
		assertTrue(calculation.get("Decision has no Argument").isEmpty());
		assertTrue(calculation.containsKey("Decision has Argument"));
		assertEquals("TEST-4", calculation.get("Decision has Argument"));
	}

}
