package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

/**
 * Interface to train the classifier manually with the given data from the user.
 */
public interface ClassificationTrainer {

	String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory().getAbsolutePath()
			+ File.separator + "condec-plugin" + File.separator + "classifier";

	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be uses to
	 * classify the comments and description of a Jira issue.
	 */
	void train();

	/**
	 * Trains the Classifier with the Data from the Database and Creats a new File.
	 * 
	 * @return Boolean is True if the Arff File could be created and saved on the
	 *         Server
	 */
	boolean saveArffFileOnServer();

	/**
	 * Gets all saved Arff File names on the server.
	 * 
	 * @return List<String>
	 */
	List<String> getArffFileList();

	/**
	 * Gets a specific Arff File String
	 * 
	 * @param fileName
	 * @return String of the Arff File
	 */
	String getArffFileString(String fileName);

}
