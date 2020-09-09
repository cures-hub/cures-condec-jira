package de.uhd.ifi.se.decision.management.jira.releasenotes;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.HashMap;

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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class TestReleaseNotesIssueProposal extends TestSetUp {
	private long idOfDKElement;
	private ReleaseNotesIssueProposal proposal;
	private double rating;
	private Issue issue;
	private CommentManager commentManager;
	private HashMap<String, Integer> existingReporterCount;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		idOfDKElement = 14;
		KnowledgeElement dkElement = new KnowledgeElement();
		dkElement.setId(idOfDKElement);
		dkElement.setDescription("this is a test helloo");
		dkElement.setSummary("this is a summary test helloo");
		proposal = new ReleaseNotesIssueProposal(dkElement, 3);
		rating = 54.21;
		proposal.setRating(rating);
		existingReporterCount = new HashMap<String, Integer>();
		// get managers
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		// constantsManager = ComponentAccessor.getConstantsManager();
		commentManager = ComponentAccessor.getCommentManager();
		user = JiraUsers.BLACK_HEAD.getApplicationUser();
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
		assertEquals(idOfDKElement, proposal.getDecisionKnowledgeElement().getId());
	}

	@Test
	public void testSetDecisionKnowledgeElement() {
		KnowledgeElement dkElement2 = new KnowledgeElement();
		dkElement2.setId(15);
		proposal.setDecisionKnowledgeElement(dkElement2);
		assertEquals(15, proposal.getDecisionKnowledgeElement().getId());
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
		assertEquals(8, proposal.getMetrics().size());
	}

	@Test
	public void testSetissueMetric() {
		EnumMap<JiraIssueMetric, Integer> issueMetric = JiraIssueMetric.toIntegerEnumMap();
		issueMetric.put(JiraIssueMetric.DAYS_COMPLETION, 135);
		proposal.setMetrics(issueMetric);
		assertEquals(135, proposal.getMetrics().get(JiraIssueMetric.DAYS_COMPLETION), 0.0);
	}

	// @todo fix this test setting priority
	public void testGetAndSetPriority() {
		proposal.getAndSetPriority(issue);
		assertEquals(issue.getPriority(), proposal.getMetrics().get(JiraIssueMetric.PRIORITY));
	}

	@Test
	public void testGetAndSetCountOfComments() {
		proposal.getAndSetCountOfComments(issue);
		assertEquals(commentManager.getComments(issue).size(),
				proposal.getMetrics().get(JiraIssueMetric.COUNT_COMMENTS), 0.0);
	}

	@Test
	public void testGetAndSetSizeOfSummary() {
		proposal.getAndSetSizeOfSummary();
		assertEquals(6, proposal.getMetrics().get(JiraIssueMetric.SIZE_SUMMARY), 0.0);

	}

	@Test
	public void testGetAndSetSizeOfDescription() {
		proposal.getAndSetSizeOfDescription();
		assertEquals(5, proposal.getMetrics().get(JiraIssueMetric.SIZE_DESCRIPTION), 0.0);
	}

	@Test
	public void testGetAndSetDaysToCompletion() {
		proposal.getAndSetDaysToCompletion(issue);
		assertEquals(2, proposal.getMetrics().get(JiraIssueMetric.DAYS_COMPLETION), 0.0);
	}

	// @todo fix this test should be 1 as a mockIssue was created
	@Test
	public void testGetAndSetExperienceReporter() {
		proposal.getAndSetExperienceReporter(issue, existingReporterCount, user);
		assertEquals(0, proposal.getMetrics().get(JiraIssueMetric.EXPERIENCE_REPORTER), 0.0);
	}

	// @todo fix this test should be 1 as a mockIssue was created
	// @todo set mockissue to status resolved
	@Test
	public void testGetAndSetExperienceResolver() {
		proposal.getAndSetExperienceResolver(issue, existingReporterCount, user);
		assertEquals(0, proposal.getMetrics().get(JiraIssueMetric.EXPERIENCE_RESOLVER), 0.0);
	}

}