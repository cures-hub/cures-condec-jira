package de.uhd.ifi.se.decision.management.jira.decisionguidance.resultmethods;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public abstract class ProjectSourceInput<T> implements InputMethod<T> {

	String projectKey;
	String name;
	KnowledgePersistenceManager knowledgePersistenceManager;
	List<KnowledgeElement> knowledgeElements;


	public abstract List<Recommendation> getResults(T input);

	public void queryDatabase() {
		knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.name);
		if (this.knowledgePersistenceManager != null)
			knowledgeElements = this.knowledgePersistenceManager.getKnowledgeElements();
		else new ArrayList<>();
	}

	public void setData(String projectKey, String name, List<KnowledgeElement> knowledgeElements) {
		this.projectKey = projectKey;
		this.name = name;
		this.knowledgeElements = knowledgeElements;
	}

}
