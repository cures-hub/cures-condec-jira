package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import smile.math.MathEx;

public class TestPreprocessedData extends TestSetUp {

	private TrainingData trainingData;

	@Before
	public void setUp() {
		init();
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainingData = trainer.getTrainingData();
	}

	@Test
	public void testTrainingDataNotNull() {
		assertNotNull(trainingData);
	}

	@Test
	public void testBinary() {
		assertEquals(41, trainingData.labelsIsRelevant.length);
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		assertEquals(104, preprocessedData.preprocessedSentences.length);
		assertEquals(104, preprocessedData.updatedLabels.length);
		// assertEquals(0.36143, preprocessedData.preprocessedSentences[0][0]);
		assertEquals(1, preprocessedData.updatedLabels[0]);
		assertEquals(1, preprocessedData.updatedLabels[1]);
		assertEquals(1, preprocessedData.updatedLabels[2]);
		assertEquals(1, preprocessedData.updatedLabels[3]);
		assertEquals(0, preprocessedData.updatedLabels[32]);
	}

	@Test
	public void testFineGrained() {
		assertEquals(29, trainingData.labelsKnowledgeType.length);
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, true);
		assertEquals(74, preprocessedData.preprocessedSentences.length);
		assertEquals(74, preprocessedData.updatedLabels.length);
		// assertEquals(0.36143, preprocessedData.preprocessedSentences[0][0]);
		assertEquals(4, preprocessedData.updatedLabels[0]);
		assertEquals(4, preprocessedData.updatedLabels[1]);
		assertEquals(0, preprocessedData.updatedLabels[10]);
		int[] uniqueLabels = MathEx.unique(preprocessedData.updatedLabels);
		assertEquals(5, uniqueLabels.length);
		assertEquals(preprocessedData.preprocessedSentences.length, preprocessedData.updatedLabels.length);
		assertNotNull(preprocessedData.preprocessedSentences[preprocessedData.updatedLabels.length - 1]);
	}

}