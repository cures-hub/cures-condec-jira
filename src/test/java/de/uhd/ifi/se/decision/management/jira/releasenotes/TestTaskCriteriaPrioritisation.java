package de.uhd.ifi.se.decision.management.jira.releasenotes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestTaskCriteriaPrioritisation {

	@Test
	public void testToString() {
		assertEquals("count_decision_knowledge", TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE.toString());
	}

	@Test
	public void getTaskCriteriaPrioritisation() {
		assertEquals(TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE, TaskCriteriaPrioritisation.getTaskCriteriaPrioritisation("count_decision_knowledge"));
		assertEquals(TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE, TaskCriteriaPrioritisation.getTaskCriteriaPrioritisation(null));
	}

	@Test
	public void toIntegerEnumMap() {
		assertEquals(8, TaskCriteriaPrioritisation.toIntegerEnumMap().size(), 0.0);
		assertEquals(0, TaskCriteriaPrioritisation.toIntegerEnumMap().get(TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE), 0.0);

	}

	@Test
	public void toDoubleEnumMap() {
		assertEquals(8, TaskCriteriaPrioritisation.toDoubleEnumMap().size(), 0.0);
		assertEquals(1.0, TaskCriteriaPrioritisation.toDoubleEnumMap().get(TaskCriteriaPrioritisation.COUNT_DECISION_KNOWLEDGE), 0.0);
	}

	@Test
	public void toList() {
		assertEquals(8, TaskCriteriaPrioritisation.toIntegerEnumMap().size(), 0.0);
	}

	@Test
	public void getOriginalList() {
		assertEquals(8, TaskCriteriaPrioritisation.toIntegerEnumMap().size(), 0.0);
	}
}