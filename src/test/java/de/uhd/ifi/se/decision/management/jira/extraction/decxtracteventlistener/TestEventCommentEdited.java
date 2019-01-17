package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.mock.ofbiz.MockGenericValue;
import org.junit.Test;

import java.util.HashMap;

public class TestEventCommentEdited extends TestSetUpEventListener {
    private IssueEvent issueEvent = new IssueEvent(issue,user,comment,null, new MockGenericValue("test"),new HashMap(), EventType.ISSUE_COMMENT_EDITED_ID);

    @Test
    public void testNoCommentContain(){
        comment = ComponentAccessor.getCommentManager().create(issue, user, "", true);
        listener.onIssueEvent(issueEvent);
    }

    @Test
    public void testRationalTag(){
        comment = ComponentAccessor.getCommentManager().create(issue, user, "{issue} fewfwewf{/issue}", true);
        listener.onIssueEvent(issueEvent);
    }

    @Test
    public void testExclusindTags(){
        comment = ComponentAccessor.getCommentManager().create(issue, user, "{code}fviowerf{/code}", true);
        listener.onIssueEvent(issueEvent);
    }

    @Test
    public void testRationalIcons(){
        comment = ComponentAccessor.getCommentManager().create(issue, user, "(!) rtwefhowiuhf", true);
        listener.onIssueEvent(issueEvent);
    }
}
