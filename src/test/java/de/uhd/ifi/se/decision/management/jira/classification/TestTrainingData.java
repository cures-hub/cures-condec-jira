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
	@NonTransactional
	public void testTraingDataNotNull() {
		assertNotNull(trainingData);
	}

	@Test
	@NonTransactional
	public void testGetAllSentences() {
		assertEquals(41, trainingData.getAllSentences().length);
		assertEquals(41, trainingData.getRelevanceLabelsForAllSentences().length);

		assertEquals("How can we implement?", trainingData.getAllSentences()[0]);
		assertEquals(1, trainingData.getRelevanceLabelsForAllSentences()[0]);

		assertEquals(1, trainingData.getRelevanceLabelsForAllSentences()[1]);

		assertEquals("Nobody knows.", trainingData.getAllSentences()[6]);
	}

	@Test
	@NonTransactional
	public void testGetRelevantSentences() {
		assertEquals(29, trainingData.getRelevantSentences().length);
		assertEquals(29, trainingData.getKnowledgeTypeLabelsForRelevantSentences().length);

		assertEquals("How can we implement?", trainingData.getRelevantSentences()[0]);
		assertEquals(4, trainingData.getKnowledgeTypeLabelsForRelevantSentences()[0]);

		assertEquals(4, trainingData.getKnowledgeTypeLabelsForRelevantSentences()[1]);

		assertEquals("Alternatively we can implement it as that.", trainingData.getRelevantSentences()[6]);
		assertEquals(0, trainingData.getRelevanceLabelsForAllSentences()[6]);
	}

	@Test
	@NonTransactional
	public void testGetDataFrame() {
		assertNotNull(trainingData.getDataFrame());
		assertEquals(0, trainingData.getDataFrame().columnIndex("isAlternative"));
	}

	@Test
	@NonTransactional
	public void testCreateTrainingDataFromKnowledgeElements() {
		DataFrame dataFrame = new TrainingData(KnowledgeElements.getTestKnowledgeElements()).getDataFrame();
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
		DataFrame dataFrame = new TrainingData("defaultTrainingData.csv").getDataFrame();
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testCreateTrainingDataFromDafaultTrainingFile() {
		DataFrame dataFrame = new TrainingData().getDataFrame();
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

	@Test
	@NonTransactional
	public void testCreateFileNameProjectValid() {
		String fileName = TrainingData.createTrainingDataFileName("TEST");
		assertTrue(fileName.startsWith("TEST"));
	}

	@Test
	@NonTransactional
	public void testCreateFileNameProjectNull() {
		String fileName = TrainingData.createTrainingDataFileName(null);
		assertTrue(fileName.startsWith("-"));
	}

}
