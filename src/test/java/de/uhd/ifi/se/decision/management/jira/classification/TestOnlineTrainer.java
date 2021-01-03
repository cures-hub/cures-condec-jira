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
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;


public class TestOnlineTrainer extends TestSetUp {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestOnlineTrainer.class);


	@Before
	public void setUp() {
		init();
	}

	public static KnowledgeElement createElement(KnowledgeType type, String summary) {
		KnowledgeElement element = new KnowledgeElement();
		element.setType(type);
		element.setSummary(summary);
		return element;
	}

	public static List<KnowledgeElement> getTrainingData() {
		List<KnowledgeElement> trainingElements = new ArrayList<KnowledgeElement>();
		trainingElements.add(createElement(KnowledgeType.ISSUE, "Issue"));
		trainingElements.add(createElement(KnowledgeType.DECISION, "Decision"));
		trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "Alternative"));
		trainingElements.add(createElement(KnowledgeType.PRO, "Pro"));
		trainingElements.add(createElement(KnowledgeType.CON, "Con"));
		trainingElements.add(createElement(KnowledgeType.OTHER, "Pizza"));
		trainingElements.add(createElement(KnowledgeType.ISSUE, "How to"));
		trainingElements.add(createElement(KnowledgeType.DECISION, "We decided"));
		trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "An option would be"));
		trainingElements.add(createElement(KnowledgeType.PRO, "+1"));
		trainingElements.add(createElement(KnowledgeType.CON, "-1"));
		trainingElements.add(createElement(KnowledgeType.OTHER, "Lunch"));
		trainingElements.add(createElement(KnowledgeType.ISSUE, "I don't know how we can"));
		trainingElements.add(createElement(KnowledgeType.DECISION, "We will do"));
		trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "A possible solution could be"));
		trainingElements.add(createElement(KnowledgeType.PRO, "Very good."));
		trainingElements.add(createElement(KnowledgeType.CON, "I don't agree"));
		trainingElements.add(createElement(KnowledgeType.OTHER, "Party tonight"));
		trainingElements.add(createElement(KnowledgeType.ISSUE, "The question is"));
		trainingElements.add(createElement(KnowledgeType.DECISION, "I implemented"));
		trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "We could have done"));
		trainingElements.add(createElement(KnowledgeType.PRO, "Great"));
		trainingElements.add(createElement(KnowledgeType.CON, "No"));
		trainingElements.add(createElement(KnowledgeType.OTHER, "Hello"));
		return trainingElements;
	}

	@Test
	@NonTransactional
	public void testClassificationTrainerSetTrainingData() {
		List<KnowledgeElement> trainingElements = getTrainingData();
		OnlineTrainer trainer = new OnlineFileTrainerImpl("TEST");
		trainer.setTrainingData(trainingElements);
		assertTrue(trainer.train());
	}

	@Test
	@NonTransactional
	public void testClassificationTrainerFromArffFile() {
		List<KnowledgeElement> trainingElements = getTrainingData();
		OnlineFileTrainerImpl trainer = new OnlineFileTrainerImpl("TEST", trainingElements);
		File file = trainer.saveTrainingFile(true);
		trainer.setTrainingFile(file);
		assertNotNull(trainer.getInstances());
		trainer = new OnlineFileTrainerImpl("TEST", file.getName());
		assertNotNull(trainer.getInstances());
		assertTrue(trainer.train());
		file.delete();
	}

	@Test
	@NonTransactional
	public void testSaveArffFile() {
		FileTrainer trainer = new OnlineFileTrainerImpl("TEST");
		File file = trainer.saveTrainingFile(false);
		assertTrue(file.exists());
		file.delete();
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
		FileTrainer trainer = new OnlineFileTrainerImpl();
		File luceneArffFile = getTrimmedDefaultArffFile();
		assertTrue(luceneArffFile.exists());
		trainer.setTrainingFile(luceneArffFile);
		//assertNotNull(trainer.getInstances());
		// assertTrue(trainer.train());
		//
		// DecisionKnowledgeClassifier classifier = trainer.getClassifier();
		// List<String> stringsToBeClassified = Arrays.asList("-1", "Issue", "Decision",
		// "Alternative", "Party tonight",
		// "+1", "Very good.");
		// // List<Boolean> expectedRelevance = Arrays.asList(true, true, false);
		// List<Boolean> predictedRelevance =
		// classifier.makeBinaryPredictions(stringsToBeClassified);
		// // assertEquals(expectedRelevance, predictedRelevance);
		// LOGGER.info((predictedRelevance);
		//
		// List<KnowledgeType> types =
		// classifier.makeFineGrainedPredictions(stringsToBeClassified);
		// LOGGER.info((types);
	}

	@Test
	@NonTransactional
	public void testTrainDefaultClassifier() {
		File trainingFile = getTrimmedDefaultArffFile();
		assertTrue(FileTrainer.trainClassifier(trainingFile));
	}

	private File getTrimmedDefaultArffFile() {
		File fullDefaultFile = new File("src/main/resources/classifier/defaultTrainingData.arff");
		File trimmedDefaultFile = new File("src/test/resources/classifier/defaultTrainingData.arff");

		int numberOfLines = 25;

		BufferedWriter writer = null;
		try {
			if (!trimmedDefaultFile.exists()) {
				trimmedDefaultFile.getParentFile().mkdirs();
				trimmedDefaultFile.createNewFile();
			} else {
				return trimmedDefaultFile;
			}
		} catch (IOException e) {
			LOGGER.error("Could not create new trimmed training file or directories for unit tests.");
			LOGGER.error(e.getMessage());
		}
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
			LOGGER.error("Could not add content to trimmed training file for unit tests.");
			LOGGER.error(e.getMessage());
		}
		return fullDefaultFile;

	}


	@Test
	@NonTransactional
	public void testGetArffFiles() {
		FileTrainer trainer = new OnlineFileTrainerImpl();
		assertEquals(ArrayList.class, trainer.getTrainingFileNames().getClass());
	}

}
