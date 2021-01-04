package de.uhd.ifi.se.decision.management.jira.classification;

import static de.uhd.ifi.se.decision.management.jira.classification.TestOnlineTrainer.getTrainingData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import weka.core.Instances;

public class TestTrainingData extends TestSetUp {

	private Instances wekaInstances;

	@Before
	public void setUp() {
		init();
		OnlineFileTrainerImpl trainer = new OnlineFileTrainerImpl("TEST");
		trainer.setTrainingData(getTrainingData());
		wekaInstances = trainer.getInstances();
	}

	@Test
	public void testInstancesNotNull() {
		assertNotNull(wekaInstances);
	}

	@Test
	public void testTrainingData() {
		TrainingData trainingData = new TrainingData(wekaInstances);
		assertEquals(24, trainingData.sentences.length);
		assertEquals(24, trainingData.labelsIsRelevant.length);
		assertEquals(20, trainingData.relevantSentences.length);

		assertEquals("Issue", trainingData.sentences[0]);
		assertEquals(1, trainingData.labelsIsRelevant[0]);
		assertEquals(4, trainingData.labelsKnowledgeType[0]);

		assertEquals("Decision", trainingData.sentences[1]);
		assertEquals(1, trainingData.labelsIsRelevant[1]);
		assertEquals(3, trainingData.labelsKnowledgeType[1]);

		assertEquals("Hello", trainingData.sentences[23]);
		assertEquals(0, trainingData.labelsIsRelevant[23]);
	}

}
