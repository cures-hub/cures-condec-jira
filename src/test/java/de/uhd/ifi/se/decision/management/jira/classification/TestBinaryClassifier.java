package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestBinaryClassifier extends TestSetUp {

	private BinaryClassifier binaryClassifier;

	@Before
	public void setUp() {
		init();
		TextClassifier classifier = TextClassifier.getInstance("TEST");
		classifier.setGroundTruthFile(TestGroundTruthData.getTestGroundTruthDataFile());
		classifier.train();
		binaryClassifier = classifier.getBinaryClassifier();
	}

	@Test
	@NonTransactional
	public void testPredictSingleSentence() {
		assertFalse(binaryClassifier.predict("Maybe we can talk about that tomorrow."));
		assertTrue(binaryClassifier.predict("How can we implement?"));
	}

	@Test
	@NonTransactional
	public void testPredictSentences() {
		List<String> sentences = new ArrayList<>();
		sentences.add("Thanks for updating the unit tests!");
		sentences.add("The code will be less clear.");
		assertFalse(binaryClassifier.predict(sentences)[0]);
		assertTrue(binaryClassifier.predict(sentences)[1]);
	}

	@Test
	@NonTransactional
	public void testSaveToAndLoadFromFile() {
		File file = binaryClassifier.saveToFile();
		assertTrue(file.exists());
		assertNotNull(binaryClassifier.loadFromFile());
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

	@Test
	public void testModeEmptyArray() {
		assertEquals(0, AbstractClassifier.mode(new int[0]));
	}
}
