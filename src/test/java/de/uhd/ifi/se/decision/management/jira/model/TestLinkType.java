package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestLinkType {

	@Test
	public void testLinkTypeToString() {
		assertEquals("relate", LinkType.RELATE.toString());
	}

	@Test
	public void testLinkTypeToList() {
		assertEquals(10, LinkType.toStringList().size());
	}
}
