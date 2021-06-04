package de.uhd.ifi.se.decision.management.jira.config;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Contains the configuration details for the basic settings for the ConDec
 * plug-in for one Jira project (see {@link DecisionKnowledgeProject}).
 */
public class BasicConfiguration {

	private boolean isActivated;

	/**
	 * Constructs an object with default values.
	 */
	public BasicConfiguration() {

	}

	/**
	 * @return true if the ConDec plug-in is activated for the Jira project.
	 */
	public boolean isActivated() {
		return isActivated;
	}

	/**
	 * @param isActivated
	 *            true if the ConDec plug-in is activated for the Jira project.
	 */
	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}

}
