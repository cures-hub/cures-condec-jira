package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestReleaseNotesEntry extends TestSetUp {

	private ReleaseNotesEntry releaseNotesEntry;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		releaseNotesEntry = new ReleaseNotesEntry(JiraIssues.getJiraIssueByKey("TEST-1"), user);
	}

	@Test
	public void testGetElement() {
		assertEquals(1, releaseNotesEntry.getElement().getId());
	}

	@Test
	public void testGetJiraIssue() {
		assertEquals(1, releaseNotesEntry.getJiraIssue().getId().intValue());
	}

	@Test
	public void testGetRating() {
		releaseNotesEntry.setRating(42);
		assertEquals(42, releaseNotesEntry.getRating(), 0.0);
	}

	@Test
	public void testGetJiraIssueMetrics() {
		assertEquals(8, releaseNotesEntry.getJiraIssueMetrics().size());
	}

	@Test
	public void testNumberOfDecisionKnowledgeElementsMetric() {
		assertTrue(releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.DECISION_KNOWLEDGE_COUNT) > 5);
	}

	@Test
	public void testPriorityMetric() {
		assertEquals(3, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.PRIORITY), 0.0);
	}

	@Test
	public void testCommentCountMetric() {
		assertEquals(0, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.COMMENT_COUNT), 0.0);
	}

	@Test
	public void testSummarySizeMetric() {
		assertEquals(3, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.SIZE_SUMMARY), 0.0);
	}

	@Test
	public void testDescriptionSizeMetric() {
		assertEquals(3, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.SIZE_DESCRIPTION), 0.0);
	}

	@Test
	public void testDaysToCompletionMetric() {
		assertEquals(0, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.DAYS_COMPLETION), 0.0);
	}

	@Test
	public void testReporterExperienceMetricValidReporter() {
		assertEquals(1, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.EXPERIENCE_REPORTER), 0.0);
	}

	@Test
	public void testReporterExperienceMetricReporterNull() {
		MutableIssue jiraIssue = (MutableIssue) JiraIssues.getJiraIssueByKey("TEST-1");
		jiraIssue.setReporterId(null);
		releaseNotesEntry = new ReleaseNotesEntry(jiraIssue, user);
		assertEquals(0, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.EXPERIENCE_REPORTER), 0.0);
	}

	@Test
	public void testResolverExperienceMetricValidAssignee() {
		assertEquals(1, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.EXPERIENCE_RESOLVER), 0.0);
	}

	@Test
	public void testResolverExperienceMetricAssigneeNull() {
		MutableIssue jiraIssue = (MutableIssue) JiraIssues.getJiraIssueByKey("TEST-1");
		jiraIssue.setAssigneeId(null);
		releaseNotesEntry = new ReleaseNotesEntry(jiraIssue, user);
		assertEquals(0, releaseNotesEntry.getJiraIssueMetrics().get(JiraIssueMetric.EXPERIENCE_RESOLVER), 0.0);
	}

	@Test
	public void testCountWords() {
		assertEquals(0, ReleaseNotesEntry.countWords(null));
		assertEquals(0, ReleaseNotesEntry.countWords(""));
		assertEquals(2, ReleaseNotesEntry.countWords("Hello world!"));
	}
}