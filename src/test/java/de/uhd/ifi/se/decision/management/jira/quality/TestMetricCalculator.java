package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.Map;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.mock.MockConstantsManager;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.mocks.MockCommentManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase;
import static de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.getTestJiraIssues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestMetricCalculator extends TestSetUp {
	protected MetricCalculator calculator;

	@Before
	public void setUp() {
		TestSetUpGit.setUpBeforeClass();
		init();
		ApplicationUser user = de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers.SYS_ADMIN.getApplicationUser();
		calculator = new MetricCalculator((long) 1, user, "16", false, KnowledgeType.toStringList(), KnowledgeStatus.toStringList(), null);
		addElementToDataBase(17, "Issue");
		addElementToDataBase(18, "Decision");
		addElementToDataBase(19, "Argument");
		calculator.setJiraIssues(getTestJiraIssues());
		new MockComponentWorker().init().addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(CommentManager.class, new MockCommentManager())
				.addMock(ProjectManager.class, new MockProjectManager())
				.addMock(ConstantsManager.class, new MockConstantsManager());
	}

	@Test
	@NonTransactional
	public void testNumberOfCommentsPerIssue() {
		Map<String, Integer> map = calculator.numberOfCommentsPerIssue();
		assertEquals(8, map.size());
	}

	@Test
	@NonTransactional
	public void testGetNumberOfRelevantComments() {
		assertEquals(2, calculator.getNumberOfRelevantComments().size());
	}

	@Test
	public void testGetDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType() {
		Map<String, String> calculation = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				KnowledgeType.ARGUMENT, KnowledgeType.DECISION);

		Map<String, String> calculation2 = calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(
				KnowledgeType.DECISION, KnowledgeType.ARGUMENT);

		assertEquals(2, calculation.size(), 0.0); // expecting always two categories
		assertTrue(calculation.containsKey("Argument has no Decision"));
		assertFalse(calculation.get("Argument has no Decision").isEmpty());
		assertTrue(calculation.containsKey("Argument has Decision"));
		assertEquals("", calculation.get("Argument has Decision"));

		assertEquals(2, calculation2.size(), 0.0); // expecting always two categories
		assertTrue(calculation2.containsKey("Decision has no Argument"));
		assertFalse(calculation2.get("Decision has no Argument").isEmpty());
		assertTrue(calculation2.containsKey("Decision has Argument"));
		assertEquals("", calculation2.get("Decision has Argument"));
	}

	@Test
	public void testGetDistributionOfKnowledgeTypes() {
		assertEquals(4, calculator.getDistributionOfKnowledgeTypes().size());
	}

	@Test
	public void testGetLinksToIssueTypeMapTypeNull() {
		assertNull(calculator.getLinksToIssueTypeMap(null, 0));
	}

	@Test
	public void testGetLinksToIssueTypeMapTypeFilled() {
		Object map = calculator.getLinksToIssueTypeMap(KnowledgeType.ARGUMENT, 0);
		assertEquals("{Links from  to Argument=, No links from  to Argument=}", map.toString());
	}

	@Test
	public void testGetNumberOfDecisionKnowledgeElementsForJiraIssues() {
		assertEquals(8, calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE, 2).size());
	}

	@Test
	public void testGetReqAndClassSummary() {
		assertEquals(2, calculator.getReqAndClassSummary().size());
	}

	@Test
	public void testGetKnowledgeSourceCount() {
		assertEquals(4, calculator.getKnowledgeSourceCount().size());
	}

}
