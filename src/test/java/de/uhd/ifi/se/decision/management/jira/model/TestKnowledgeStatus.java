package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestKnowledgeStatus {

	@Test
	public void testGetKnowledgeStatusNullOrEmpty() {
		assertEquals(KnowledgeStatus.UNDEFINED, KnowledgeStatus.getKnowledgeStatus(null));
		assertEquals(KnowledgeStatus.UNDEFINED, KnowledgeStatus.getKnowledgeStatus(""));
	}

	@Test
	public void testGetKnowledgeStatusUnknown() {
		assertEquals(KnowledgeStatus.UNDEFINED, KnowledgeStatus.getKnowledgeStatus("open"));
	}

	@Test
	public void testGetKnowledgeStatusKnown() {
		assertEquals(KnowledgeStatus.REJECTED, KnowledgeStatus.getKnowledgeStatus("rejected"));
	}

	@Test
	public void testToList() {
		assertEquals(9, KnowledgeStatus.toStringList().size());
	}

	@Test
	public void testGetDefaultStatus() {
		assertEquals(KnowledgeStatus.IDEA, KnowledgeStatus.getDefaultStatus(KnowledgeType.ALTERNATIVE));
		assertEquals(KnowledgeStatus.DECIDED, KnowledgeStatus.getDefaultStatus(KnowledgeType.DECISION));
		assertEquals(KnowledgeStatus.UNRESOLVED, KnowledgeStatus.getDefaultStatus(KnowledgeType.ISSUE));
		assertEquals(KnowledgeStatus.UNDEFINED, KnowledgeStatus.getDefaultStatus(KnowledgeType.ARGUMENT));
		assertEquals(KnowledgeStatus.UNDEFINED, KnowledgeStatus.getDefaultStatus(null));
	}

	@Test
	public void testGetColor() {
		assertEquals("crimson", KnowledgeStatus.UNRESOLVED.getColor());
		assertEquals("gray", KnowledgeStatus.DISCARDED.getColor());
		assertEquals("gray", KnowledgeStatus.REJECTED.getColor());
		assertEquals("crimson", KnowledgeStatus.CHALLENGED.getColor());
		assertEquals("", KnowledgeStatus.UNDEFINED.getColor());
	}
}
