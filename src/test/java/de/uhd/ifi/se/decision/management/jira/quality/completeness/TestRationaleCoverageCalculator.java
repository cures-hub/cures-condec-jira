package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCoverageCalculator extends TestSetUp {

	protected RationaleCoverageCalculator calculator;

	@Before
	public void setUp() {
		init();
		List<KnowledgeElement> knowledgeElements = KnowledgeElements.getTestKnowledgeElements();
		assertEquals(22, knowledgeElements.size());
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		filterSettings.setLinkDistance(3);
		calculator = new RationaleCoverageCalculator(user, filterSettings);
	}

	@Test
	@NonTransactional
	public void testGetJiraIssuesWithNeighborsOfOtherTypeNull() {
		assertNull(calculator.getJiraIssuesWithNeighborsOfOtherType(JiraIssueTypes.getTestTypes().get(0), null));
	}

	@Test
	@NonTransactional
	public void testGetJiraIssuesWithNeighborsOfOtherTypeFilled() {
		Map<String, String> calculation = calculator
				.getJiraIssuesWithNeighborsOfOtherType(JiraIssueTypes.getTestTypes().get(0), KnowledgeType.ISSUE);

		assertTrue(calculation.containsKey("More than 2 links from Task to Issue"));
		assertTrue(calculation.get("More than 2 links from Task to Issue").isEmpty());
		assertTrue(calculation.containsKey("Less than 2 links from Task to Issue"));
		assertTrue(calculation.get("Less than 2 links from Task to Issue").isEmpty());
		assertTrue(calculation.containsKey("No links from Task to Issue"));
		assertTrue(calculation.get("No links from Task to Issue").isEmpty());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForJiraIssuesNull() {
		assertNull(calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(null, null));
	}

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForJiraIssuesFilled() {
		assertEquals(0, calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(JiraIssueTypes.getTestTypes().get(0), KnowledgeType.ISSUE).size());
	}

	@Test
	@NonTransactional
	public void testCalculateNumberOfElementsOfKnowledgeTypeWithinLinkDistance() {
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		KnowledgeType knowledgeType = KnowledgeType.ISSUE;
		int linkDistance = 2;
		assertEquals(0, calculator.calculateNumberOfElementsOfKnowledgeTypeWithinLinkDistance(knowledgeElement, knowledgeType, linkDistance));
	}

	@Test
	@NonTransactional
	public void testGetCodeFilesWithNeighborsOfOtherTypeNull() {
		assertNull(calculator.getCodeFilesWithNeighborsOfOtherType(null));
	}

	@Test
	@NonTransactional
	public void testGetCodeFilesWithNeighborsOfOtherTypeFilled() {
		Map<String, String> calculation = calculator.getCodeFilesWithNeighborsOfOtherType(KnowledgeType.ISSUE);

		assertTrue(calculation.containsKey("Links from Code File to Issue"));
		assertTrue(calculation.get("Links from Code File to Issue").contains("TEST-LinkedClassThatIsDone.java"));
		assertTrue(calculation.containsKey("No links from Code File to Issue"));
		assertTrue(calculation.get("No links from Code File to Issue").contains("TEST-ClassThatIsNotDone.java"));
		assertTrue(calculation.get("No links from Code File to Issue").contains("TEST-SmallClassThatIsDone.java"));
		assertFalse(calculation.get("No links from Code File to Issue").contains("TEST-TestClassThatIsDone.java"));

		calculation = calculator.getCodeFilesWithNeighborsOfOtherType(KnowledgeType.DECISION);

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
