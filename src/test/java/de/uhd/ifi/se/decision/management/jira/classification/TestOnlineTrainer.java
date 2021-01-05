package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import net.java.ao.test.jdbc.NonTransactional;
import smile.data.DataFrame;

public class TestOnlineTrainer extends TestSetUp {

	@Before
	public void setUp() {
		init();
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
		assertEquals(ArrayList.class, FileTrainer.getTrainingFileNames().getClass());
	}

}
