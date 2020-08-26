package de.uhd.ifi.se.decision.management.jira.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLinkType {

	@Test
	public void testLinkTypeToString() {
		assertEquals("relate", LinkType.RELATE.toString());
	}

	@Test
	public void testLinkTypeToList() {
		assertEquals(11, LinkType.toStringSet().size());
	}
}
