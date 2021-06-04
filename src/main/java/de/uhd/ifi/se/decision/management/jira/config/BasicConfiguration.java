package de.uhd.ifi.se.decision.management.jira.config;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;

/**
 * Contains the configuration details for the basic settings for the ConDec
 * plug-in for one Jira project (see {@link DecisionKnowledgeProject}).
 */
public class BasicConfiguration {

	private boolean isActivated;
	private boolean isJiraIssueDocumentationLocationActivated;

	/**
	 * Constructs an object with default values.
	 * 
	 * @issue Should ConDec be enabled or disabled for a Jira project per default?
	 * @decision Enable ConDec for a Jira project per default!
	 * @pro Supports the rationale manager in setting up the rationale management
	 *      process, opt-out nudging.
	 */
	public BasicConfiguration() {
		setActivated(true);
		setJiraIssueDocumentationLocationActivated(false);
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

	/**
	 * @see JiraIssuePersistenceManager
	 * @see DocumentationLocation
	 * 
	 * @return true if decision knowledge can be stored in entire Jira issues in
	 *         this Jira project (not only in the description and comments of
	 *         existing Jira issues). If this is true, you need make sure that the
	 *         project is associated with the decision knowledge issue type scheme.
	 */
	public boolean isJiraIssueDocumentationLocationActivated() {
		return isJiraIssueDocumentationLocationActivated;
	}

	/**
	 * @see JiraIssuePersistenceManager
	 * @see DocumentationLocation
	 * 
	 * @param isJiraIssueDocumentationLocationActivated
	 *            true if decision knowledge can be stored in entire Jira issues in
	 *            this Jira project (not only in the description and comments of
	 *            existing Jira issues). If this is true, you need make sure that
	 *            the project is associated with the decision knowledge issue type
	 *            scheme.
	 */
	public void setJiraIssueDocumentationLocationActivated(boolean isJiraIssueDocumentationLocationActivated) {
		this.isJiraIssueDocumentationLocationActivated = isJiraIssueDocumentationLocationActivated;
	}

}
