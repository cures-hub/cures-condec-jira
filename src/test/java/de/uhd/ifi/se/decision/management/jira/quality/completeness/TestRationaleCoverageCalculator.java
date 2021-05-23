package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRationaleCoverageCalculator extends TestSetUp {

	private RationaleCoverageCalculator calculator;

	@Before
	public void setUp() {
		init();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		FilterSettings filterSettings = new FilterSettings("TEST", "");
		String sourceKnowledgeTypes = "TEST-1";
		calculator = new RationaleCoverageCalculator(user, filterSettings, sourceKnowledgeTypes);
	}

	@Test
	@NonTransactional
	public void testRationaleCoverageCalculator() {
		assertNotNull(calculator);
	}

	@Test
	@NonTransactional
	public void testGetDecisionsPerSelectedJiraIssue() {
		assertEquals(calculator.getDecisionsPerSelectedJiraIssue().size(), 5);
	}

	@Test
	@NonTransactional
	public void testGetIssuesPerSelectedJiraIssue() {
		assertEquals(calculator.getIssuesPerSelectedJiraIssue().size(), 5);
	}

	@Test
	@NonTransactional
	public void testGetDecisionDocumentedForSelectedJiraIssue() {
		assertEquals(calculator.getDecisionDocumentedForSelectedJiraIssue().size(), 3);
	}

	@Test
	@NonTransactional
	public void testGetIssueDocumentedForSelectedJiraIssue() {
		assertEquals(calculator.getIssueDocumentedForSelectedJiraIssue().size(), 3);
	}

	@Test
	@NonTransactional
	public void testCalculateNumberOfDecisionKnowledgeElementsForKnowledgeElement() {
		assertEquals(calculator.calculateNumberOfDecisionKnowledgeElementsForKnowledgeElement(
				KnowledgeElements.getTestKnowledgeElement(), KnowledgeType.ISSUE), 2);
	}

	@Test
	@NonTransactional
	public void testCalculateNumberOfDecisionKnowledgeElementsForKnowledgeElementNull() {
		assertEquals(calculator.calculateNumberOfDecisionKnowledgeElementsForKnowledgeElement(
				KnowledgeElements.getTestKnowledgeElement(), null), 0);
	}

}
