package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.MockEventPublisher;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import org.junit.Before;


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
        comment = ComponentAccessor.getCommentManager().create(issue, user, comment, true);
        user = ComponentAccessor.getUserManager().getUserByName("NoFails");
    }
}
