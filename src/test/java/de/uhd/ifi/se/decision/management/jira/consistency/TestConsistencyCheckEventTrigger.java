package de.uhd.ifi.se.decision.management.jira.consistency;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.status.MockStatus;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.StatusClosedTrigger;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.WorkflowDoneTrigger;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestConsistencyCheckEventTrigger extends TestSetUp {
	ConsistencyCheckEventTrigger trigger;
	private ApplicationUser user;
	private MutableIssue issue;
	private Comment jiraComment;

	@Before
	public void setUp() {
		TestSetUp.init();
		issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		jiraComment = ComponentAccessor.getCommentManager().create(issue, user, "Test Comment", true);
	}

	@Test
	public void testWorkflowDoneTrigger(){
		trigger = new WorkflowDoneTrigger();

		assertEquals("The name should be 'done'.", "done", trigger.getName());

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("eventsource", "workflow");
		issue.setStatus(new MockStatus("Done", "Done"));

		IssueEvent event = new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
			params, EventType.ISSUE_GENERICEVENT_ID);

		assertTrue("Trigger should be triggered.", trigger.isTriggered(event));

		event = new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
			new HashMap<String, String>(), EventType.ISSUE_DELETED_ID);
		assertFalse("Trigger should not be triggered.",trigger.isTriggered(event));

		assertFalse("Trigger should not be triggered.",trigger.isTriggered(null));

	}

	@Test
	public void testStatusClosedTrigger(){
		trigger = new StatusClosedTrigger();

		assertEquals("The name should be 'done'.", "closed", trigger.getName());

		IssueEvent event = new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
			new HashMap<String, String>(), EventType.ISSUE_CLOSED_ID);

		assertTrue("Trigger should be triggered.",trigger.isTriggered(event));

		event = new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
			new HashMap<String, String>(), EventType.ISSUE_DELETED_ID);
		assertFalse("Trigger should not be triggered.",trigger.isTriggered(event));

		assertFalse("Trigger should not be triggered.",trigger.isTriggered(null));

	}
}
