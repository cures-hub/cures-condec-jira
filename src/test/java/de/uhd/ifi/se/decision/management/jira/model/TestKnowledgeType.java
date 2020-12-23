package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class TestKnowledgeType {

	@Test
	public void testGetKnowledgeTypeNull() {
		assertEquals(KnowledgeType.OTHER, KnowledgeType.getKnowledgeType(null));
	}

	@Test
	public void testGetKnowledgeTypeNonExistent() {
		assertEquals(KnowledgeType.OTHER, KnowledgeType.getKnowledgeType("Test"));
	}

	@Test
	public void testGetKnowledgeTypeExistent() {
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
		assertEquals(ArrayList.class, KnowledgeType.toStringList().getClass());
	}

	@Test
	public void testGetParentTypes() {
		assertTrue(KnowledgeType.getParentTypes(null).isEmpty());

		// parent types of arguments are either alternative or decision
		assertEquals(KnowledgeType.ALTERNATIVE, KnowledgeType.getParentTypes(KnowledgeType.ARGUMENT).get(0));
		assertEquals(KnowledgeType.DECISION, KnowledgeType.getParentTypes(KnowledgeType.ARGUMENT).get(1));

		// parent type of decision and alternative is issue
		assertEquals(KnowledgeType.ISSUE, KnowledgeType.getParentTypes(KnowledgeType.DECISION).get(0));
	}

	@Test
	public void testGetTag() {
		assertEquals("{issue}", KnowledgeType.ISSUE.getTag());
		assertEquals("", KnowledgeType.OTHER.getTag());
	}
}
