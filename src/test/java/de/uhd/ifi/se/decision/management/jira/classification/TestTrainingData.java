package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;

public class TestTrainingData extends TestSetUp {

	private TrainingData trainingData;

	@Before
	public void setUp() {
		init();
		File trainingFile = TestClassifierTrainer.getTestTrainingDataFile();
		trainingData = new TrainingData(trainingFile);
	}

	@Test
	public void testTraingDataNotNull() {
		assertNotNull(trainingData);
	}

	@Test
	public void testTrainingData() {
		assertEquals(41, trainingData.sentences.length);
		assertEquals(41, trainingData.labelsIsRelevant.length);
		assertEquals(29, trainingData.relevantSentences.length);

		assertEquals("How can we implement?", trainingData.sentences[0]);
		assertEquals(1, trainingData.labelsIsRelevant[0]);
		assertEquals(4, trainingData.labelsKnowledgeType[0]);

		assertEquals(1, trainingData.labelsIsRelevant[1]);
		assertEquals(4, trainingData.labelsKnowledgeType[1]);

		assertEquals("Nobody knows.", trainingData.sentences[6]);
		assertEquals(0, trainingData.labelsIsRelevant[6]);
	}

}
