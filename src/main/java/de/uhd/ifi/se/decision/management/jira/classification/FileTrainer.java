package de.uhd.ifi.se.decision.management.jira.classification;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;

public interface FileTrainer {

	Logger LOGGER = LoggerFactory.getLogger(FileTrainer.class);

	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be used to
	 * classify the comments and description of a Jira issue and Git-commit
	 * messages.
	 */
	boolean train();

	/**
	 * Creates a new file for the current project that can be used to train the
	 * classifier and saves it on the server in the JIRA home directory in the
	 * data/condec-plugin/project-key folder.
	 *
	 * @param useOnlyValidatedData
	 *            Boolean flag to indicated whether to use all or only
	 *            user-validated data.
	 * @return ARFF file that was created and saved on the server or null if it
	 *         could not be saved.
	 */
	File saveTrainingFile(boolean useOnlyValidatedData);

	/**
	 * Reads training data from an file to train the classifier.
	 *
	 * @param file
	 *            file to train the classifier.
	 */
	void setTrainingFile(File file);

	/**
	 * @return all files on the server as a list.
	 */
	List<File> getAllTrainingFiles();

	/**
	 * @return names of all files on the server as a list of strings.
	 */
	List<String> getTrainingFileNames();

	/**
	 * @return instance of {@link DecisionKnowledgeClassifier}, i.e., supervised
	 *         binary and fine grained classifier to identify decision knowledge in
	 *         natural language texts.
	 */
	DecisionKnowledgeClassifier getClassifier();

	/**
	 * Trains the default classifier with the default training file.
	 *
	 * @return true if training succeeded.
	 */
	public static boolean trainDefaultClassifier() {
		return trainClassifier(new File(DecisionKnowledgeClassifier.DEFAULT_DIR + "defaultTrainingData.arff"));
	}

	/**
	 * Trains the classifier with the given training file.
	 *
	 * @param file
	 *            training data for the classifier.
	 * @return true if training succeeded.
	 */
	public static boolean trainClassifier(File file) {
		if (!file.exists()) {
			LOGGER.error("Could not find default training data for supervised text classifier.");
			return false;
		}
		FileTrainer fileTrainer = new OnlineFileTrainerImpl();
		fileTrainer.setTrainingFile(file);
		return fileTrainer.train();
	}

	/**
	 * Copies the default training file to a folder in the target.
	 *
	 * @return updated file with default training content.
	 */
	static File copyDefaultTrainingDataToFile() {

		return copyDataToFile(DecisionKnowledgeClassifier.DEFAULT_DIR, "defaultTrainingData.arff",
				ComponentGetter.getUrlOfClassifierFolder());
	}

	static File copyDataToFile(String path, String filename, String url) {
		File classifierDir = new File(path);
		if (!classifierDir.exists()) {
			// creates directory if it does not exist
			classifierDir.mkdirs();
		}
		String downloadUrlOfFile = url + filename;
		File file = new File(path + filename);

		try {
			InputStream inputStream = new URL(downloadUrlOfFile).openStream();

			if (!file.exists() || !getMD5Checksum(inputStream).equals(getMD5Checksum(new FileInputStream(file)))) {

				inputStream = new URL(downloadUrlOfFile).openStream();

				file.createNewFile();

				int read;
				byte[] bytes = new byte[1024];

				FileOutputStream fos = new FileOutputStream(file);

				while ((read = inputStream.read(bytes)) != -1) {
					fos.write(bytes, 0, read);
				}
				fos.flush();
				fos.close();

				// LOGGER.info(("Copied default preprocessing data to file. Message: " +
				// file.getName());
			}
		} catch (Exception e) {
			//LOGGER.info(("Path: " + path + ", filename: " + filename + ", url: " + url);
			//System.err.println("Failed to copy data to file. Message: " + e.getMessage());
		}
		return file;
	}

	/**
	 * Creates a checksum for a given InputStream. See:
	 * https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	 *
	 * @param fileInputStream
	 * @return byte array of MD5 hashes
	 * @throws Exception
	 */
	static byte[] createChecksum(InputStream fileInputStream) throws Exception {
		byte[] buffer = new byte[1024];
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
	}

	/**
	 * Calculated the hexadecimal value of the MD5 hash of the file content for a
	 * given InputStream. See:
	 * https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
	 *
	 * @param fileInputStream
	 * @return MD5 checksum of an InputStream
	 * @throws Exception
	 */
	static String getMD5Checksum(InputStream fileInputStream) throws Exception {
		byte[] b = createChecksum(fileInputStream);
		StringBuilder result = new StringBuilder();

		for (byte value : b) {
			result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
	}

}
