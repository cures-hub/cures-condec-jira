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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;
import smile.data.DataFrame;

public class TestOnlineFileTrainerImpl extends TestSetUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestOnlineFileTrainerImpl.class);

	private OnlineFileTrainerImpl trainer;
	private static final List<String> TEST_SENTENCES = Arrays.asList("Pizza is preferred", "I have an issue");

	@Before
	public void setUp() {
		init();
		trainer = new OnlineFileTrainerImpl("TEST");
		trainer.setTrainingFile(getTrimmedTrainingDataFile());
	}

	@Test
	@NonTransactional
	public void testOnlineClassificationTrainerSetTrainingData() {
		trainer.setTrainingFile(TestOnlineFileTrainerImpl.getTrimmedTrainingDataFile());
		assertTrue(trainer.train());
	}

	@Test
	@NonTransactional
	public void testOnlineClassificationTrainerFromArffFile() {
		File file = trainer.saveTrainingFile(true);
		trainer.setTrainingFile(file);
		assertNotNull(trainer.getDataFrame());
		trainer = new OnlineFileTrainerImpl("TEST", file.getName());
		// assertNotNull(trainer.getInstances());
		assertTrue(trainer.train());
		file.delete();
	}

	@Test
	@NonTransactional
	public void testSaveArffFile() {
		File file = trainer.saveTrainingFile(false);
		assertTrue(file.exists());
		// file.delete();
	}

	@Test
	@NonTransactional
	public void testMockingOfClassifierDirectoryWorks() {
		assertEquals(DecisionKnowledgeClassifier.DEFAULT_DIR, System.getProperty("user.home") + File.separator + "data"
				+ File.separator + "condec-plugin" + File.separator + "classifier" + File.separator);
	}

	@Test
	@NonTransactional
	public void testDefaultArffFile() {
		File file = getTrimmedTrainingDataFile();
		assertTrue(file.exists());
		trainer.setTrainingFile(file);
	}

	@Test
	@NonTransactional
	public void testGetArffFiles() {
		assertEquals(ArrayList.class, FileTrainer.getTrainingFileNames().getClass());
	}

	@Test
	@NonTransactional
	public void testEvaluateClassifier() {
		trainer.train();
		boolean executionSuccessful = true;
		try {
			trainer.evaluateClassifier();
		} catch (Exception e) {
			executionSuccessful = false;
		}
		assertTrue(executionSuccessful);
	}

	// TODO: evaluate without existing dec. know.

	@Test
	@NonTransactional
	public void testUpdate() {
		trainer.train();

		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setDescription("In my opinion the query would be better!");
		sentence.setRelevant(true);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		sentence.setValidated(true);

		assertTrue(trainer.update(sentence));
	}

	// TODO: tests with unvalidated data element

	@Test
	@NonTransactional
	public void testGetClassifier() {
		assertNotNull(this.trainer.getClassifier());
	}

	@Test
	@NonTransactional
	public void testGetInstances() {
		assertNotNull(this.trainer.getDataFrame());
	}

	@Test
	@NonTransactional
	public void testMakeBinaryPredicition() {
		trainer.train();
		assertEquals(2, trainer.getClassifier().makeBinaryPredictions(TEST_SENTENCES).length);
	}

	@Test
	@NonTransactional
	public void testMakeFineGrainedPredicition() {
		assertEquals(2, this.trainer.getClassifier().makeFineGrainedPredictions(TEST_SENTENCES).size());
	}

	@Test
	@NonTransactional
	public void testBuildDataFrame() {
		DataFrame dataFrame = trainer.buildDataFrame(KnowledgeElements.getTestKnowledgeElements());
		assertEquals(5, dataFrame.columnIndex("sentence"));
		assertTrue(dataFrame.size() > 1);
	}

	@Test
	@NonTransactional
	public void testCreateTrainingRow() {
		Object[] rowValues = trainer.createTrainingRow(KnowledgeElements.getTestKnowledgeElement());
		assertEquals(0, rowValues[0]);
		assertEquals("WI: Implement feature", rowValues[5]);
	}

	public static File getTrimmedTrainingDataFile() {
		File trimmedDefaultFile = new File(TestFileTrainer.TEST_TRAINING_FILE_PATH);
		if (trimmedDefaultFile.exists()) {
			return trimmedDefaultFile;
		}

		File fullDefaultFile = new File("src/main/resources/classifier/defaultTrainingData.csv");

		int numberOfLines = 41;

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
			LOGGER.error(e.getMessage());
		}
		return fullDefaultFile;
	}

}
