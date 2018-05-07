package de.uhd.ifi.se.decision.documentation.jira.persistence.IssueStrategyTest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGetUnlinkedDecisionComponents extends  TestIssueStrategySetUp {

    @Test
    public void tesIdNullKeyNull(){
        assertEquals(0,issueStrategy.getUnlinkedDecisionComponents((long)0,null).size());
    }

    @Test
    public void testIdNullKeyFilled(){
        assertEquals(0,issueStrategy.getUnlinkedDecisionComponents((long)0, "TEST").size());
    }

    @Test
    public void testIdFilledKeyFilled(){
        assertEquals(11,issueStrategy.getUnlinkedDecisionComponents((long)15,"TEST").size());
    }
}
