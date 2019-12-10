package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

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
		assertEquals(7, KnowledgeStatus.toList().size());
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
	public void testGetNewKnowledgeStatusForType() {
		DecisionKnowledgeElement formerElement = new DecisionKnowledgeElementImpl();
		formerElement.setType(KnowledgeType.ALTERNATIVE);
		DecisionKnowledgeElement newElement = new DecisionKnowledgeElementImpl();
		newElement.setType(KnowledgeType.DECISION);
		assertEquals(KnowledgeStatus.DECIDED, KnowledgeStatus.getNewKnowledgeStatusForType(formerElement, newElement));

		assertEquals(KnowledgeStatus.REJECTED,
				KnowledgeStatus.getNewKnowledgeStatusForType(KnowledgeType.DECISION, KnowledgeType.ALTERNATIVE, null));
		assertEquals(KnowledgeStatus.IDEA, KnowledgeStatus.getNewKnowledgeStatusForType(KnowledgeType.ALTERNATIVE,
				KnowledgeType.ALTERNATIVE, KnowledgeStatus.IDEA));
	}

	@Test
	public void testGetNewKnowledgeTypeForStatus() {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		assertEquals(KnowledgeType.OTHER, KnowledgeStatus.getNewKnowledgeTypeForStatus(element));
		assertEquals(KnowledgeType.OTHER, KnowledgeStatus.getNewKnowledgeTypeForStatus(null, null));

		element.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, KnowledgeStatus.getNewKnowledgeTypeForStatus(element));

		element.setStatus(KnowledgeStatus.DECIDED);
		assertEquals(KnowledgeType.DECISION, KnowledgeStatus.getNewKnowledgeTypeForStatus(element));

		assertEquals(KnowledgeType.ALTERNATIVE,
				KnowledgeStatus.getNewKnowledgeTypeForStatus(KnowledgeStatus.IDEA, KnowledgeType.DECISION));
	}

}
