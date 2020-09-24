package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithm;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithmType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.ProjectSourceSubstringAlgorithm;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public class ProjectSource extends KnowledgeSource {

	private String projectSourceName;
	KnowledgePersistenceManager knowledgePersistenceManager;

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.knowledgeSourceAlgorithmType = KnowledgeSourceAlgorithmType.SUBSTRING;
		this.knowledgeSourceType = KnowledgeSourceType.PROJECT;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(this.projectKey);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		this.isActivated = false;
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this.projectKey = projectKey;
		this.projectSourceName = projectSourceName;
		this.isActivated = isActivated;
		this.knowledgeSourceAlgorithmType = KnowledgeSourceAlgorithmType.SUBSTRING;
		this.knowledgeSourceType = KnowledgeSourceType.PROJECT;
		try {
			this.knowledgePersistenceManager = KnowledgePersistenceManager.getOrCreate(projectSourceName);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return this.projectSourceName;
	}

	@Override
	public void setName(String name) {
		this.projectSourceName = name;
	}

	@Override
	public boolean isActivated() {
		return this.isActivated;
	}

	@Override
	public void setActivated(boolean activated) {
	}

	@Override
	public List<Recommendation> getResults(String inputs) {
		KnowledgeSourceAlgorithm knowledgeSourceAlgorithm;
		switch (this.knowledgeSourceAlgorithmType) {
			case SUBSTRING:
				knowledgeSourceAlgorithm = new ProjectSourceSubstringAlgorithm(projectKey, projectSourceName, inputs);
				return knowledgeSourceAlgorithm.getResults();
			default:
				knowledgeSourceAlgorithm = new ProjectSourceSubstringAlgorithm(projectKey, projectSourceName, inputs);
				return knowledgeSourceAlgorithm.getResults();
		}
	}

}
