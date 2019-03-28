package de.uhd.ifi.se.decision.management.jira.extraction.classification;


import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

import java.io.File;

/**
 * Interface to train the classifier manually with the given data from the user.
 */
public interface ClassificationTrainer {

	String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory().getAbsolutePath()
			                     + File.separator + "condec-plugin" + File.separator+ "classifier";

	/**
	 * Trains the Classifier withe the Data from the Database that was set and
	 * validated from the user. Creates a new model Files that can be uses to
	 * classify the comments and description of a Jira issue.
	 */
	void train();

	/**
	 * Trains the Classifier with the Data from the Database and Creats a new File.
	 * @return String with the Data for the Arff File
	 */
	boolean saveArffFileOnServer();

}
