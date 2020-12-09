package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

import java.util.List;

public class ProjectSource extends KnowledgeSource {

	protected KnowledgePersistenceManager knowledgePersistenceManager;
	protected List<KnowledgeElement> knowledgeElements;

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.isActivated = false;
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this(projectKey);
		this.name = projectSourceName;
		this.isActivated = isActivated;
		this.icon = "aui-iconfont-jira";
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.name);
		} catch (IllegalArgumentException e) {

		}
	}


	@Override
	public InputMethod getInputMethod() {
		if (recommenderType.equals(RecommenderType.KEYWORD))
			this.inputMethod = new ProjectSourceInputString();
		else
			this.inputMethod = new ProjectSourceInputKnowledgeElement();

		return this.inputMethod;
	}

	public KnowledgePersistenceManager getKnowledgePersistenceManager() {
		return knowledgePersistenceManager;
	}

}
