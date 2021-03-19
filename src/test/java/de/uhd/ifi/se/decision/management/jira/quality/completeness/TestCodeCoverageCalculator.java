package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;


import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeCoverageCalculator extends TestSetUp {

	protected CodeCoverageCalculator calculator;

	@Before
	public void setUp() {
		init();
		String projectKey = "TEST";
        int linkDistance = 3;
		calculator = new CodeCoverageCalculator(projectKey, linkDistance);
	}

	@Test
	@NonTransactional
	public void testGetJiraIssuesWithNeighborsOfOtherTypeNull() {
		assertNull(calculator.getCodeFilesWithNeighborsOfOtherType(null));
	}

	@Test
	@NonTransactional
	public void testGetJiraIssuesWithNeighborsOfOtherTypeFilled() {
		Map<String, String> calculation = calculator
				.getCodeFilesWithNeighborsOfOtherType(KnowledgeType.ISSUE);

		assertTrue(calculation.containsKey("Links from Code File to Issue"));
		assertTrue(calculation.get("Links from Code File to Issue").isEmpty());
		assertTrue(calculation.containsKey("No links from Code File to Issue"));
		assertTrue(calculation.get("No links from Code File to Issue").isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForJiraIssues() {
		assertEquals(0, calculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType.ISSUE).size());
	}

}
