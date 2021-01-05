package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassifierTrainer;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessedData;
import smile.math.MathEx;

public class TestPreprocessedData extends TestSetUp {

	private TrainingData trainingData;

	@Before
	public void setUp() {
		init();
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainingData = new TrainingData(trainer.getDataFrame());
	}

	@Test
	public void testTrainingDataNotNull() {
		assertNotNull(trainingData);
	}

	@Test
	public void testBinary() {
		assertEquals(40, trainingData.labelsIsRelevant.length);
		PreprocessedData preprocessedData = new PreprocessedData(trainingData, false);
		assertEquals(274, preprocessedData.preprocessedSentences.length);
		assertEquals(274, preprocessedData.updatedLabels.length);
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
		assertEquals(213, preprocessedData.preprocessedSentences.length);
		assertEquals(213, preprocessedData.updatedLabels.length);
		// assertEquals(0.36143, preprocessedData.preprocessedSentences[0][0]);
		assertEquals(4, preprocessedData.updatedLabels[0]);
		assertEquals(4, preprocessedData.updatedLabels[1]);
		assertEquals(4, preprocessedData.updatedLabels[10]);
		int[] uniqueLabels = MathEx.unique(preprocessedData.updatedLabels);
		assertEquals(5, uniqueLabels.length);
		assertEquals(preprocessedData.preprocessedSentences.length, preprocessedData.updatedLabels.length);
		System.out.print(Arrays.toString(preprocessedData.preprocessedSentences));
		// System.out.println(Arrays.toString(preprocessedData.updatedLabels));
		assertNotNull(preprocessedData.preprocessedSentences[preprocessedData.updatedLabels.length - 1]);

	}

}
