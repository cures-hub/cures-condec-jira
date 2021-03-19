package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestCodeCoverageCalculator extends TestSetUp {

	protected CodeCoverageCalculator calculator;

	@Before
	public void setUp() {
		init();
		String projectKey = "TEST";
        List<KnowledgeElement> knowledgeElements = KnowledgeElements.getTestKnowledgeElements();
        assertEquals(22, knowledgeElements.size());
        int linkDistance = 3;
		calculator = new CodeCoverageCalculator(projectKey, linkDistance);
	}

	@Test
	@NonTransactional
	public void testGetCodeFilesWithNeighborsOfOtherTypeNull() {
		assertNull(calculator.getCodeFilesWithNeighborsOfOtherType(null));
	}

	@Test
	@NonTransactional
	public void testGetCodeFilesWithNeighborsOfOtherTypeFilled() {
		Map<String, String> calculation = calculator
				.getCodeFilesWithNeighborsOfOtherType(KnowledgeType.ISSUE);

		assertTrue(calculation.containsKey("Links from Code File to Issue"));
		assertTrue(calculation.get("Links from Code File to Issue").contains("TEST-LinkedClassThatIsDone.java"));
		assertTrue(calculation.containsKey("No links from Code File to Issue"));
		assertTrue(calculation.get("No links from Code File to Issue").contains("TEST-ClassThatIsNotDone.java"));
		assertTrue(calculation.get("No links from Code File to Issue").contains("TEST-SmallClassThatIsDone.java"));
		assertTrue(calculation.get("No links from Code File to Issue").contains("TEST-TestClassThatIsDone.java"));

		calculation = calculator
				.getCodeFilesWithNeighborsOfOtherType(KnowledgeType.DECISION);

		assertTrue(calculation.containsKey("Links from Code File to Decision"));
		assertTrue(calculation.get("Links from Code File to Decision").contains("TEST-LinkedClassThatIsDone.java"));
		assertTrue(calculation.containsKey("No links from Code File to Decision"));
		assertTrue(calculation.get("No links from Code File to Decision").contains("TEST-ClassThatIsNotDone.java"));
		assertTrue(calculation.get("No links from Code File to Decision").contains("TEST-SmallClassThatIsDone.java"));
		assertTrue(calculation.get("No links from Code File to Decision").contains("TEST-TestClassThatIsDone.java"));
    }

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForCodeFilesNull() {
		assertNull(calculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(null));
	}

    @Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForCodeFilesFilled() {
		assertEquals(4, calculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType.ISSUE).size());
		assertEquals(4, calculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType.DECISION).size());
	}

}
