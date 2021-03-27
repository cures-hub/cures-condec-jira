package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;

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

	@Override
	public InputMethod<Object, ProjectSource> getInputMethod() {
		if (recommenderType.equals(RecommenderType.KEYWORD)) {
			inputMethod = new ProjectSourceInputString();
		} else {
			inputMethod = new ProjectSourceInputKnowledgeElement();
		}
		return inputMethod;
	}

}
