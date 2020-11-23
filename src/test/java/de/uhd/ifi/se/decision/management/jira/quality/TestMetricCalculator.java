package de.uhd.ifi.se.decision.management.jira.quality;

import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addComment;
import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase;
import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.getTestJiraIssues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueLinks;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMetricCalculator extends TestSetUpGit {
	protected static final Logger LOGGER = LoggerFactory.getLogger(TestMetricCalculator.class);

	protected MetricCalculator calculator;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		ApplicationUser user = de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers.SYS_ADMIN.getApplicationUser();
		calculator = new MetricCalculator((long) 1, user, "alternative", KnowledgeType.toStringList(),
				KnowledgeStatus.toStringList(), null);
		calculator.setJiraIssues(getTestJiraIssues());
	}

	@Test
	@NonTransactional
	public void testNumberOfCommentsPerIssue() {
		Map<String, Integer> map = calculator.numberOfCommentsPerIssue();
		assertEquals(10, map.size());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfRelevantComments() {
		addComment(getTestJiraIssues().get(7));
		calculator.setJiraIssues(getTestJiraIssues());
		assertEquals(2, calculator.getNumberOfRelevantComments().size());
	}

	@Test
	@NonTransactional
	public void testGetDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType() {
		addComment(getTestJiraIssues().get(6));
		JiraIssueLinks.getTestJiraIssueLinks();
		calculator.setJiraIssues(getTestJiraIssues());
		Map<String, String> calculation = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				KnowledgeType.ARGUMENT, KnowledgeType.DECISION);

		Map<String, String> calculation2 = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				KnowledgeType.DECISION, KnowledgeType.ARGUMENT);

		assertEquals(2, calculation.size()); // expecting always two categories
		assertTrue(calculation.containsKey("Argument has no Decision"));
		assertFalse(calculation.get("Argument has no Decision").isEmpty());
		assertTrue(calculation.containsKey("Argument has Decision"));
		assertEquals("", calculation.get("Argument has Decision"));

		assertEquals(2, calculation2.size()); // expecting always two categories
		assertTrue(calculation2.containsKey("Decision has no Argument"));
		assertFalse(calculation2.get("Decision has no Argument").isEmpty());
		assertTrue(calculation2.containsKey("Decision has Argument"));
		assertEquals("", calculation2.get("Decision has Argument"));
	}

	@Test
	@NonTransactional
	public void testGetDistributionOfKnowledgeTypes() {
		assertEquals(4, calculator.getDistributionOfKnowledgeTypes().size());
	}

	@Test
	@NonTransactional
	public void testGetLinksToIssueTypeMapTypeNull() {
		assertNull(calculator.getLinksToIssueTypeMap(null, 0));
	}

	@Test
	@NonTransactional
	public void testGetLinksToIssueTypeMapTypeFilled() {
		addComment(getTestJiraIssues().get(6));
		calculator.setJiraIssues(getTestJiraIssues());
		Object map = calculator.getLinksToIssueTypeMap(KnowledgeType.ARGUMENT, 1);
		assertEquals("{Links from  to Argument=, No links from  to Argument=}", map.toString());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfDecisionKnowledgeElementsForJiraIssues() {
		assertEquals(10, calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE, 2).size());
	}

	@Test
	@NonTransactional
	public void testGetReqAndClassSummary() {
		assertEquals(2, calculator.getReqAndClassSummary().size());
	}

	@Test
	@NonTransactional
	public void testGetKnowledgeSourceCount() {
		addElementToDataBase(24, "Decision");
		addComment(getTestJiraIssues().get(7));
		calculator.setJiraIssues(getTestJiraIssues());
		assertEquals(4, calculator.getKnowledgeSourceCount().size());
	}

}
