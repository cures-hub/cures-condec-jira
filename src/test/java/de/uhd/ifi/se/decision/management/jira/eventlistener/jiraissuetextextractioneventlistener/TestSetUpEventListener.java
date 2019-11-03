package de.uhd.ifi.se.decision.management.jira.eventlistener.jiraissuetextextractioneventlistener;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.MockEventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.eventlistener.ConDecEventListener;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public abstract class TestSetUpEventListener extends TestSetUp {

	protected MutableIssue jiraIssue;
	private ApplicationUser user;

	protected ConDecEventListener listener;

	@Before
	public void setUp() {
		init();
		EventPublisher publisher = new MockEventPublisher();
		listener = new ConDecEventListener(publisher);
		jiraIssue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("TEST-4");
		user = JiraUsers.SYS_ADMIN.getApplicationUser();
	}

	protected Comment createComment(String comment) {
		return ComponentAccessor.getCommentManager().create(jiraIssue, user, comment, true);
	}

	protected IssueEvent createIssueEvent(String comment, long eventType) {
		Comment jiraComment = ComponentAccessor.getCommentManager().create(jiraIssue, user, comment, true);
		return createIssueEvent(jiraComment, eventType);
	}

	protected IssueEvent createIssueEvent(Comment comment, long eventType) {
		return new IssueEvent(jiraIssue, user, comment, null, new MockGenericValue("test"),
				new HashMap<String, String>(), eventType);
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
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate("TEST")
				.getJiraIssueTextManager();
		List<DecisionKnowledgeElement> elements = persistenceManager.getElementsInComment(comment.getId());
		if (elements.size() > 0) {
			return elements.get(0);
		}
		return null;
	}

	@After
	public void tearDown() {
		jiraIssue.setDescription("");
	}
}
