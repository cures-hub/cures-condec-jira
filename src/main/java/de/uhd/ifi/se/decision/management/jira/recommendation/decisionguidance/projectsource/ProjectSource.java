package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;

/**
 * Models another Jira project that is used as a knowledge source.
 */
public class ProjectSource extends KnowledgeSource {

	/**
	 * Jira project key of the {@link DecisionKnowledgeProject} that is used as the knowledge source.
	 */
	protected final String projectKey;

	/**
	 * @param jiraProject
	 *            other Jira project.
	 */
	public ProjectSource(Project jiraProject) {
			super(jiraProject == null ? "" : jiraProject.getName(), true);
			if (jiraProject != null) {
				this.projectKey = jiraProject.getKey();
			} else {
				this.projectKey = "";
			}
	}

	/**
	 * @param projectKey
	 *            key of the other Jira project.
	 */
	public ProjectSource(String projectKey) {
		this(new DecisionKnowledgeProject(projectKey).getJiraProject());
	}

	/**
	 * @param projectKey
	 *            key of the other Jira project.
	 * @param isActivated
	 *            true if recommendations should be generated from this source.
	 */
	public ProjectSource(String projectKey, boolean isActivated) {
		this(projectKey);
		this.activated = isActivated;
	}

	/**
	 * @return {@link ProjectSource#projectKey}
	 */
	public String getProjectKey() {
		return projectKey == null ? "" : projectKey;
	}

	/**
	 * @return Jira project that is used as the knowledge source.
	 */
	public Project getJiraProject() {
		return new DecisionKnowledgeProject(projectKey).getJiraProject();
	}

	/**
	 * @return name of the knowledge source, i.e. the name of a Jira project.
	 */
	@XmlElement
	public String getName() {
		return getJiraProject().getName();
	}

	@Override
	public String getIcon() {
		return "aui-iconfont-jira";
	}
}
