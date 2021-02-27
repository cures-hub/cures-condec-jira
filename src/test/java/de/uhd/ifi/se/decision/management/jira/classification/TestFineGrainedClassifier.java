package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestFineGrainedClassifier extends TestSetUp {

	private FineGrainedClassifier fineGrainedClassifier;

	@Before
	public void setUp() {
		init();
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainer.train();
		fineGrainedClassifier = TextClassifier.getInstance("TEST").getFineGrainedClassifier();
	}

	@Test
	@NonTransactional
	public void testPredictSingleSentence() {
		assertEquals(KnowledgeType.ISSUE, fineGrainedClassifier.predict("How can we implement?"));
	}

	@Test
	@NonTransactional
	public void testPredictSentences() {
		List<String> sentences = new ArrayList<>();
		sentences.add("The decision was made to use a SVM.");
		sentences.add("The code will be less clear.");
		assertEquals(KnowledgeType.DECISION, fineGrainedClassifier.predict(sentences).get(0));
		assertEquals(KnowledgeType.CON, fineGrainedClassifier.predict(sentences).get(1));
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
	public void testMapIndexToKnowledgeType() {
		assertEquals(KnowledgeType.ALTERNATIVE, FineGrainedClassifier.mapIndexToKnowledgeType(0));
		assertEquals(KnowledgeType.PRO, FineGrainedClassifier.mapIndexToKnowledgeType(1));
		assertEquals(KnowledgeType.CON, FineGrainedClassifier.mapIndexToKnowledgeType(2));
		assertEquals(KnowledgeType.DECISION, FineGrainedClassifier.mapIndexToKnowledgeType(3));
		assertEquals(KnowledgeType.ISSUE, FineGrainedClassifier.mapIndexToKnowledgeType(4));
		assertEquals(KnowledgeType.OTHER, FineGrainedClassifier.mapIndexToKnowledgeType(-1));
	}

	@Test
	@NonTransactional
	public void testSaveToAndLoadFromFile() {
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
