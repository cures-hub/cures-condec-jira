package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;

/**
 * Interface for a project and its configuration
 */
public interface DecisionKnowledgeProject {
	String getProjectKey();

	void setProjectKey(String projectKey);

	String getProjectName();

	void setProjectName(String projectName);

	boolean isActivated();

	void setActivated(boolean isActivated);

	boolean isIssueStrategy();

	void setIssueStrategy(boolean isIssueStrategy);

	boolean isUsingDecXplore();

	Set<KnowledgeType> getKnowledgeTypes();

	void setKnowledgeTypes(Set<KnowledgeType> knowledgeTypes);

	AbstractPersistenceStrategy getPersistenceStrategy();
}