package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestDecisionKnowledgeAbstractClassifier extends TestSetUp {

	private DecisionKnowledgeClassifier decisionKnowledgeClassifier;

	@Before
	public void setUp() {
		init();
		decisionKnowledgeClassifier = DecisionKnowledgeClassifier.getInstance();
	}

	@Test
	@NonTransactional
	public void testGetTypeAlternative() {
		double[] classification = { 1.0, 0.0, 0.0, 0.0, 0.0 };
		KnowledgeType type = this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapIndexToKnowledgeType(this.decisionKnowledgeClassifier.getFineGrainedClassifier().maxAtInArray(classification));
		assertEquals(KnowledgeType.ALTERNATIVE, type);
	}

	@Test
	@NonTransactional
	public void testGetTypePro() {
		double[] classification = { .0, 1.0, 0.0, 0.0, 0.0 };
		KnowledgeType type = this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapIndexToKnowledgeType(this.decisionKnowledgeClassifier.getFineGrainedClassifier().maxAtInArray(classification));
		assertEquals(KnowledgeType.PRO, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeCon() {
		double[] classification = { .0, .0, 1.0, 0.0, 0.0 };
		KnowledgeType type = this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapIndexToKnowledgeType(this.decisionKnowledgeClassifier.getFineGrainedClassifier().maxAtInArray(classification));
		assertEquals(KnowledgeType.CON, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeDecision() {
		double[] classification = { .0, 0.0, 0.0, 1.0, 0.0 };
		KnowledgeType type = this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapIndexToKnowledgeType(this.decisionKnowledgeClassifier.getFineGrainedClassifier().maxAtInArray(classification));
		assertEquals(KnowledgeType.DECISION, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeIssue() {
		double[] classification = { .0, 0.0, 0.0, .0, 1.0 };
		KnowledgeType type = this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapIndexToKnowledgeType(this.decisionKnowledgeClassifier.getFineGrainedClassifier().maxAtInArray(classification));
		assertEquals(KnowledgeType.ISSUE, type);
	}

	@Test
	@NonTransactional
	public void testIsRelevant() {
		assertTrue(this.decisionKnowledgeClassifier.getBinaryClassifier().isRelevant(new double[] { 0.2, 0.8 }));
		assertFalse(this.decisionKnowledgeClassifier.getBinaryClassifier().isRelevant(new double[] { 0.8, 0.2 }));
	}

	@Test
	@NonTransactional
	public void testMapKnowledgeTypeToIndex() {
		assertEquals(Integer.valueOf(0), this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapKnowledgeTypeToIndex(KnowledgeType.ALTERNATIVE));
		assertEquals(Integer.valueOf(1), this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapKnowledgeTypeToIndex(KnowledgeType.PRO));
		assertEquals(Integer.valueOf(2), this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapKnowledgeTypeToIndex(KnowledgeType.CON));
		assertEquals(Integer.valueOf(3), this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapKnowledgeTypeToIndex(KnowledgeType.DECISION));
		assertEquals(Integer.valueOf(4), this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapKnowledgeTypeToIndex(KnowledgeType.ISSUE));
		assertEquals(Integer.valueOf(-1), this.decisionKnowledgeClassifier.getFineGrainedClassifier().mapKnowledgeTypeToIndex(KnowledgeType.OTHER));
	}


}
