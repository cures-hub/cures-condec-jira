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
import weka.core.Instances;

/**
 * Interface responsible to train the supervised text classifier. For this
 * purpose, the project admin needs to create and select an ARFF file.
 */
public interface ClassificationTrainer {

	public static final File DEFAULT_TRAINING_DATA = new File(DecisionKnowledgeClassifier.DEFAULT_DIR + "lucene.arff");

	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be uses to
	 * classify the comments and description of a Jira issue.
	 */
	boolean train();

	/**
	 * Creats a new Attribute-Relation File Format (ARFF) file for the current
	 * project that can be used to train the classifier and saves it on the server
	 * in the JIRA home directory in the data/condec-plugin/project-key folder.
	 * 
	 * @return ARFF file that was created and saved on the server or null if it
	 *         could not be saved.
	 */
	File saveArffFile();

	/**
	 * Reads training data from an Attribute-Relation File Format (ARFF) file to
	 * train the classifier.
	 * 
	 * @param arffFile
	 *            Attribute-Relation File Format (ARFF) file to train the
	 *            classifier.
	 */
	void setArffFile(File arffFile);

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
	 * Gets all Attribute-Relation File Format (ARFF) files on the server.
	 * 
	 * @return all Attribute-Relation File Format (ARFF) files on the server as a
	 *         list.
	 */
	List<File> getArffFiles();

	/**
	 * Gets the names of all Attribute-Relation File Format (ARFF) files on the
	 * server.
	 * 
	 * @return names of all Attribute-Relation File Format (ARFF) files on the
	 *         server as a list of strings.
	 */
	List<String> getArffFileNames();

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
	 * Trains the default classifier with the default ARFF file.
	 * 
	 * @return true if training succeeded.
	 */
	public static boolean trainDefaultClassifier() {
		return trainClassifier(DEFAULT_TRAINING_DATA);
	}

	/**
	 * Trains the classifier with the given ARFF file.
	 * 
	 * @param arffFile
	 *            training data for the classifier in the ARFF format.
	 * 
	 * @return true if training succeeded.
	 */
	public static boolean trainClassifier(File arffFile) {
		if (!arffFile.exists()) {
			System.err.println("Could not find default training data for supervised text classifier.");
			return false;
		}
		ClassificationTrainer classificationTrainer = new ClassificationTrainerImpl();
		classificationTrainer.setArffFile(arffFile);
		return classificationTrainer.train();
	}

	/**
	 * Copies the default ARFF file to the given file.
	 * 
	 * @param arffFile
	 *            file to copy default ARFF file to.
	 * 
	 * @return updated file with default ARFF content.
	 */
	public static File copyDefaultTrainingDataToFile(File arffFile) {
		if (arffFile.exists()) {
			return arffFile;
		}
		String pathToArffFile = ComponentGetter.getUrlOfClassifierFolder() + "lucene.arff";
		try {
			InputStream inputStream = new URL(pathToArffFile).openStream();
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);

			OutputStream outputStream = new FileOutputStream(arffFile);
			outputStream.write(buffer);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arffFile;
	}
}
