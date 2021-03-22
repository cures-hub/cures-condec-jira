package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
		File trainingFile = getTestGroundTruthDataFile();
		groundTruthData = new GroundTruthData(trainingFile);
	}

	@Test
	@NonTransactional
	public void testGroundTruthDataNotNull() {
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
	public void testCreateGroundTruthDataFromKnowledgeElements() {
		DataFrame dataFrame = new GroundTruthData(KnowledgeElements.getTestKnowledgeElements()).getDataFrame();
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testCreateGroundTruthDataEntry() {
		Object[] rowValues = GroundTruthData.createTrainingRow(KnowledgeElements.getTestKnowledgeElement());
		assertEquals(0, rowValues[0]);
		assertEquals("WI: Implement feature", rowValues[5]);
	}

	@Test
	@NonTransactional
	public void testCreateGroundTruthDataFromFileName() {
		DataFrame dataFrame = new GroundTruthData("defaultTrainingData.csv").getDataFrame();
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testCreateGroundTruthDataFromDafaultFile() {
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

	@Test
	@NonTransactional
	public void testDefaultGroundTruthDataFile() {
		File file = TestGroundTruthData.getTestGroundTruthDataFile();
		assertTrue(file.exists());
	}

	/**
	 * @issue Which parts of text should we use to train and evaluate the classifier
	 *        during unit testing?
	 * @decision Use a subset of the default training data that comes with the
	 *           ConDec plugin!
	 * @pro To only use a subset increases the time efficiency of unit testing.
	 * @alternative We could use the entire default training data during unit
	 *              testing.
	 * @pro This would make the evaluation results more realistic than when only
	 *      using a few parts of text.
	 * 
	 * @return ground truth data set for unit tests that only covers a few parts of
	 *         text so that training and evaluation of the classifier in unit tests
	 *         is fast.
	 */
	public static File getTestGroundTruthDataFile() {
		File trimmedDefaultFile = new File(TestFileManager.TEST_TRAINING_FILE_PATH);
		if (trimmedDefaultFile.exists()) {
			return trimmedDefaultFile;
		}

		File fullDefaultFile = new File("src/main/resources/classifier/defaultTrainingData.csv");

		int numberOfLines = 52;

		BufferedWriter writer = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fullDefaultFile));
			writer = new BufferedWriter(new FileWriter(trimmedDefaultFile));

			String currentLine;
			int counter = 0;
			while ((currentLine = reader.readLine()) != null && counter < numberOfLines) {
				writer.write(currentLine + System.getProperty("line.separator"));
				counter++;
			}
			writer.close();
			reader.close();
			return trimmedDefaultFile;
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return fullDefaultFile;
	}

}
