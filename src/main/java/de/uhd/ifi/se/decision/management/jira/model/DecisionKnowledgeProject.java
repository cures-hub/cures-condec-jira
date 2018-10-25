package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.IssueStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;

/**
 * Interface for a project and its configuration. The project is a JIRA project
 * that is extended with settings for this plug-in, for example, whether the
 * plug-in is activated for the project.
 */
public interface DecisionKnowledgeProject {

	/**
	 * Get the key of the project. The project is a JIRA project that is extended
	 * with settings for this plug-in, for example, whether the plug-in is activated
	 * for the project.
	 *
	 * @return key of the JIRA project.
	 */
	String getProjectKey();

	/**
	 * Set the key of the project. The project is a JIRA project that is extended
	 * with settings for this plug-in, for example, whether the plug-in is activated
	 * for the project.
	 *
	 * @param projectKey of the JIRA project.
	 */
	void setProjectKey(String projectKey);

	/**
	 * Get the name of the project. The project is a JIRA project that is extended
	 * with settings for this plug-in, for example, whether the plug-in is activated
	 * for the project.
	 *
	 * @return name of the JIRA project.
	 */
	String getProjectName();

	/**
	 * Set the name of the project. The project is a JIRA project that is extended
	 * with settings for this plug-in, for example, whether the plug-in is activated
	 * for the project.
	 *
	 * @param projectName of the JIRA project.
	 */
	void setProjectName(String projectName);

	/**
	 * Determine whether the plug-in is activated for this project.
	 *
	 * @return true if the plug-in is activated for this project.
	 */
	boolean isActivated();

	/**
	 * Set whether the plug-in is activated for this project.
	 *
	 * @param isActivated true if the plug-in should be activated for this project.
	 */
	void setActivated(boolean isActivated);

	/**
	 * Determine whether decision knowledge is stored in JIRA issues for this
	 * project. If you choose the issue strategy, you need to associate the project
	 * with the decision knowledge issue type scheme.
	 *
	 * @see AbstractPersistenceStrategy
	 * @see IssueStrategy
	 * @see ActiveObjectStrategy
	 * @return true if decision knowledge is stored in JIRA issues for this project
	 *         (issue strategy). Otherwise object relational mapping is used (active
	 *         object strategy).
	 */
	boolean isIssueStrategy();

	/**
	 * Set whether decision knowledge is stored in JIRA issues for this project. If
	 * you choose the issue strategy, you need to associate the project with the
	 * decision knowledge issue type scheme.
	 *
	 * @see AbstractPersistenceStrategy
	 * @see IssueStrategy
	 * @see ActiveObjectStrategy
	 * @param isIssueStrategy true if decision knowledge should be stored in JIRA
	 *                        issues for this project (issue strategy). Otherwise
	 *                        object relational mapping is used (active object
	 *                        strategy).
	 */
	void setIssueStrategy(boolean isIssueStrategy);

	/**
	 * Get the persistence strategy for decision knowledge used in this project.
	 *
	 * @see AbstractPersistenceStrategy
	 * @see IssueStrategy
	 * @see ActiveObjectStrategy
	 * @return persistence strategy for decision knowledge used in this project,
	 *         either issue strategy or active object strategy. The active object
	 *         strategy is the default strategy.
	 */
	AbstractPersistenceStrategy getPersistenceStrategy();

	/**
	 * Get the persistence strategy for decision knowledge used in this project.
	 *
	 * @see AbstractPersistenceStrategy
	 * @see IssueStrategy
	 * @see ActiveObjectStrategy
	 * @param projectKey of the JIRA project.
	 * @return persistence strategy for decision knowledge used in this project,
	 *         either issue strategy or active object strategy. The active object
	 *         strategy is the default strategy.
	 */
	static AbstractPersistenceStrategy getPersistenceStrategy(String projectKey) {
		return StrategyProvider.getPersistenceStrategy(projectKey);
	}

	/**
	 * Get the types of decision knowledge that is used in this project.
	 *
	 * @see KnowledgeType
	 * @return set of decision knowledge types used in this project.
	 */
	Set<KnowledgeType> getKnowledgeTypes();

	/**
	 * Determine whether decision knowledge is extracted from git commit messages.
	 *
	 * @return true if decision knowledge is extracted from git commit messages.
	 */
	boolean isKnowledgeExtractedFromGit();

	/**
	 * Set whether decision knowledge is extracted from git commit messages.
	 *
	 * @param isKnowledgeExtractedFromGit true if decision knowledge should be
	 *                                    extracted from git commit messages.
	 */
	void setKnowledgeExtractedFromGit(boolean isKnowledgeExtractedFromGit);

	/**
	 * Determine whether decision knowledge is extracted from issue comments.
	 *
	 * @return true if decision knowledge is extracted from issue comments.
	 */
	boolean isKnowledgeExtractedFromIssues();

	/**
	 * Set whether decision knowledge is extracted from issue comments.
	 *
	 * @param isKnowledgeExtractedFromIssues true if decision knowledge should be
	 *                                       extracted from issue comments.
	 */
	void setKnowledgeExtractedFromIssues(boolean isKnowledgeExtractedFromIssues);

	/**
	 * Set whether the webhook is enabled for this project.
	 *
	 * @param isWebhookEnabled true if the webhook is enabled for this project.
	 */
	void setWebhookEnabled(boolean isWebhookEnabled);

	/**
	 * Return whether the webhook is enabled for this project.
	 * 
	 * @return true if the webhook is enabled for this project.
	 */
	boolean isWebhookEnabled();

	/**
	 * Set the URL where the decision knowledge should be sent and the secret key
	 * for the submission.
	 *
	 * @param webhookUrl    URL of the webhook
	 * @param webhookSecret secret key
	 */
	void setWebhookData(String webhookUrl, String webhookSecret);

	/**
	 * Return the webhook URL where the decision knowledge is sent to if the webhook
	 * is enabled.
	 *
	 * @return webhook URL where the decision knowledge is sent to if the webhook is
	 *         enabled.
	 */
	String getWebhookUrl();

	/**
	 * Return the webhook secret key.
	 *
	 * @return secret key for the submission of the decision knowledge via webhook.
	 */
	String getWebhookSecret();

	/**
	 * Return the type of the root element of the sent decision knowledge tree via
	 * webhook.
	 *
	 * @return type of webhook root element.
	 */
	String getWebhookRootType();

	/**
	 * Checks if is icon parsing enabled.
	 *
	 * @return true, if is icon parsing enabled
	 */
	boolean isIconParsingEnabled();

	/**
	 * Checks if is classifier used for issue comments.
	 *
	 * @return true, if is classifier used for issue comments
	 */
	boolean isClassifierUsedForIssueComments();
}