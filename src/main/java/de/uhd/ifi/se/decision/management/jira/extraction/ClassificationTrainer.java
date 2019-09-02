package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.ClassificationTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.Instances;

/**
 * Interface responsible to train the supervised text classifier. For this
 * purpose, the project admin needs to create and select a file.
 */
public interface ClassificationTrainer {

	public static final File DEFAULT_TRAINING_DATA = new File(DecisionKnowledgeClassifier.DEFAULT_DIR + "lucene.arff");

	static final Logger LOGGER = LoggerFactory.getLogger(ClassificationTrainer.class);
	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be uses to
	 * classify the comments and description of a Jira issue.
	 */
	boolean train();

	/**
	 * Creates a new file for the current
	 * project that can be used to train the classifier and saves it on the server
	 * in the JIRA home directory in the data/condec-plugin/project-key folder.
	 * @param useOnlyValidatedData
	 *            Boolean flag to indicated whether to use all or only user-validated data.
	 * @return ARFF file that was created and saved on the server or null if it
	 *         could not be saved.
	 */
	File saveTrainingFile(boolean useOnlyValidatedData);

	/**
	 * Reads training data from an file to
	 * train the classifier.
	 * 
	 * @param file
	 *            file to train the
	 *            classifier.
	 */
	void setTrainingFile(File file);

	/**
	 * Provides a list of decision knowledge element with a knowledge type and a
	 * summary to train the classifier with.
	 * 
	 * @param trainingElements
	 *            list of decision knowledge element with a knowledge type and a
	 *            summary.
	 */
	void setTrainingData(List<DecisionKnowledgeElement> trainingElements);

	/**
	 * Gets all files on the server.
	 * 
	 * @return all files on the server as a
	 *         list.
	 */
	List<File> getTrainingFiles();

	/**
	 * Gets the names of all files on the
	 * server.
	 * 
	 * @return names of all files on the
	 *         server as a list of strings.
	 */
	List<String> getTrainingFileNames();

	/**
	 * Gets the supervised binary and fine grained classifier to identify decision
	 * knowledge in natural language texts.
	 * 
	 * @see DecisionKnowledgeClassifier
	 * 
	 * @return instance of DecisionKnowledgeClassifier.
	 */
	DecisionKnowledgeClassifier getClassifier();

	/**
	 * Gets the weighted instances of the classifier.
	 * 
	 * @see Instances
	 * 
	 * @return weighted instances of the classifier.
	 */
	Instances getInstances();

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
	 * @param file
	 *            training data for the classifier.
	 * 
	 * @return true if training succeeded.
	 */
	public static boolean trainClassifier(File file) {
		if (!file.exists()) {
			LOGGER.error("Could not find default training data for supervised text classifier.");
			return false;
		}
		ClassificationTrainer classificationTrainer = new ClassificationTrainerImpl();
		classificationTrainer.setTrainingFile(file);
		return classificationTrainer.train();
	}

	/**
	 * Copies the default training file to the given file.
	 * 
	 * @param file
	 *            file to copy default training file to.
	 * 
	 * @return updated file with default training content.
	 */
	public static File copyDefaultTrainingDataToFile(File file) {
		if (file.exists()) {
			return file;
		}
		//TODO: lucene.arf ändern
		String pathToTrainingFile = ComponentGetter.getUrlOfClassifierFolder() + "lucene.arff";
		try {
			InputStream inputStream = new URL(pathToTrainingFile).openStream();
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);

			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(buffer);
			outputStream.close();
		} catch (IOException e) {
			LOGGER.error("Failed to copy default training data to file. Message: " + e.getMessage());
		}
		return file;
	}
}
