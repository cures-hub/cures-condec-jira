package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import net.java.ao.test.jdbc.NonTransactional;

public class TestFileManager extends TestSetUp {

	public static final String TEST_TRAINING_FILE_PATH = "src/test/resources/classifier/defaultTrainingData.csv";

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testGetTrainingFileNames() {
		assertFalse(FileManager.getGroundTruthFileNames().contains("glove.6b.50d.csv"));
		assertTrue(FileManager.getGroundTruthFileNames().contains("defaultTrainingData.csv"));
	}

	@Test
	@NonTransactional
	public void testCopyDefaultTrainingDataToFile() {
		File defaultTrainingDataFile = FileManager.copyDefaultTrainingDataToClassifierDirectory();
		assertEquals("defaultTrainingData.csv", defaultTrainingDataFile.getName());
	}

	@Test
	public void testHasSameContent() {
		try {
			File file = new File(TEST_TRAINING_FILE_PATH);
			FileInputStream fileStream = new FileInputStream(file);
			String testString = "teststring";
			InputStream testStream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
			assertFalse(FileManager.hasSameContent(fileStream, testStream));
			testStream.close();
			fileStream.close();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@Ignore
	@NonTransactional
	public void testMockingOfClassifierDirectoryWorks() {
		assertEquals(TextClassifier.CLASSIFIER_DIRECTORY, System.getProperty("user.home") + File.separator + "data"
				+ File.separator + "condec-plugin" + File.separator + "classifier" + File.separator);
	}

}
