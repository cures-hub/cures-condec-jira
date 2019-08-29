package de.uhd.ifi.se.decision.management.jira.releasenotes;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteIssueProposalImpl;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TestReleaseNoteIssueProposal extends TestSetUp {
	private long idOfDKElement;
	private ReleaseNoteIssueProposal proposal;
	private double rating;
	private Issue issue;
	private CommentManager commentManager;
	private HashMap<String, Integer> existingReporterCount;
	private ApplicationUser user;

	@Before
	public void setUp() {
		init();
		idOfDKElement = 14;
		DecisionKnowledgeElement dkElement = new DecisionKnowledgeElementImpl();
		dkElement.setId(idOfDKElement);
		dkElement.setDescription("this is a test helloo");
		dkElement.setSummary("this is a summary test helloo");
		proposal = new ReleaseNoteIssueProposalImpl(dkElement, 3);
		rating = 54.21;
		proposal.setRating(rating);
		existingReporterCount = new HashMap<String, Integer>();
		//get managers
		IssueManager issueManager = ComponentAccessor.getIssueManager();
//		constantsManager = ComponentAccessor.getConstantsManager();
		commentManager = ComponentAccessor.getCommentManager();
		user = JiraUsers.BLACK_HEAD.getApplicationUser();
//		List allPriorities = Arrays.asList(constantsManager.getPriorities().stream().toArray());
//		priority= (Priority) allPriorities.get(0);

		//get test issue and set properties
		issue = issueManager.getIssueByCurrentKey("TEST-14");
		Timestamp created = new Timestamp(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
		Timestamp resolved = new Timestamp(System.currentTimeMillis());
		((MutableIssue) issue).setCreated(created);
		((MutableIssue) issue).setResolutionDate(resolved);
//		((MutableIssue) issue).setPriority(priority);
		((MutableIssue) issue).setReporterId(user.getKey());

		//additional issue for query
		Issue additionalIssue = new MockIssue();
		((MutableIssue) additionalIssue).setReporterId(user.getKey());
		((MutableIssue) additionalIssue).setAssigneeId(user.getKey());
//		((MutableIssue) additionalIssue).setStatus("resolved");
	}

	@Test
	public void testGetDecisionKnowledgeElement() {
		assertEquals(idOfDKElement, proposal.getDecisionKnowledgeElement().getId());
	}

	@Test
	public void testSetDecisionKnowledgeElement() {
		DecisionKnowledgeElement dkElement2 = new DecisionKnowledgeElementImpl();
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
	public void testGetTaskCriteriaPrioritisation() {
		assertEquals(8, proposal.getTaskCriteriaPrioritisation().size());

	}

	@Test
	public void testSetTaskCriteriaPrioritisation() {
		EnumMap<TaskCriteriaPrioritisation, Integer> taskCriteriaPrioritisation = TaskCriteriaPrioritisation.toIntegerEnumMap();
		taskCriteriaPrioritisation.put(TaskCriteriaPrioritisation.DAYS_COMPLETION, 135);
		proposal.setTaskCriteriaPrioritisation(taskCriteriaPrioritisation);
		assertEquals(135, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.DAYS_COMPLETION), 0.0);
	}

	//@todo fix this test setting priority
	public void testGetAndSetPriority() {
		proposal.getAndSetPriority(issue);
		assertEquals(issue.getPriority(), proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.PRIORITY));
	}

	@Test
	public void testGetAndSetCountOfComments() {
		proposal.getAndSetCountOfComments(issue);
		assertEquals(commentManager.getComments(issue).size(), proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.COUNT_COMMENTS), 0.0);
	}

	@Test
	public void testGetAndSetSizeOfSummary() {
		proposal.getAndSetSizeOfSummary();
		assertEquals(6, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.SIZE_SUMMARY), 0.0);

	}

	@Test
	public void testGetAndSetSizeOfDescription() {
		proposal.getAndSetSizeOfDescription();
		assertEquals(5, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.SIZE_DESCRIPTION), 0.0);
	}


	@Test
	public void testGetAndSetDaysToCompletion() {
		proposal.getAndSetDaysToCompletion(issue);
		assertEquals(2, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.DAYS_COMPLETION), 0.0);
	}

	//@todo fix this test should be 1 as a mockIssue was created
	@Test
	public void testGetAndSetExperienceReporter() {
		proposal.getAndSetExperienceReporter(issue, existingReporterCount, user);
		assertEquals(0, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.EXPERIENCE_REPORTER), 0.0);
	}

	//@todo fix this test should be 1 as a mockIssue was created
	//@todo set mockissue to status resolved
	@Test
	public void testGetAndSetExperienceResolver() {
		proposal.getAndSetExperienceResolver(issue, existingReporterCount, user);
		assertEquals(0, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.EXPERIENCE_RESOLVER), 0.0);
	}

}