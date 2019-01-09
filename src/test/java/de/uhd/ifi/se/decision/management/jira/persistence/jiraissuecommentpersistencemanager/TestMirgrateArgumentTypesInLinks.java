package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuecommentpersistencemanager;

import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestMirgrateArgumentTypesInLinks extends TestJiraIssueCommentPersistenceMangerSetUp{

    @Test
    @NonTransactional
    public void testProjectKeyNull(){
        manager.migrateArgumentTypesInLinks(null);
        assertTrue(true);
    }

    @Test
    @NonTransactional
    public void testProjectKeyEmpty(){
        manager.migrateArgumentTypesInLinks("");
        assertTrue(true);
    }

    @Test
    @NonTransactional
    public void testPrjectKeyFilled(){
        manager.migrateArgumentTypesInLinks("TEST");
        assertTrue(true);
    }
}
