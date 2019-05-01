package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.ClassificationTrainer;
import de.uhd.ifi.se.decision.management.jira.extraction.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ClassificationTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTextSplitter.AoSentenceTestDatabaseUpdater.class)
public class TestClassificationTrainer extends TestSetUpWithIssues {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
	}

	private DecisionKnowledgeElement createElement(KnowledgeType type, String summary) {
		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setType(type);
		element.setSummary(summary);
		return element;
	}

	private List<DecisionKnowledgeElement> getTrainingData() {
		List<DecisionKnowledgeElement> trainingElements = new ArrayList<DecisionKnowledgeElement>();
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
	public void testClassificationTrainerSetTrainingData() {
		List<DecisionKnowledgeElement> trainingElements = getTrainingData();
		ClassificationTrainer trainer = new ClassificationTrainerImpl("TEST");
		trainer.setTrainingData(trainingElements);
		assertNotNull(trainer.getInstances());
		assertTrue(trainer.train());
		// DecisionKnowledgeClassifier classifier = trainer.getClassifier();
		// List<String> stringsToBeClassified = Arrays.asList("-1", "Issue", "Decision",
		// "Alternative", "Party tonight",
		// "+1", "Very good.");
		// List<Boolean> isRelevant =
		// classifier.makeBinaryPredictions(stringsToBeClassified);
		// System.out.println(isRelevant);
		//
		// List<KnowledgeType> types =
		// classifier.makeFineGrainedPredictions(stringsToBeClassified);
		// System.out.println(types);
		// // assertTrue(isRelevant.get(1));
	}

	@Test
	public void testClassificationTrainerFromArffFile() {
		List<DecisionKnowledgeElement> trainingElements = getTrainingData();
		ClassificationTrainer trainer = new ClassificationTrainerImpl("TEST", trainingElements);
		File file = trainer.saveArffFile();
		trainer.setArffFile(file);
		assertNotNull(trainer.getInstances());
		trainer = new ClassificationTrainerImpl("TEST", file.getName());
		assertNotNull(trainer.getInstances());
		// assertTrue(trainer.train());
		file.delete();
	}

	@Test
	public void testSaveArffFile() {
		ClassificationTrainer trainer = new ClassificationTrainerImpl("TEST");
		File file = trainer.saveArffFile();
		assertTrue(file.exists());
		file.delete();
	}

	@Test
	public void testMockingOfClassifierDirectoryWorks() {
		assertEquals(DecisionKnowledgeClassifier.DEFAULT_DIR, System.getProperty("user.home") + File.separator + "data"
				+ File.separator + "condec-plugin" + File.separator + "classifier" + File.separator);
	}

	@Test
	public void testDefaultArffFile() {
		ClassificationTrainer trainer = new ClassificationTrainerImpl();
		File luceneArffFile = getDefaultArffFile();
		assertTrue(luceneArffFile.exists());
		trainer.setArffFile(luceneArffFile);
		assertNotNull(trainer.getInstances());
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
		// System.out.println(predictedRelevance);
		//
		// List<KnowledgeType> types =
		// classifier.makeFineGrainedPredictions(stringsToBeClassified);
		// System.out.println(types);
	}

	@Test
	public void testCopyDefaultTrainingDataToFile() {
		assertFalse(ClassificationTrainer.copyDefaultTrainingDataToFile(new File("")).exists());
		File luceneArffFile = getDefaultArffFile();
		assertTrue(ClassificationTrainer.copyDefaultTrainingDataToFile(luceneArffFile).exists());
	}

	@Test
	public void testTrainDefaultClassifier() {
		// File luceneArffFile = getDefaultArffFile();
		// assertTrue(ClassificationTrainer.trainClassifier(luceneArffFile));
		assertFalse(ClassificationTrainer.trainDefaultClassifier());
	}

	private File getDefaultArffFile() {
		File luceneArffFile = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator
				+ "classifier" + File.separator + "lucene.arff");
		return luceneArffFile;
	}

	@Test
	public void testGetArffFiles() {
		ClassificationTrainer trainer = new ClassificationTrainerImpl();
		assertEquals(ArrayList.class, trainer.getArffFileNames().getClass());
	}

}