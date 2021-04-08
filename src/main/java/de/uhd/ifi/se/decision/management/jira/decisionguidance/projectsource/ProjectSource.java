package de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Models another Jira project that is used as a knowledge source.
 */
public class ProjectSource extends KnowledgeSource {

	protected String projectKey;

	/**
	 * @param projectKey
	 *            key of the other Jira project.
	 */
	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.isActivated = false;
		this.icon = "aui-iconfont-jira";
	}

	/**
	 * @param projectKey
	 *            key of the other Jira project.
	 * @param projectSourceName
	 *            name of the other Jira project.
	 * @param isActivated
	 *            true if recommendations should be generated from this source.
	 */
	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this(projectKey);
		this.name = projectSourceName;
		this.isActivated = isActivated;
	}

	/**
	 * @param jiraProject
	 *            other Jira project as a {@link DecisionKnowledgeProject} object.
	 */
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
