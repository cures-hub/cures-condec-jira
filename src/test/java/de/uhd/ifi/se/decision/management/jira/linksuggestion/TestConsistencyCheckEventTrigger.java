package de.uhd.ifi.se.decision.management.jira.linksuggestion;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.status.MockStatus;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.issue.status.category.StatusCategoryImpl;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.QualityCheckEventTrigger;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.IssueClosedTrigger;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.WorkflowDoneTrigger;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestConsistencyCheckEventTrigger extends TestSetUp {
	private QualityCheckEventTrigger trigger;
	private ApplicationUser user;
	private MutableIssue issue;
	private Comment jiraComment;

	public static IssueEvent generateWorkflowIssueEvent(MutableIssue issue, ApplicationUser user, Comment jiraComment, String status, StatusCategory category, Long eventType) {
		HashMap<String, String> params = new HashMap<>();
		params.put("eventsource", "workflow");
		issue.setStatus(new MockStatus(status, status, category));

		return new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
			params, eventType);
	}

	@Before
	public void setUp() {
		TestSetUp.init();
		issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		jiraComment = ComponentAccessor.getCommentManager().create(issue, user, "Test Comment", true);
	}

	@Test
	public void testWorkflowDoneTrigger() {
		trigger = new WorkflowDoneTrigger(null);

		assertEquals("The name should be 'done'.", "done", trigger.getName());

		IssueEvent event = generateWorkflowIssueEvent(issue, user, jiraComment, "Done", StatusCategoryImpl.findByKey(StatusCategory.COMPLETE), EventType.ISSUE_GENERICEVENT_ID);
		trigger = new WorkflowDoneTrigger( event);

		assertTrue("Trigger should be triggered.", trigger.isTriggered());

		event = new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
			new HashMap<String, String>(), EventType.ISSUE_DELETED_ID);
		trigger = new WorkflowDoneTrigger(event);

		assertFalse("Trigger should not be triggered.", trigger.isTriggered());
		trigger = new WorkflowDoneTrigger(null);

		assertFalse("Trigger should not be triggered.", trigger.isTriggered());

	}


	@Test
	public void testStatusClosedTrigger() {
		trigger = new IssueClosedTrigger(null);
		assertEquals("The name should be 'closed'.", "closed", trigger.getName());

		IssueEvent event = generateWorkflowIssueEvent(issue, user, jiraComment, "Done", StatusCategoryImpl.findByKey(StatusCategory.COMPLETE), EventType.ISSUE_CLOSED_ID);
		trigger = new IssueClosedTrigger( event);
		assertTrue("Trigger should be triggered.", trigger.isTriggered());

		event = generateWorkflowIssueEvent(issue, user, jiraComment, "Done", StatusCategoryImpl.findByKey(StatusCategory.COMPLETE), EventType.ISSUE_DELETED_ID);
		trigger = new IssueClosedTrigger(event);
		assertFalse("Trigger should not be triggered.", trigger.isTriggered());

		trigger = new IssueClosedTrigger(null);
		assertFalse("Trigger should not be triggered.", trigger.isTriggered());

	}
}
