package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

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
        List<KnowledgeElement> knowledgeElements = KnowledgeElements.getTestKnowledgeElements();
        assertEquals(22, knowledgeElements.size());
        int linkDistance = 3;
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		filterSettings.setLinkDistance(linkDistance);
		calculator = new CodeCoverageCalculator(user, filterSettings);
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
		assertFalse(calculation.get("No links from Code File to Issue").contains("TEST-TestClassThatIsDone.java"));

		calculation = calculator
				.getCodeFilesWithNeighborsOfOtherType(KnowledgeType.DECISION);

		assertTrue(calculation.containsKey("Links from Code File to Decision"));
		assertTrue(calculation.get("Links from Code File to Decision").contains("TEST-LinkedClassThatIsDone.java"));
		assertTrue(calculation.containsKey("No links from Code File to Decision"));
		assertTrue(calculation.get("No links from Code File to Decision").contains("TEST-ClassThatIsNotDone.java"));
		assertTrue(calculation.get("No links from Code File to Decision").contains("TEST-SmallClassThatIsDone.java"));
		assertFalse(calculation.get("No links from Code File to Decision").contains("TEST-TestClassThatIsDone.java"));
    }

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForCodeFilesNull() {
		assertNull(calculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(null));
	}

    @Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForCodeFilesFilled() {
		assertEquals(3, calculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType.ISSUE).size());
		assertEquals(3, calculator.getNumberOfDecisionKnowledgeElementsForCodeFiles(KnowledgeType.DECISION).size());
	}

}
