package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;

public class TestMirgrateArgumentTypesInLinks extends TestJiraIssueCommentPersistenceManagerSetUp{

    @Test
    @NonTransactional
    public void testProjectKeyNull(){
    	JiraIssueCommentPersistenceManager.migrateArgumentTypesInLinks(null);
    }

    @Test
    @NonTransactional
    public void testProjectKeyEmpty(){
    	JiraIssueCommentPersistenceManager.migrateArgumentTypesInLinks("");
    }

    @Test
    @NonTransactional
    public void testPrjectKeyFilled(){
    	JiraIssueCommentPersistenceManager.migrateArgumentTypesInLinks("TEST");
    }
}
