package de.uhd.ifi.se.decision.management.jira.classification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;

/**
 * Saves and writes training data files and other files for the text classifier.
 */
public interface FileManager {

	Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

	/**
	 * @return all files on the server as a list.
	 */
	static List<File> getAllTrainingFiles() {
		List<File> trainingFilesOnServer = new ArrayList<File>();
		for (File file : new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY).listFiles()) {
			if (file.getName().toLowerCase(Locale.ENGLISH).contains(".csv")) {
				trainingFilesOnServer.add(file);
			}
		}
		return trainingFilesOnServer;
	}

	/**
	 * @return names of all files on the server as a list of strings.
	 */
	static List<String> getTrainingFileNames() {
		List<File> arffFilesOnServer = getAllTrainingFiles();
		List<String> arffFileNames = new ArrayList<String>();
		for (File file : arffFilesOnServer) {
			arffFileNames.add(file.getName());
		}
		return arffFileNames;
	}

	/**
	 * Copies the default training file to a folder where it is needed.
	 *
	 * @return updated file with default training content.
	 */
	static File copyDefaultTrainingDataToClassifierDirectory() {
		return copyDataToFile("defaultTrainingData.csv");
	}

	static File copyDataToFile(String filename) {
		File classifierDir = new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY);
		classifierDir.mkdirs();
		String downloadUrlOfFile = ComponentGetter.getUrlOfClassifierFolder() + filename;
		File file = new File(DecisionKnowledgeClassifier.CLASSIFIER_DIRECTORY + filename);
		try {
			InputStream inputStream = new URL(downloadUrlOfFile).openStream();
			if (!file.exists() || hasSameContent(inputStream, new FileInputStream(file))) {
				inputStream = new URL(downloadUrlOfFile).openStream();
				file.createNewFile();
				int read;
				byte[] bytes = new byte[1024];
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				while ((read = inputStream.read(bytes)) != -1) {
					fileOutputStream.write(bytes, 0, read);
				}
				fileOutputStream.flush();
				fileOutputStream.close();
				LOGGER.info("Copied default preprocessing data to file. Message: " + file.getName());
			}
		} catch (Exception e) {
			LOGGER.error("Failed to copy data to file. Message: " + e.getMessage());
		}
		return file;
	}

	static boolean hasSameContent(InputStream inputStream1, InputStream inputStream2) {
		return getMD5Checksum(inputStream1).equals(getMD5Checksum(inputStream2));
	}

	/**
	 * Creates a checksum for a given InputStream. See:
	 * https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	 *
	 * @param fileInputStream
	 * @return byte array of MD5 hashes
	 */
	static byte[] createChecksum(InputStream fileInputStream) {
		byte[] buffer = new byte[1024];
		try {
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fileInputStream.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fileInputStream.close();
			return complete.digest();
		} catch (NoSuchAlgorithmException | IOException e) {
			LOGGER.error(e.getMessage());
		}
		return buffer;
	}

	/**
	 * Calculated the hexadecimal value of the MD5 hash of the file content for a
	 * given InputStream. See:
	 * https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	 *
	 * @param fileInputStream
	 * @return MD5 checksum of an InputStream
	 */
	static String getMD5Checksum(InputStream fileInputStream) {
		byte[] b = createChecksum(fileInputStream);
		StringBuilder result = new StringBuilder();

		for (byte value : b) {
			result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}

}
