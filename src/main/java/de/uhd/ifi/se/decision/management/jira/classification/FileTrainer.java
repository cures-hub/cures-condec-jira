package de.uhd.ifi.se.decision.management.jira.classification;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineFileTrainerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public interface FileTrainer {

	Logger LOGGER = LoggerFactory.getLogger(FileTrainer.class);

	File DEFAULT_TRAINING_DATA = new File(DecisionKnowledgeClassifier.DEFAULT_DIR + "defaultTrainingData.arff");

	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be used to
	 * classify the comments  and description of a Jira issue and Git-commit
	 * messages.
	 */
	boolean train();

	/**
	 * Creates a new file for the current
	 * project that can be used to train the classifier and saves it on the server
	 * in the JIRA home directory in the data/condec-plugin/project-key folder.
	 *
	 * @param useOnlyValidatedData Boolean flag to indicated whether to use all or only user-validated data.
	 * @return ARFF file that was created and saved on the server or null if it
	 * could not be saved.
	 */
	File saveTrainingFile(boolean useOnlyValidatedData);

	/**
	 * Reads training data from an file to
	 * train the classifier.
	 *
	 * @param file file to train the
	 *             classifier.
	 */
	void setTrainingFile(File file);

	/**
	 * Gets all files on the server.
	 *
	 * @return all files on the server as a
	 * list.
	 */
	List<File> getTrainingFiles();

	/**
	 * Gets the names of all files on the
	 * server.
	 *
	 * @return names of all files on the
	 * server as a list of strings.
	 */
	List<String> getTrainingFileNames();

	/**
	 * Gets the supervised binary and fine grained classifier to identify decision
	 * knowledge in natural language texts.
	 *
	 * @return instance of DecisionKnowledgeClassifier.
	 * @see DecisionKnowledgeClassifier
	 */
	DecisionKnowledgeClassifier getClassifier();


	/**
	 * Trains the default classifier with the default training file.
	 *
	 * @return true if training succeeded.
	 */
	public static boolean trainDefaultClassifier() {
		return trainClassifier(DEFAULT_TRAINING_DATA);
	}

	/**
	 * Trains the classifier with the given training file.
	 *
	 * @param file training data for the classifier.
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
	public static File copyDefaultTrainingDataToFile() {
		File file = FileTrainer.DEFAULT_TRAINING_DATA;
		File classfierDir = new File(DecisionKnowledgeClassifier.DEFAULT_DIR);
		if (!classfierDir.exists()) {
			//creates directory if it does not exist
			classfierDir.mkdirs();
		}

		if (file.exists()) {
			return file;
		}

		String pathToTrainingFile = ComponentGetter.getUrlOfClassifierFolder() + "defaultTrainingData.arff";
		try {

			file.createNewFile();
			InputStream inputStream = new URL(pathToTrainingFile).openStream();
			FileOutputStream outputStream = new FileOutputStream(file);

			int read;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to copy default training data to file. Message: " + e.getMessage());
		}
		return file;
	}

}
