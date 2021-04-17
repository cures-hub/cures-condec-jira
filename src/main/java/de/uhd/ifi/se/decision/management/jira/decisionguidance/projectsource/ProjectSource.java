package de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.project.Project;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;

/**
 * Models another Jira project that is used as a knowledge source.
 */
public class ProjectSource extends KnowledgeSource {

	protected Project jiraProject;

	/**
	 * @param projectKey
	 *            key of the other Jira project.
	 */
	public ProjectSource(String projectKey) {
		this.jiraProject = new DecisionKnowledgeProject(projectKey).getJiraProject();
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
	 * @param projectKey
	 *            key of the other Jira project.
	 * @param isActivated
	 *            true if recommendations should be generated from this source.
	 */
	public ProjectSource(String projectKey, boolean isActivated) {
		this(projectKey);
		this.isActivated = isActivated;
	}

	/**
	 * @param jiraProject
	 *            other Jira project.
	 */
	public ProjectSource(Project jiraProject) {
		this.jiraProject = jiraProject;
	}

	/**
	 * @return Jira project key of the Jira project that is used as the knowledge
	 *         source.
	 */
	public String getProjectKey() {
		return jiraProject != null ? jiraProject.getKey() : "";
	}

	/**
	 * @return Jira project that is used as the knowledge source.
	 */
	public Project getJiraProject() {
		return jiraProject;
	}

	/**
	 * @param projectKey
	 *            of the Jira project that is used as the knowledge source.
	 */
	public void setProjectKey(String projectKey) {
		this.jiraProject = new DecisionKnowledgeProject(projectKey).getJiraProject();
	}

	/**
	 * @return name of the knowledge source, e.g. the name of a Jira project or a
	 *         topic in DBPedia such as "Frameworks".
	 */
	@XmlElement
	public String getName() {
		return jiraProject.getName();
	}
}
