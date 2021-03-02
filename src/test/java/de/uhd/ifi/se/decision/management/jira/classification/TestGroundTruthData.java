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

public class TestGroundTruthData extends TestSetUp {

	private GroundTruthData groundTruthData;

	@Before
	public void setUp() {
		init();
		File trainingFile = TestTextClassifier.getTestTrainingDataFile();
		groundTruthData = new GroundTruthData(trainingFile);
	}

	@Test
	@NonTransactional
	public void testTraingDataNotNull() {
		assertNotNull(groundTruthData);
	}

	@Test
	@NonTransactional
	public void testGetAllSentences() {
		assertEquals(41, groundTruthData.getAllSentences().length);
		assertEquals(41, groundTruthData.getRelevanceLabelsForAllSentences().length);

		assertEquals("How can we implement?", groundTruthData.getAllSentences()[0]);
		assertEquals(1, groundTruthData.getRelevanceLabelsForAllSentences()[0]);

		assertEquals(1, groundTruthData.getRelevanceLabelsForAllSentences()[1]);

		assertEquals("Nobody knows.", groundTruthData.getAllSentences()[6]);
	}

	@Test
	@NonTransactional
	public void testGetRelevantSentences() {
		assertEquals(29, groundTruthData.getRelevantSentences().length);
		assertEquals(29, groundTruthData.getKnowledgeTypeLabelsForRelevantSentences().length);

		assertEquals("How can we implement?", groundTruthData.getRelevantSentences()[0]);
		assertEquals(4, groundTruthData.getKnowledgeTypeLabelsForRelevantSentences()[0]);

		assertEquals(4, groundTruthData.getKnowledgeTypeLabelsForRelevantSentences()[1]);

		assertEquals("Alternatively we can implement it as that.", groundTruthData.getRelevantSentences()[6]);
		assertEquals(0, groundTruthData.getRelevanceLabelsForAllSentences()[6]);
	}

	@Test
	@NonTransactional
	public void testGetDataFrame() {
		assertNotNull(groundTruthData.getDataFrame());
		assertEquals(0, groundTruthData.getDataFrame().columnIndex("isAlternative"));
	}

	@Test
	@NonTransactional
	public void testCreateTrainingDataFromKnowledgeElements() {
		DataFrame dataFrame = new GroundTruthData(KnowledgeElements.getTestKnowledgeElements()).getDataFrame();
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testCreateTrainingRow() {
		Object[] rowValues = GroundTruthData.createTrainingRow(KnowledgeElements.getTestKnowledgeElement());
		assertEquals(0, rowValues[0]);
		assertEquals("WI: Implement feature", rowValues[5]);
	}

	@Test
	@NonTransactional
	public void testCreateTrainingDataFromFileName() {
		DataFrame dataFrame = new GroundTruthData("defaultTrainingData.csv").getDataFrame();
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testCreateTrainingDataFromDafaultTrainingFile() {
		DataFrame dataFrame = new GroundTruthData().getDataFrame();
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testSaveToFile() {
		File trainingFile = groundTruthData.saveToFile("TEST");
		assertTrue(trainingFile.exists());
		assertTrue(trainingFile.getName().startsWith("TEST"));
		trainingFile.delete();
	}

	@Test
	@NonTransactional
	public void testCreateFileNameProjectValid() {
		String fileName = GroundTruthData.createTrainingDataFileName("TEST");
		assertTrue(fileName.startsWith("TEST"));
	}

	@Test
	@NonTransactional
	public void testCreateFileNameProjectNull() {
		String fileName = GroundTruthData.createTrainingDataFileName(null);
		assertTrue(fileName.startsWith("-"));
	}

}
