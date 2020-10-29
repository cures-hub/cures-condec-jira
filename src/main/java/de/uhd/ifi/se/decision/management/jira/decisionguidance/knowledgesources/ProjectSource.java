package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods.ProjectSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods.ProjectSourceInput;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods.ProjectSourceInputString;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import mst.In;

import java.util.List;

public class ProjectSource extends KnowledgeSource {

	protected KnowledgePersistenceManager knowledgePersistenceManager;
	List<KnowledgeElement> knowledgeElements;

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.knowledgeSourceType = KnowledgeSourceType.PROJECT;
		this.isActivated = false;
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this(projectKey);
		this.name = projectSourceName;
		this.isActivated = isActivated;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.name);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setData() {
		if (this.inputMethod != null)
			((ProjectSourceInput) this.inputMethod).setData(this.projectKey, this.name, this.knowledgeElements);
	}

	@Override
	public InputMethod getInputMethod() {
		if (ConfigPersistenceManager.getRecommendationInput(projectKey).equals(RecommenderType.KEYWORD))
			this.inputMethod = new ProjectSourceInputString();
		else
			this.inputMethod = new ProjectSourceInputKnowledgeElement();

		return this.inputMethod;
	}

	public KnowledgePersistenceManager getKnowledgePersistenceManager() {
		return knowledgePersistenceManager;
	}
	
}
