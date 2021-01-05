package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.ao.test.jdbc.NonTransactional;

public class TestFileTrainer {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestFileTrainer.class);

	public static final String TEST_TRAINING_FILE_PATH = "src/test/resources/classifier/defaultTrainingData.csv";

	@Test
	@Ignore
	@NonTransactional
	public void testCopyDefaultTrainingDataToFile() {
		assertTrue(FileManager.copyDefaultTrainingDataToFile().exists());
	}

	@Test
	public void testFileContentHash() {
		try {
			File file = new File(TEST_TRAINING_FILE_PATH);
			FileInputStream fileStream = new FileInputStream(file);
			String fileChecksum = FileManager.getMD5Checksum(fileStream);

			String testString = "teststring";
			InputStream testStream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
			String testChecksum = FileManager.getMD5Checksum(testStream);
			assertFalse(testChecksum.equals(fileChecksum));

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			fail(e.getMessage());
		}
	}

}
