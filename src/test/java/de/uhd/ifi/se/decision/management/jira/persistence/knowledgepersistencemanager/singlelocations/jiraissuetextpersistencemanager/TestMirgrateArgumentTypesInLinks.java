package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.singlelocations.jiraissuetextpersistencemanager;

import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.persistence.impl.JiraIssueTextPersistenceManager;

public class TestMirgrateArgumentTypesInLinks extends TestJiraIssueCommentPersistenceManagerSetUp{

    @Test
    @NonTransactional
    public void testProjectKeyNull(){
    	JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks(null);
    }

    @Test
    @NonTransactional
    public void testProjectKeyEmpty(){
    	JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks("");
    }

    @Test
    @NonTransactional
    public void testPrjectKeyFilled(){
    	JiraIssueTextPersistenceManager.migrateArgumentTypesInLinks("TEST");
    }
}
