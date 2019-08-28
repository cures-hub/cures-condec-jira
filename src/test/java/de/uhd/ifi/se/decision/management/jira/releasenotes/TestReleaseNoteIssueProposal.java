package de.uhd.ifi.se.decision.management.jira.releasenotes;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.priority.Priority;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.releasenotes.impl.ReleaseNoteIssueProposalImpl;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestReleaseNoteIssueProposal extends TestSetUp {
	private long idOfDKElement;
	private ReleaseNoteIssueProposal proposal;
	private double rating;
	private IssueManager issueManager;
	private Issue issue;
	private ConstantsManager constantsManager;
	private Priority priority;
	private CommentManager commentManager;
	private Timestamp created;
	private Timestamp resolved;

	@Before
	public void setUp() {
		init();
		idOfDKElement = 14;
		DecisionKnowledgeElement dkElement = new DecisionKnowledgeElementImpl();
		dkElement.setId(idOfDKElement);
		proposal = new ReleaseNoteIssueProposalImpl(dkElement, 3);
		rating = 54.21;
		proposal.setRating(rating);
		issueManager = ComponentAccessor.getIssueManager();
		constantsManager = ComponentAccessor.getConstantsManager();
		List allPriorities = Arrays.asList(constantsManager.getPriorities().stream().toArray());
//		priority= (Priority) allPriorities.get(0);
		issue = issueManager.getIssueByCurrentKey("TEST-14");
		((MutableIssue) issue).setDescription("this is a test helloo");
		((MutableIssue) issue).setSummary("this is a summary test helloo");
		created = new Timestamp(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000);
		resolved = new Timestamp(System.currentTimeMillis());
		((MutableIssue) issue).setCreated(created);
		((MutableIssue) issue).setResolutionDate(resolved);
//		((MutableIssue) issue).setPriority(priority);
		commentManager = ComponentAccessor.getCommentManager();


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

	public void testGetAndSetPriority() {
		proposal.getAndSetPriority(issue);
		assertEquals(issue.getPriority(), proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.PRIORITY));
	}

	@Test
	public void testGetAndSetCountOfComments() {
		proposal.getAndSetCountOfComments(issue);
		assertEquals(commentManager.getComments(issue).size(), proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.COUNT_COMMENTS), 0.0);
	}

	public void testGetAndSetSizeOfSummary() {
		proposal.getAndSetSizeOfSummary();
		assertEquals(6, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.SIZE_SUMMARY), 0.0);

	}

	@Test
	public void testGetAndSetSizeOfDescription() {
		proposal.getAndSetSizeOfDescription();
		assertEquals(0, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.SIZE_DESCRIPTION), 0.0);
	}


	@Test
	public void testGetAndSetDaysToCompletion() {
		proposal.getAndSetDaysToCompletion(issue);
		assertEquals(2, proposal.getTaskCriteriaPrioritisation().get(TaskCriteriaPrioritisation.DAYS_COMPLETION), 0.0);
	}


}