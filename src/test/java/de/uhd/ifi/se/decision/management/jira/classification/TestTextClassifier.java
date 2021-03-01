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
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import net.java.ao.test.jdbc.NonTransactional;

public class TestTextClassifier extends TestSetUp {
	private TextClassifier classifier;
	private static final List<String> TEST_SENTENCES = Arrays.asList("Pizza is preferred", "I have an issue");

	@Before
	public void setUp() {
		init();
		classifier = TextClassifier.getInstance("TEST");
		classifier.setTrainingFile(getTestTrainingDataFile());
	}

	@Test
	@NonTransactional
	public void testOnlineClassificationTrainerSetTrainingData() {
		classifier.setTrainingFile(TestTextClassifier.getTestTrainingDataFile());
		assertTrue(classifier.train());
	}

	@Test
	@NonTransactional
	@Ignore
	public void testOnlineClassificationTrainerFromElementsInKnowledgeGraph() {
		File file = classifier.saveTrainingFile();
		assertTrue(file.exists());
		classifier.setTrainingFile(file);
		assertNotNull(classifier.getTrainingData());
		classifier = new TextClassifier("TEST", file.getName());
		assertTrue(classifier.train());
		file.delete();
	}

	@Test
	@NonTransactional
	public void testDefaultArffFile() {
		File file = getTestTrainingDataFile();
		assertTrue(file.exists());
		classifier.setTrainingFile(file);
	}

	@Test
	@NonTransactional
	public void testEvaluateClassifier() {
		classifier.train();
		boolean executionSuccessful = true;
		try {
			classifier.evaluateClassifier(3);
		} catch (Exception e) {
			executionSuccessful = false;
		}
		assertTrue(executionSuccessful);
	}

	// TODO: evaluate without existing dec. know.

	@Test
	@NonTransactional
	public void testUpdate() {
		classifier.train();

		PartOfJiraIssueText sentence = new PartOfJiraIssueText();
		sentence.setDescription("In my opinion the query would be better!");
		sentence.setRelevant(true);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		sentence.setValidated(true);

		TextClassificationConfiguration config = ConfigPersistenceManager.getTextClassificationConfiguration("TEST");
		config.setOnlineLearningActivated(true);
		ConfigPersistenceManager.saveTextClassificationConfiguration("TEST", config);
		assertTrue(TextClassifier.getInstance("TEST").update(sentence));
	}

	// TODO: tests with unvalidated data element

	@Test
	@NonTransactional
	public void testGetInstances() {
		assertNotNull(this.classifier.getTrainingData());
	}

	@Test
	@NonTransactional
	public void testMakeBinaryPredicition() {
		classifier.train();
		assertEquals(2, TextClassifier.getInstance("TEST").getBinaryClassifier().predict(TEST_SENTENCES).length);
	}

	@Test
	@NonTransactional
	public void testMakeFineGrainedPredicition() {
		assertEquals(2, TextClassifier.getInstance("TEST").getFineGrainedClassifier().predict(TEST_SENTENCES).size());
	}

	public static File getTestTrainingDataFile() {
		File trimmedDefaultFile = new File(TestFileManager.TEST_TRAINING_FILE_PATH);
		if (trimmedDefaultFile.exists()) {
			return trimmedDefaultFile;
		}

		File fullDefaultFile = new File("src/main/resources/classifier/defaultTrainingData.csv");

		int numberOfLines = 42;

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

	@Test
	@NonTransactional
	public void testDefaultTrainingFile() {
		File trainingFile = TestTextClassifier.getTestTrainingDataFile();
		assertTrue(trainingFile.exists());
		classifier.setTrainingFile(trainingFile);
	}

}
