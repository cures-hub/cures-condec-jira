package de.uhd.ifi.se.decision.management.jira.persistence.jiraissuepersistencemanager;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestDeleteLink extends TestJiraIssuePersistenceManagerSetUp {

    @Test
    public void testLinkNullUserNull(){
        assertFalse(issueStrategy.deleteLink(null, null));
    }

    @Test
    public void testLinkNullUserFilled(){
        assertFalse(issueStrategy.deleteLink(null, user));
    }

    @Test
    public void testLinkFilledUserNull(){
        assertFalse(issueStrategy.deleteLink(link, null));
    }

    @Test
    public void testLinkFilledUserFilled(){
        assertTrue(issueStrategy.deleteLink(link, user));
    }
}
