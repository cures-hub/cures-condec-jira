package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.MockEventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import net.java.ao.EntityManager;

public class TestSetUpEventListener extends TestSetUpWithIssues {

	private EntityManager entityManager;
	protected MutableIssue jiraIssue;
	private ApplicationUser user;

	protected DecXtractEventListener listener;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		EventPublisher publisher = new MockEventPublisher();
		listener = new DecXtractEventListener(publisher);
		jiraIssue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
	}

	protected Comment createComment(String comment) {
		return ComponentAccessor.getCommentManager().create(jiraIssue, user, comment, true);
	}

	protected IssueEvent createIssueEvent(String comment, long eventType) {
		Comment jiraComment = ComponentAccessor.getCommentManager().create(jiraIssue, user, comment, true);
		return createIssueEvent(jiraComment, eventType);
	}

	protected IssueEvent createIssueEvent(Comment comment, long eventType) {
		return new IssueEvent(jiraIssue, user, comment, null, new MockGenericValue("test"), new HashMap<String, String>(),
				eventType);
	}

	protected boolean isCommentExistent(String oldComment) {
		List<Comment> changedComments = ComponentAccessor.getCommentManager().getComments(jiraIssue);
		for (Comment comment : changedComments) {
			if (comment.getBody().equalsIgnoreCase(oldComment)) {
				return true;
			}
		}
		return false;
	}

	protected DecisionKnowledgeElement getFirstElementInComment(Comment comment) {
		List<DecisionKnowledgeElement> elements = JiraIssueTextPersistenceManager
				.getElementsForComment(comment.getId());
		if (elements.size() > 0) {
			return elements.get(0);
		}
		return null;
	}
}
