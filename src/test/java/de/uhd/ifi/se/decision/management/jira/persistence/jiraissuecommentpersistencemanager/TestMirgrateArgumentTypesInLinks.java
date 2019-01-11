package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

public class TestMirgrateArgumentTypesInLinks extends TestJiraIssueCommentPersistenceMangerSetUp{

    @Test
    @NonTransactional
    public void testProjectKeyNull(){
        manager.migrateArgumentTypesInLinks(null);
    }

    @Test
    @NonTransactional
    public void testProjectKeyEmpty(){
        manager.migrateArgumentTypesInLinks("");
    }

    @Test
    @NonTransactional
    public void testPrjectKeyFilled(){
        manager.migrateArgumentTypesInLinks("TEST");
    }
}
