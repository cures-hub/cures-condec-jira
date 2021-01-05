package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;
import smile.data.DataFrame;


public class TestOnlineTrainer extends TestSetUp {

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
	public void testClassificationTrainerFromCSVFile() {
		OnlineFileTrainerImpl trainer = new OnlineFileTrainerImpl("TEST");
		trainer.setTrainingFile(TestOnlineFileTrainerImpl.getTrimmedTrainingDataFile());
		DataFrame dataFrame = trainer.getDataFrame();
		assertNotNull(dataFrame);
		assertEquals(0, dataFrame.columnIndex("isAlternative"));
	}

	@Test
	@NonTransactional
	public void testSaveArffFile() {
		FileTrainer trainer = new OnlineFileTrainerImpl("TEST");
		trainer.setTrainingFile(TestOnlineFileTrainerImpl.getTrimmedTrainingDataFile());
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
		File luceneArffFile = TestOnlineFileTrainerImpl.getTrimmedTrainingDataFile();
		assertTrue(luceneArffFile.exists());
		trainer.setTrainingFile(luceneArffFile);
	}

	@Test
	@NonTransactional
	public void testTrainDefaultClassifier() {
		File trainingFile = TestOnlineFileTrainerImpl.getTrimmedTrainingDataFile();
		assertTrue(FileTrainer.trainClassifier(trainingFile));
	}

	@Test
	@NonTransactional
	public void testGetArffFiles() {
		FileTrainer trainer = new OnlineFileTrainerImpl();
		assertEquals(ArrayList.class, trainer.getTrainingFileNames().getClass());
	}

}
