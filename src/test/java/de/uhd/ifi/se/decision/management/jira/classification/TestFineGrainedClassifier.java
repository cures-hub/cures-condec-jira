package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestFineGrainedClassifier extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testGetTypeAlternative() {
		double[] classification = { 1.0, 0.0, 0.0, 0.0, 0.0 };
		KnowledgeType type = FineGrainedClassifier.getType(classification);
		assertEquals(KnowledgeType.ALTERNATIVE, type);
	}

	@Test
	@NonTransactional
	public void testGetTypePro() {
		double[] classification = { .0, 1.0, 0.0, 0.0, 0.0 };
		KnowledgeType type = FineGrainedClassifier.getType(classification);
		assertEquals(KnowledgeType.PRO, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeCon() {
		double[] classification = { .0, .0, 1.0, 0.0, 0.0 };
		KnowledgeType type = FineGrainedClassifier.getType(classification);
		assertEquals(KnowledgeType.CON, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeDecision() {
		double[] classification = { .0, 0.0, 0.0, 1.0, 0.0 };
		KnowledgeType type = FineGrainedClassifier.getType(classification);
		assertEquals(KnowledgeType.DECISION, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeIssue() {
		double[] classification = { .0, 0.0, 0.0, .0, 1.0 };
		KnowledgeType type = FineGrainedClassifier.getType(classification);
		assertEquals(KnowledgeType.ISSUE, type);
	}

	@Test
	@NonTransactional
	public void testMapKnowledgeTypeToIndex() {
		assertEquals(0, FineGrainedClassifier.mapKnowledgeTypeToIndex(KnowledgeType.ALTERNATIVE));
		assertEquals(1, FineGrainedClassifier.mapKnowledgeTypeToIndex(KnowledgeType.PRO));
		assertEquals(2, FineGrainedClassifier.mapKnowledgeTypeToIndex(KnowledgeType.CON));
		assertEquals(3, FineGrainedClassifier.mapKnowledgeTypeToIndex(KnowledgeType.DECISION));
		assertEquals(4, FineGrainedClassifier.mapKnowledgeTypeToIndex(KnowledgeType.ISSUE));
		assertEquals(-1, FineGrainedClassifier.mapKnowledgeTypeToIndex(KnowledgeType.OTHER));
	}

	@Test
	@NonTransactional
	public void testSaveToAndLoadFromFile() {
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainer.train();
		FineGrainedClassifier fineGrainedClassifier = DecisionKnowledgeClassifier.getInstance()
				.getFineGrainedClassifier();
		File file = fineGrainedClassifier.saveToFile();
		assertTrue(file.exists());
		assertTrue(fineGrainedClassifier.loadFromFile());
		assertTrue(fineGrainedClassifier.isModelTrained());
		file.delete();
	}
}
