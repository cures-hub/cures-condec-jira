package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCoverageCalculator extends TestSetUp {

	protected RationaleCoverageCalculator calculator;

	@Before
	public void setUp() {
		init();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		String projectKey = "TEST";
		FilterSettings filterSettings = new FilterSettings(projectKey, "");
		calculator = new RationaleCoverageCalculator(user, filterSettings);
	}

	@Test
	@NonTransactional
	public void testGetJiraIssuesWithNeighborsOfOtherTypeNull() {
		assertNull(calculator.getJiraIssuesWithNeighborsOfOtherType(null, null));
	}

	@Test
	@NonTransactional
	public void testGetJiraIssuesWithNeighborsOfOtherTypeFilled() {
		Map<String, String> calculation = calculator
				.getJiraIssuesWithNeighborsOfOtherType(JiraIssueTypes.getTestTypes().get(0), KnowledgeType.ISSUE);

		assertTrue(calculation.containsKey("Many links from Task to Issue"));
		assertTrue(calculation.get("Many links from Task to Issue").isEmpty());
		assertTrue(calculation.containsKey("Some links from Task to Issue"));
		assertTrue(calculation.get("Some links from Task to Issue").isEmpty());
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

}
