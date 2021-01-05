package de.uhd.ifi.se.decision.management.jira.classification;

import static de.uhd.ifi.se.decision.management.jira.classification.TestOnlineTrainer.getTrainingData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import smile.data.DataFrame;

public class TestTrainingData extends TestSetUp {

	private DataFrame dataFrame;

	@Before
	public void setUp() {
		init();
		OnlineFileTrainerImpl trainer = new OnlineFileTrainerImpl("TEST");
		trainer.setTrainingData(getTrainingData());
		File trainingFile = TestOnlineFileTrainerImpl.getTrimmedDefaultArffFile();
		dataFrame = OnlineFileTrainerImpl.getDataFrameFromArffFile(trainingFile);
	}

	@Test
	public void testInstancesNotNull() {
		assertNotNull(dataFrame);
	}

	@Test
	public void testTrainingData() {
		TrainingData trainingData = new TrainingData(dataFrame);
		assertEquals(40, trainingData.sentences.length);
		assertEquals(40, trainingData.labelsIsRelevant.length);
		assertEquals(29, trainingData.relevantSentences.length);

		assertEquals("How can we implement?", trainingData.sentences[0]);
		assertEquals(1, trainingData.labelsIsRelevant[0]);
		assertEquals(4, trainingData.labelsKnowledgeType[0]);

		assertEquals("Nobody knows.", trainingData.sentences[6]);
		assertEquals(0, trainingData.labelsIsRelevant[6]);
	}

}
