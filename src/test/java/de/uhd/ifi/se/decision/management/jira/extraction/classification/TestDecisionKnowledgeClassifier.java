package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DecisionKnowledgeClassifierImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDecisionKnowledgeClassifier extends TestSetUpWithIssues {

	@Before
	public void setUp() {
		initialization();
	}

	@Test
	@NonTransactional
	public void testGetTypeAlternative() {
		double[] classification = { 1.0, 0.0, 0.0, 0.0, 0.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.ALTERNATIVE, type);
	}

	@Test
	@NonTransactional
	public void testGetTypePro() {
		double[] classification = { .0, 1.0, 0.0, 0.0, 0.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.PRO, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeCon() {
		double[] classification = { .0, .0, 1.0, 0.0, 0.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.CON, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeDecision() {
		double[] classification = { .0, 0.0, 0.0, 1.0, 0.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.DECISION, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeIssue() {
		double[] classification = { .0, 0.0, 0.0, .0, 1.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.ISSUE, type);
	}

	@Test
	@NonTransactional
	public void testIsRelevant() {
		assertTrue(DecisionKnowledgeClassifierImpl.isRelevant(1.0));
		assertFalse(DecisionKnowledgeClassifierImpl.isRelevant(0.));
		assertFalse(DecisionKnowledgeClassifierImpl.isRelevant(0.4));
	}
}
