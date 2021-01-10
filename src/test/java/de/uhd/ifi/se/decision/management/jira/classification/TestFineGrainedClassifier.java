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
		FineGrainedClassifier fineGrainedClassifier = TextClassifier.getInstance()
				.getFineGrainedClassifier();
		File file = fineGrainedClassifier.saveToFile();
		assertTrue(file.exists());
		assertTrue(fineGrainedClassifier.loadFromFile());
		assertTrue(fineGrainedClassifier.isTrained());
		file.delete();
	}

	@Test
	public void testModeFineGrainedClassification() {
		int[] fineGrainedClassificationResult = new int[4];
		fineGrainedClassificationResult[0] = 4;
		assertEquals(0, AbstractClassifier.mode(fineGrainedClassificationResult));
		fineGrainedClassificationResult[1] = 4;
		assertEquals(0, AbstractClassifier.mode(fineGrainedClassificationResult));
		fineGrainedClassificationResult[2] = 4;
		assertEquals(4, AbstractClassifier.mode(fineGrainedClassificationResult));
	}
}
