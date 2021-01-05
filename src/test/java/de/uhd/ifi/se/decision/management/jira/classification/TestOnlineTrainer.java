package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.ClassifierTrainer;
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
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		DataFrame dataFrame = trainer.getDataFrame();
		assertNotNull(dataFrame);
		assertEquals(0, dataFrame.columnIndex("isAlternative"));
	}

	@Test
	@NonTransactional
	public void testSaveTrainingFile() {
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		File file = trainer.saveTrainingFile(false);
		assertTrue(file.exists());
		file.delete();
	}

	@Test
	@NonTransactional
	public void testDefaultTrainingFile() {
		ClassifierTrainer trainer = new ClassifierTrainer();
		File trainingFile = TestClassifierTrainer.getTestTrainingDataFile();
		assertTrue(trainingFile.exists());
		trainer.setTrainingFile(trainingFile);
	}
}
