package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.EnumMap;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestReleaseNotesIssueProposal extends TestSetUp {
	private long idOfDKElement;
	private JiraIssueProposalForReleaseNotes proposal;
	private double rating;
	private Issue issue;
	private CommentManager commentManager;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		idOfDKElement = 14;
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		proposal = new JiraIssueProposalForReleaseNotes(JiraIssues.getJiraIssueByKey("TEST-14"), user);
		rating = 54.21;
		proposal.setRating(rating);
		// get managers
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		// constantsManager = ComponentAccessor.getConstantsManager();
		commentManager = ComponentAccessor.getCommentManager();

		// List allPriorities =
		// Arrays.asList(constantsManager.getPriorities().stream().toArray());
		// priority= (Priority) allPriorities.get(0);

		// get test issue and set properties
		issue = issueManager.getIssueByCurrentKey("TEST-14");
		Timestamp created = new Timestamp(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
		Timestamp resolved = new Timestamp(System.currentTimeMillis());
		((MutableIssue) issue).setCreated(created);
		((MutableIssue) issue).setResolutionDate(resolved);
		// ((MutableIssue) issue).setPriority(priority);
		((MutableIssue) issue).setReporterId(user.getKey());

		// additional issue for query
		Issue additionalIssue = new MockIssue();
		((MutableIssue) additionalIssue).setReporterId(user.getKey());
		((MutableIssue) additionalIssue).setAssigneeId(user.getKey());
		// ((MutableIssue) additionalIssue).setStatus("resolved");
	}

	@Test
	public void testGetDecisionKnowledgeElement() {
		assertEquals(idOfDKElement, (long) proposal.getElement().getId());
	}

	@Test
	public void testGetRating() {
		assertEquals(rating, proposal.getRating(), 0.0);
	}

	@Test
	public void testSetRating() {
		double rating2 = 32.653;
		proposal.setRating(rating2);
		assertEquals(rating2, proposal.getRating(), 0.0);
	}

	@Test
	public void testGetissueMetric() {
		assertEquals(8, proposal.getJiraIssueMetrics().size());
	}

	@Test
	public void testSetissueMetric() {
		EnumMap<JiraIssueMetric, Double> issueMetric = JiraIssueMetric.toEnumMap();
		issueMetric.put(JiraIssueMetric.DAYS_COMPLETION, 135.0);
		proposal.setMetrics(issueMetric);
		assertEquals(135, proposal.getJiraIssueMetrics().get(JiraIssueMetric.DAYS_COMPLETION), 0.0);
	}

	// @todo fix this test setting priority
	public void testGetAndSetPriority() {
		proposal.getAndSetPriority(issue);
		assertEquals(issue.getPriority(), proposal.getJiraIssueMetrics().get(JiraIssueMetric.PRIORITY));
	}

	@Test
	public void testGetAndSetCountOfComments() {
		proposal.getAndSetCountOfComments(issue);
		assertEquals(commentManager.getComments(issue).size(),
				proposal.getJiraIssueMetrics().get(JiraIssueMetric.COMMENT_COUNT), 0.0);
	}

	@Test
	public void testGetAndSetSizeOfSummary() {
		proposal.getAndSetSizeOfSummary();
		assertEquals(5, proposal.getJiraIssueMetrics().get(JiraIssueMetric.SIZE_SUMMARY), 0.0);

	}

	@Test
	public void testGetAndSetSizeOfDescription() {
		proposal.getAndSetSizeOfDescription();
		assertEquals(5, proposal.getJiraIssueMetrics().get(JiraIssueMetric.SIZE_DESCRIPTION), 0.0);
	}

	@Test
	public void testGetAndSetDaysToCompletion() {
		proposal.getAndSetDaysToCompletion(issue);
		assertEquals(2, proposal.getJiraIssueMetrics().get(JiraIssueMetric.DAYS_COMPLETION), 0.0);
	}

	// @todo fix this test should be 1 as a mockIssue was created
	@Test
	public void testGetAndSetExperienceReporter() {
		proposal.calculateReporterExperience(issue, user);
		assertEquals(0, proposal.getJiraIssueMetrics().get(JiraIssueMetric.EXPERIENCE_REPORTER), 0.0);
	}

	// @todo fix this test should be 1 as a mockIssue was created
	// @todo set mockissue to status resolved
	@Test
	public void testGetAndSetExperienceResolver() {
		proposal.calculateResolverExperience(issue, user);
		assertEquals(0, proposal.getJiraIssueMetrics().get(JiraIssueMetric.EXPERIENCE_RESOLVER), 0.0);
	}

	@Test
	public void testCountWords() {
		assertEquals(0, JiraIssueProposalForReleaseNotes.countWords(null));
		assertEquals(0, JiraIssueProposalForReleaseNotes.countWords(""));
		assertEquals(2, JiraIssueProposalForReleaseNotes.countWords("Hello world!"));
	}

}