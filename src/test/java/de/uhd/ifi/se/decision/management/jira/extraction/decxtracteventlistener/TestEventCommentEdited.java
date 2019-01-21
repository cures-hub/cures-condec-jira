package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestEventCommentEdited extends TestSetUpEventListener {
    @Test
    public void testNoCommentContain(){
        comment = ComponentAccessor.getCommentManager().create(issue, user, "", true);
        IssueEvent issueEvent = new IssueEvent(issue,user,comment,null, new MockGenericValue("test"),new HashMap(), EventType.ISSUE_COMMENT_EDITED_ID);
        listener.onIssueEvent(issueEvent);
    }

    @Test
    public void testRationalTag(){
        comment = ComponentAccessor.getCommentManager().create(issue, user, "{issue} fewfwewf{/issue}", true);
        IssueEvent issueEvent = new IssueEvent(issue,user,comment,null, new MockGenericValue("test"),new HashMap(), EventType.ISSUE_COMMENT_EDITED_ID);
        listener.onIssueEvent(issueEvent);
    }

    @Test
    public void testExclusindTags(){
        comment = ComponentAccessor.getCommentManager().create(issue, user, "{code}fviowerf{/code}", true);
        IssueEvent issueEvent = new IssueEvent(issue,user,comment,null, new MockGenericValue("test"),new HashMap(), EventType.ISSUE_COMMENT_EDITED_ID);
        listener.onIssueEvent(issueEvent);
    }

    @Test
    public void testRationalIcons(){
        comment = ComponentAccessor.getCommentManager().create(issue, user, "(!) rtwefhowiuhf", true);
        IssueEvent issueEvent = new IssueEvent(issue,user,comment,null, new MockGenericValue("test"),new HashMap(), EventType.ISSUE_COMMENT_EDITED_ID);
        listener.onIssueEvent(issueEvent);
    }
}
