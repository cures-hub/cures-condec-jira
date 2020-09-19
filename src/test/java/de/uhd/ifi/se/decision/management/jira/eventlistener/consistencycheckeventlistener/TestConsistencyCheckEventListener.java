package de.uhd.ifi.se.decision.management.jira.eventlistener.consistencycheckeventlistener;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.issue.status.category.StatusCategoryImpl;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.eventlistener.implementation.QualityCheckEventListenerSingleton;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ConsistencyCheckLogsInDatabase;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Optional;

import static de.uhd.ifi.se.decision.management.jira.quality.consistency.TestConsistencyCheckEventTrigger.generateWorkflowIssueEvent;
import static org.junit.Assert.*;

public class TestConsistencyCheckEventListener extends TestSetUp {

	private QualityCheckEventListenerSingleton eventListener;
	private KnowledgeElement knowledgeElement;
	private ApplicationUser user;
	private Comment jiraComment;

	@Before
	public void setUp() {
		TestSetUp.init();
		knowledgeElement = new KnowledgeElement(ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4"));
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
		jiraComment = ComponentAccessor.getCommentManager().create(knowledgeElement.getJiraIssue(), user, "Test Comment", true);
		eventListener = (QualityCheckEventListenerSingleton) QualityCheckEventListenerSingleton.getInstance();
	}


	@Test
	public void testGetter() {
		assertEquals("Two consistency check event trigger names should be registered.", 2, eventListener.getAllQualityCheckEventTriggerNames().size());

		assertTrue("Name 'done' should exist.", eventListener.doesQualityCheckEventTriggerNameExist("done"));

		assertFalse("Name 'none' should NOT exist.", eventListener.doesQualityCheckEventTriggerNameExist("none"));

		assertFalse("Name being null should NOT exist.", eventListener.doesQualityCheckEventTriggerNameExist(null));
	}

	@Test
	public void testOnIssueEvent() {
		Optional<ConsistencyCheckLogsInDatabase> check = ConsistencyCheckLogHelper.getCheck(knowledgeElement);
		assertTrue("No pending check should exist.", check.isEmpty());

		eventListener.onIssueEvent(generateWorkflowIssueEvent((MutableIssue) knowledgeElement.getJiraIssue(), user, jiraComment, "Done", StatusCategoryImpl.findByKey(StatusCategory.COMPLETE), EventType.ISSUE_GENERICEVENT_ID));
		check = ConsistencyCheckLogHelper.getCheck(knowledgeElement);
		assertTrue("Now a pending check should exist.", check.isPresent());
		reset();

		ConfigPersistenceManager.setActivationStatusOfQualityEvent(knowledgeElement.getProject().getProjectKey(), "done", false);
		eventListener.onIssueEvent(generateWorkflowIssueEvent((MutableIssue) knowledgeElement.getJiraIssue(), user, jiraComment, "Done", StatusCategoryImpl.findByKey(StatusCategory.COMPLETE), EventType.ISSUE_GENERICEVENT_ID));
		check = ConsistencyCheckLogHelper.getCheck(knowledgeElement);
		assertFalse("No pending check should exist.", check.isPresent());
		ConfigPersistenceManager.setActivationStatusOfQualityEvent(knowledgeElement.getProject().getProjectKey(), "done", true);

		eventListener.onIssueEvent(generateWorkflowIssueEvent((MutableIssue) knowledgeElement.getJiraIssue(), user, jiraComment, "Open", StatusCategoryImpl.findByKey(StatusCategory.IN_PROGRESS), EventType.ISSUE_GENERICEVENT_ID));

		assertFalse("After resetting the workflow the chock does no longer need approval.", ConsistencyCheckLogHelper.doesKnowledgeElementNeedApproval(knowledgeElement));



	}

	@AfterEach
	public void reset() {
		ConsistencyCheckLogHelper.deleteCheck(knowledgeElement);
	}


}
