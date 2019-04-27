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

	DecisionKnowledgeClassifier getClassifier();

	Instances getInstances();

	public static boolean trainDefaultClassifier() {
		File targetFile = null;
		String pathToDefaultArffFile = ComponentGetter.getUrlOfClassifierFolder() + "lucene.arff";
		try {
			InputStream inputStream = new URL(pathToDefaultArffFile).openStream();
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);

			targetFile = new File(DecisionKnowledgeClassifier.DEFAULT_DIR + "lucene.arff");
			OutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (targetFile != null && !targetFile.exists()) {
			System.err.println("Could not find default training data for supervised text classifier.");
			return false;
		}
		ClassificationTrainer classificationTrainer = new ClassificationTrainerImpl();
		classificationTrainer.setArffFile(targetFile);
		return classificationTrainer.train();
	}
}
