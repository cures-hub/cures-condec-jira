package de.uhd.ifi.se.decision.management.jira.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLinkType {

    @Test
    public void testLinkTypeToString(){
        assertEquals("contain", LinkType.CONTAIN.toString());
    }

    @Test
    public void testLinkTypeToList(){
        assertEquals(3, LinkType.toList().size(), 0.0);
    }
}
