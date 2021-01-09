package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;
import smile.data.DataFrame;

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

	@Test
	@NonTransactional
	public void testDataFrame() {
		assertNotNull(trainingData.dataFrame);
		assertEquals(0, trainingData.dataFrame.columnIndex("isAlternative"));
	}

	@Test
	@NonTransactional
	public void testCreateTrainingDataFromKnowledgeElements() {
		DataFrame dataFrame = new TrainingData(KnowledgeElements.getTestKnowledgeElements()).dataFrame;
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testCreateTrainingRow() {
		Object[] rowValues = TrainingData.createTrainingRow(KnowledgeElements.getTestKnowledgeElement());
		assertEquals(0, rowValues[0]);
		assertEquals("WI: Implement feature", rowValues[5]);
	}

	@Test
	@NonTransactional
	public void testCreateTrainingDataFromFileName() {
		DataFrame dataFrame = new TrainingData("defaultTrainingData.csv").dataFrame;
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testSaveToFile() {
		File trainingFile = trainingData.saveToFile("TEST");
		assertTrue(trainingFile.exists());
		assertTrue(trainingFile.getName().startsWith("TEST"));
		trainingFile.delete();
	}

}
