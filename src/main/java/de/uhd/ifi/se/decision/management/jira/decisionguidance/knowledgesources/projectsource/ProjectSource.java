package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;

/**
 * Models another Jira project as a knowledge source.
 */
public class ProjectSource extends KnowledgeSource {

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.isActivated = false;
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this(projectKey);
		this.name = projectSourceName;
		this.isActivated = isActivated;
		this.icon = "aui-iconfont-jira";
	}
}
