package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestLinkType {

	@Test
	public void testToString() {
		assertEquals("relate", LinkType.RELATE.toString());
	}

	@Test
	public void testToStringSet() {
		assertEquals(13, LinkType.toStringSet().size());
	}

	@Test
	public void testGetOutwardName() {
		assertEquals("relates to", LinkType.RELATE.getOutwardName());
	}

	@Test
	public void testGetInwardName() {
		assertEquals("relates to", LinkType.RELATE.getInwardName());
	}

	@Test
	public void testGetLinkType() {
		assertEquals(LinkType.RELATE, LinkType.getLinkType(null)); // default
		assertEquals(LinkType.SUPPORT, LinkType.getLinkType("supports"));
		assertEquals(LinkType.ATTACK, LinkType.getLinkType("attacks"));
		assertEquals(LinkType.ATTACK, LinkType.getLinkType("Attack"));
		assertEquals(LinkType.RELATE, LinkType.getLinkType("relates"));
		assertEquals(LinkType.RELATE, LinkType.getLinkType("relate"));
		assertEquals(LinkType.OTHER, LinkType.getLinkType("jira_issue_link"));
	}

	@Test
	public void testGetLinkTypeForKnowledgeType() {
		assertEquals(LinkType.SUPPORT, LinkType.getLinkTypeForKnowledgeType(KnowledgeType.PRO));
		assertEquals(LinkType.ATTACK, LinkType.getLinkTypeForKnowledgeType(KnowledgeType.CON));
		assertEquals(LinkType.RELATE, LinkType.getLinkTypeForKnowledgeType(KnowledgeType.DECISION));
	}

	@Test
	public void testGetLinkTypeColor() {
		assertEquals("#80c9ff", LinkType.getLinkTypeColor("relate"));
		assertEquals("#00994C", LinkType.getLinkTypeColor("Supports"));
		assertEquals("", "");
		assertEquals("#bd3525", LinkType.getLinkTypeColor("jira_subtask_link"));
		assertEquals("#15ceb6", LinkType.getLinkTypeColor("clones"));
	}

	@Test
	public void testDefaultTypes() {
		assertEquals(2, LinkType.getDefaultTypes().size());
	}
}