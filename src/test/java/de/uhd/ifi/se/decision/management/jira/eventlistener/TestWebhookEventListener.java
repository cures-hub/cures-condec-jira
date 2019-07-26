package de.uhd.ifi.se.decision.management.jira.eventlistener;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.MockEventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.issue.link.IssueLinkCreatedEvent;
import com.atlassian.jira.event.issue.link.IssueLinkDeletedEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLink;

public class TestWebhookEventListener extends TestSetUpWithIssues {

	private ConDecEventListener listener;
	private ApplicationUser user;
	private Issue issue;
	private Comment jiraComment;

	@Before
	public void setUp() {
		TestSetUpWithIssues.initialization();
		EventPublisher publisher = new MockEventPublisher();
		listener = new ConDecEventListener(publisher);
		issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		user = ComponentAccessor.getUserManager().getUserByName("SysAdmin");
		jiraComment = ComponentAccessor.getCommentManager().create(issue, user, "Test Comment", true);
	}

	@Test
	public void testCreation() {
		assertNotNull(listener);
	}

	@Test
	public void testAfterPropertiesSet() throws Exception {
		listener.afterPropertiesSet();
	}

	@Test
	public void testDestroy() throws Exception {
		listener.destroy();
	}

	@Test
	public void testIssueCreated() {
		IssueEvent event = new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
				new HashMap<String, String>(), EventType.ISSUE_CREATED_ID);
		listener.onIssueEvent(event);
	}

	@Test
	public void testIssueUpdated() {
		IssueEvent event = new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
				new HashMap<String, String>(), EventType.ISSUE_UPDATED_ID);
		listener.onIssueEvent(event);
	}

	@Test
	public void testIssueDeleted() {
		IssueEvent event = new IssueEvent(issue, user, jiraComment, null, new MockGenericValue("test"),
				new HashMap<String, String>(), EventType.ISSUE_DELETED_ID);
		listener.onIssueEvent(event);
	}

	@Test
	public void testIssueLinkCreated() {
		IssueLink link = new MockIssueLink(1);
		IssueLinkCreatedEvent event = new IssueLinkCreatedEvent(link, null);
		listener.onLinkCreatedIssueEvent(event);
	}

	@Test
	public void testIssueLinkDeleted() {
		IssueLink link = new MockIssueLink(1);
		IssueLinkDeletedEvent event = new IssueLinkDeletedEvent(link, null);
		listener.onLinkDeletedIssueEvent(event);
	}
}
