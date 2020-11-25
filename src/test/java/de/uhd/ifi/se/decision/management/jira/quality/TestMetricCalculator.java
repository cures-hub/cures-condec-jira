package de.uhd.ifi.se.decision.management.jira.quality;

import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addComment;
import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase;
import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.getTestJiraIssues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMetricCalculator extends TestSetUp {

	protected MetricCalculator calculator;

	@Before
	public void setUp() {
		init();
		ApplicationUser user = JiraUsers.SYS_ADMIN.getApplicationUser();
		calculator = new MetricCalculator(user, JiraIssueTypes.getTestTypes().get(2), new FilterSettings("TEST", ""));
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
