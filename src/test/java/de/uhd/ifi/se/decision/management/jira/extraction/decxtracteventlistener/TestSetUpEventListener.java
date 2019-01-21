package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import java.util.HashMap;

import org.junit.Before;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.MockEventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;

public class TestSetUpEventListener extends TestSetUpWithIssues {

	private EntityManager entityManager;

	protected DecXtractEventListener listener;
	protected EventPublisher publisher;
	protected Issue issue;
	protected Comment comment;
	protected ApplicationUser user;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		publisher = new MockEventPublisher();
		listener = new DecXtractEventListener(publisher);
		issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
	}

	protected IssueEvent createIssueEvent(String comment, long eventType) {
		Comment jiraComment = ComponentAccessor.getCommentManager().create(issue, user, comment, true);
		return createIssueEvent(jiraComment, eventType);
	}
	
	private IssueEvent createIssueEvent(Comment comment, long eventType) {
		return new IssueEvent(issue, user, comment, null, new MockGenericValue("test"), new HashMap<String, String>(),
				eventType);
	}
}
