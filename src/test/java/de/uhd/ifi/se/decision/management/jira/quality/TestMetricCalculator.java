package de.uhd.ifi.se.decision.management.jira.quality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestMetricCalculator extends TestSetUp {
    protected MetricCalculator calculator;
    private Issue issue;

    @Before
    public void setUp() {
	TestSetUpGit.setUpBeforeClass();
	init();
	ApplicationUser user = de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers.SYS_ADMIN.getApplicationUser();

	calculator = new MetricCalculator((long) 1, user, "16");
	de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase(17, "Issue");
	de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase(18, "Decision");
	de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues.addElementToDataBase(19, "Argument");
	issue = new MockIssue();
    }

    @Test
    @NonTransactional
    public void testGetLinkDistanceIssueMap() {
	Map<Integer, List<Issue>> resultMap = calculator.getLinkDistanceIssueMap(0, issue);
	assertEquals(1, resultMap.size());
    }

    @Test
    @NonTransactional
    public void testNumberOfCommentsPerIssue() {
	Map<String, Integer> map = calculator.numberOfCommentsPerIssue();
	// TODO this should be 1
	assertEquals(0, map.size());
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
	assertNull(calculator.getLinksToIssueTypeMap(null));
    }

    @Test
    @Ignore
    public void testGetLinksToIssueTypeMapTypeFilled() {
	Object map = calculator.getLinksToIssueTypeMap(KnowledgeType.ARGUMENT);
	assertEquals("{Links from other to Argument=0, No links from other to Argument=0}", map.toString());
    }
    /*
     * @Test public void testNumberOfCommitsPerIssue() { Map<String, Integer>
     * expectedCommits = new HashMap<>(); // TODO: setup some test commits //
     * expectedCommits.put(baseIssueKey,0); assertEquals(expectedCommits,
     * calculator.numberOfCommitsPerIssue()); }
     */

    @Test
    public void testGetNumberOfDecisionKnowledgeElementsForJiraIssues() {
	assertEquals(0, calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ARGUMENT, 2).size());
    }

    @Test
    @Ignore
    public void testGetReqAndClassSummary() {
	assertEquals(2, calculator.getReqAndClassSummary().size());
    }

    @Test
    public void testGetKnowledgeSourceCount() {
	assertEquals(4, calculator.getKnowledgeSourceCount().size());
    }

}
