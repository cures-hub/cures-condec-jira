package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;

/**
 * Interface responsible to train the supervised text classifier. For this
 * purpose, the project admin needs to create and select an ARFF file.
 */
public interface ClassificationTrainer {

	/**
	 * @issue What is the best place to store the supervised text classifier related
	 *        data?
	 * @decision Clone git repo to JIRAHome/data/condec-plugin/classifier!
	 */
	String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory().getAbsolutePath()
			+ File.separator + "condec-plugin" + File.separator + "classifier" + File.separator;

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
	 * Gets a specific Arff File String
	 * 
	 * @param fileName
	 * @return String of the Arff File
	 */
	String getArffFileString(String fileName);
}
