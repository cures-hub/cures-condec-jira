package de.uhd.ifi.se.decision.documentation.jira.persistence.IssueStrategyTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TestGetDecisionKnowledgeElements extends TestIssueStrategySetUp {

    @Test
    public void testKeyNull(){
        assertNull(issueStrategy.getDecisionKnowledgeElements(null));
    }

    @Test
    public void testKeyNotExistend(){
        assertEquals(0,issueStrategy.getDecisionKnowledgeElements("NOTExistend").size());
    }

    @Test
    public void testKeyExistend(){
        assertEquals(13,issueStrategy.getDecisionKnowledgeElements("TEST").size());
    }
}
