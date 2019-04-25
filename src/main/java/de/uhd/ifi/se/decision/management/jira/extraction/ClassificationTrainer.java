package de.uhd.ifi.se.decision.management.jira.extraction;

import java.io.File;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

/**
 * Interface to train the classifier with the ARFF file selected by the project admin.
 */
public interface ClassificationTrainer {

	String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory().getAbsolutePath()
			+ File.separator + "condec-plugin" + File.separator + "classifier" + File.separator;

	/**
	 * Trains the Classifier with the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be uses to
	 * classify the comments and description of a Jira issue.
	 */
	void train();

	/**
	 * Creats a new Attribute-Relation File Format (ARFF) file for the current project that can be used to train the
	 * classifier.
	 * 
	 * @return ARFF file that was created and saved on the server or null if it could not be saved.
	 */
	File saveArffFile();

	/**
	 * Gets the names of all Attribute-Relation File Format (ARFF) files on the server.
	 * 
	 * @return names of all Attribute-Relation File Format (ARFF) files on the server as a list of Strings.
	 */
	List<String> getArffFiles();

	/**
	 * Gets a specific Arff File String
	 * 
	 * @param fileName
	 * @return String of the Arff File
	 */
	String getArffFileString(String fileName);

}
