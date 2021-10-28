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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;

/**
 * Saves and writes {@link GroundTruthData} files and other files for the text
 * classifier.
 */
public interface FileManager {

	Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

	/**
	 * @return all ground truth files on the server as a list.
	 */
	static List<File> getAllGroundTruthFiles() {
		return getFilesMatchingRegex("(?!glove)(.)*.csv");
	}

	/**
	 * @return names of all ground truth files on the server as a list of strings.
	 */
	static List<String> getGroundTruthFileNames() {
		List<File> arffFilesOnServer = getAllGroundTruthFiles();
		List<String> arffFileNames = new ArrayList<String>();
		for (File file : arffFilesOnServer) {
			arffFileNames.add(file.getName());
		}
		return arffFileNames;
	}

	/**
	 * @return names of all trained classifiers on the server as a list of strings.
	 */
	static Set<String> getTrainedClassifierNames() {
		List<File> trainedClassifierFilesOnServer = getAllTrainedClassifiers();
		Set<String> fileNames = new HashSet<String>();
		for (File file : trainedClassifierFilesOnServer) {
			String[] split = file.getName().split("(-fineGrained)|(-binary)");
			if (split.length > 0) {
				fileNames.add(split[0]);
			}
		}
		return fileNames;
	}

	/**
	 * @return all files of trained classifiers on the server as a list.
	 */
	static List<File> getAllTrainedClassifiers() {
		return getFilesMatchingRegex("(.)*.model");
	}

	private static List<File> getFilesMatchingRegex(String regex) {
		List<File> filesMatchingRegex = new ArrayList<File>();
		for (File file : new File(TextClassifier.CLASSIFIER_DIRECTORY).listFiles()) {
			if (file.getName().toLowerCase(Locale.ENGLISH).matches(regex)) {
				filesMatchingRegex.add(file);
			}
		}
		return filesMatchingRegex;
	}

	/**
	 * Copies the default training file to a folder where it is needed.
	 *
	 * @return updated file with default training content.
	 */
	static File copyDefaultTrainingDataToClassifierDirectory() {
		copyDataToFile("CONDEC-NLP4RE2021.csv");
		return copyDataToFile("defaultTrainingData.csv");
	}

	static File copyDataToFile(String filename) {
		File classifierDir = new File(TextClassifier.CLASSIFIER_DIRECTORY);
		classifierDir.mkdirs();
		String downloadUrlOfFile = ComponentGetter.getUrlOfClassifierFolder() + filename;
		File file = new File(TextClassifier.CLASSIFIER_DIRECTORY + filename);
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
			LOGGER.debug("Failed to copy data to file. Message: " + e.getMessage());
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
