package de.uhd.ifi.se.decision.management.jira.eventlistener.consistencycheckeventlistener;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.issue.status.category.StatusCategoryImpl;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.ConsistencyCheckEventListenerSingleton;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ConsistencyCheckLogsInDatabase;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Optional;

import static de.uhd.ifi.se.decision.management.jira.consistency.TestConsistencyCheckEventTrigger.generateWorkflowIssueEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestConsistencyCheckEventListener extends TestSetUp {

	private ConsistencyCheckEventListenerSingleton eventListener;
	private MutableIssue issue;
	private ApplicationUser user;
	private Comment jiraComment;

	@Before
	public void setUp() {
		TestSetUp.init();
		issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		jiraComment = ComponentAccessor.getCommentManager().create(issue, user, "Test Comment", true);
		eventListener = ConsistencyCheckEventListenerSingleton.getInstance();
	}


	@Test
	public void testGetter() {
		assertEquals("Two consistency check event trigger names should be registered.", 2, eventListener.getAllConsistencyCheckEventTriggerNames().size());

		assertTrue("Name 'done' should exist.", eventListener.doesConsistencyCheckEventTriggerNameExist("done"));

		assertFalse("Name 'none' should NOT exist.", eventListener.doesConsistencyCheckEventTriggerNameExist("none"));

		assertFalse("Name being null should NOT exist.", eventListener.doesConsistencyCheckEventTriggerNameExist(null));
	}

	@Test
	public void testOnIssueEvent() {
		Optional<ConsistencyCheckLogsInDatabase> check = ConsistencyCheckLogHelper.getCheck(issue.getKey());
		assertTrue("No pending check should exist.", check.isEmpty());

		eventListener.onIssueEvent(generateWorkflowIssueEvent(issue, user, jiraComment, "Done", StatusCategoryImpl.findByKey(StatusCategory.COMPLETE), EventType.ISSUE_GENERICEVENT_ID));
		check = ConsistencyCheckLogHelper.getCheck(issue.getKey());
		assertTrue("Now a pending check should exist.", check.isPresent());

		eventListener.onIssueEvent(generateWorkflowIssueEvent(issue, user, jiraComment, "Open", StatusCategoryImpl.findByKey(StatusCategory.IN_PROGRESS), EventType.ISSUE_GENERICEVENT_ID));

		assertFalse("After resetting the workflow the chock does no longer need approval.", ConsistencyCheckLogHelper.doesIssueNeedApproval(issue));
	}

	@AfterEach
	public void reset() {
		ConsistencyCheckLogHelper.deleteCheck(issue);
	}


}
