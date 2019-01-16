package de.uhd.ifi.se.decision.management.jira.extraction.decxtracteventlistener;

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
public class TestOnIssueEventIssueComment extends TestSetUpEventListener {

    @Test
    public void testNoCommentContain(){
        IssueEvent issueEvent = new IssueEvent(issue,user,comment,null, new MockGenericValue("test"),new HashMap(),EventType.ISSUE_COMMENTED_ID);
        listener.onIssueEvent(issueEvent);
    }

}
