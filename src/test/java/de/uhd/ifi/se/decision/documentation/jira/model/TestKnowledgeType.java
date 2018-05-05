package de.uhd.ifi.se.decision.documentation.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Test;

public class TestKnowledgeType {
	@Test
	public void testGetKnowledgeTypeNull() {
		assertEquals(KnowledgeType.OTHER, KnowledgeType.getKnowledgeType(null));
	}

	@Test
	public void testGetKnowledgeTypeNotExistend() {
		assertEquals(KnowledgeType.OTHER, KnowledgeType.getKnowledgeType("Test"));
	}

	@Test
	public void testGetKnowledgeTypeExistend() {
		for (KnowledgeType type : KnowledgeType.values()) {
			assertEquals(type, KnowledgeType.getKnowledgeType(type.toString()));
		}
	}

	@Test
	public void testGetSuperTypeNull() {
		assertNull(KnowledgeType.getSuperType(null));
	}

	@Test
	public void testGetSuperTypeNotCovered() {
		assertEquals(KnowledgeType.CONTEXT, KnowledgeType.getSuperType(KnowledgeType.CONTEXT));
	}

	@Test
	public void testGetSuperTypeCovered() {
		assertEquals(KnowledgeType.PROBLEM, KnowledgeType.getSuperType(KnowledgeType.ISSUE));
		assertEquals(KnowledgeType.PROBLEM, KnowledgeType.getSuperType(KnowledgeType.GOAL));
		assertEquals(KnowledgeType.SOLUTION, KnowledgeType.getSuperType(KnowledgeType.ALTERNATIVE));
		assertEquals(KnowledgeType.SOLUTION, KnowledgeType.getSuperType(KnowledgeType.CLAIM));
		assertEquals(KnowledgeType.CONTEXT, KnowledgeType.getSuperType(KnowledgeType.CONSTRAINT));
		assertEquals(KnowledgeType.CONTEXT, KnowledgeType.getSuperType(KnowledgeType.ASSUMPTION));
		assertEquals(KnowledgeType.CONTEXT, KnowledgeType.getSuperType(KnowledgeType.IMPLICATION));
		assertEquals(KnowledgeType.RATIONALE, KnowledgeType.getSuperType(KnowledgeType.ARGUMENT));
		assertEquals(KnowledgeType.RATIONALE, KnowledgeType.getSuperType(KnowledgeType.ASSESSMENT));
		assertEquals(KnowledgeType.PROBLEM, KnowledgeType.ISSUE.getSuperType());
	}

	@Test
	public void testToString() {
		assertEquals("Issue", KnowledgeType.ISSUE.toString());
	}

	@Test
	public void testToList() {
		assertEquals(ArrayList.class, KnowledgeType.toList().getClass());
	}
}
