package de.uhd.ifi.se.decision.management.jira.classification;

import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.*;

public class TestFileTrainer {

	public static final String TEST_ARFF_FILE_PATH = "src/test/resources/classifier/defaultTrainingData.arff";

	@Test
	@NonTransactional
	public void testCopyDefaultTrainingDataToFile() {
		assertTrue(FileTrainer.copyDefaultTrainingDataToFile().exists());
	}

	@Test
	public void testFileContentHash(){
	try{
		File file = new File(TEST_ARFF_FILE_PATH);
		FileInputStream fileStream = new FileInputStream(file);
		String fileChecksum = FileTrainer.getMD5Checksum(fileStream);

		String testString = "teststring";
		InputStream testStream = new ByteArrayInputStream(testString.getBytes(StandardCharsets.UTF_8));
		String testChecksum = FileTrainer.getMD5Checksum(testStream);
		//System.out.println("HASHMD5 : " + fileChecksum);
		assertFalse(testChecksum.equals(fileChecksum));

	}catch (Exception e){
		e.printStackTrace();
		fail();
	}
	}


}
