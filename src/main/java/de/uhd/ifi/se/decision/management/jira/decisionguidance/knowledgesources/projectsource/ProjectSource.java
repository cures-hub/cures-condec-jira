package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Models another Jira project as a knowledge source.
 */
public class ProjectSource extends KnowledgeSource {

	protected String projectKey;

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.isActivated = false;
		this.icon = "aui-iconfont-jira";
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this(projectKey);
		this.name = projectSourceName;
		this.isActivated = isActivated;
	}

	public ProjectSource(DecisionKnowledgeProject jiraProject) {
		this(jiraProject.getProjectKey(), jiraProject.getProjectName(), false);
	}

	/**
	 * @return Jira project key of the Jira project that is used as the knowledge
	 *         source.
	 */
	public String getProjectKey() {
		return projectKey;
	}

	/**
	 * @param projectKey
	 *            of the Jira project that is used as the knowledge source.
	 */
	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}
}
