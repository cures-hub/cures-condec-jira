package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms;

import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

public abstract class ProjectKnowledgeSourceAlgorithm implements KnowledgeSourceAlgorithm {


	protected String projectKey;
	protected String projectSourceName;
	protected KnowledgePersistenceManager knowledgePersistenceManager;

	public void setData(String projectKey, String knowledgeSourceName) {
		this.projectKey = projectKey;
		this.projectSourceName = knowledgeSourceName;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

}
