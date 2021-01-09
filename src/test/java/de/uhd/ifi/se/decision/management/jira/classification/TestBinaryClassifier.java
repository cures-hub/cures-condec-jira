package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestBinaryClassifier extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testSaveToAndLoadFromFile() {
		ClassifierTrainer trainer = new ClassifierTrainer("TEST");
		trainer.setTrainingFile(TestClassifierTrainer.getTestTrainingDataFile());
		trainer.train();
		BinaryClassifier binaryClassifier = DecisionKnowledgeClassifier.getInstance().getBinaryClassifier();
		File file = binaryClassifier.saveToFile();
		assertTrue(file.exists());
		assertTrue(binaryClassifier.loadFromFile());
		assertTrue(binaryClassifier.isModelTrained());
		file.delete();
	}
}
