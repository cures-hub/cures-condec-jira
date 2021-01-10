package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
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
		BinaryClassifier binaryClassifier = TextClassifier.getInstance().getBinaryClassifier();
		File file = binaryClassifier.saveToFile();
		assertTrue(file.exists());
		assertTrue(binaryClassifier.loadFromFile());
		assertTrue(binaryClassifier.isTrained());
		file.delete();
	}

	@Test
	public void testModeBinaryClassification() {
		int[] binaryClassificationResult = new int[4];
		binaryClassificationResult[0] = 1;
		assertEquals(0, AbstractClassifier.mode(binaryClassificationResult));
		binaryClassificationResult[1] = 1;
		assertEquals(0, AbstractClassifier.mode(binaryClassificationResult));
		binaryClassificationResult[2] = 1;
		assertEquals(1, AbstractClassifier.mode(binaryClassificationResult));
	}
}
