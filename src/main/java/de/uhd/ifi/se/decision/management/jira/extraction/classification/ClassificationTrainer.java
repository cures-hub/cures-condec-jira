package de.uhd.ifi.se.decision.management.jira.extraction.classification;


import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.util.JiraHome;

import java.io.File;

/**
 * Interface to train the classifier manually with the given data from the user.
 */
public interface ClassificationTrainer {

	String DEFAULT_DIR = ComponentAccessor.getComponentOfType(JiraHome.class).getDataDirectory().getAbsolutePath()
			                     + File.separator + "condec-plugin" + File.separator;

	/**
	 * Trains the Classifier withe the Data from the Database that was set and
	 * validated from the user.
	 */
	void train();

}
