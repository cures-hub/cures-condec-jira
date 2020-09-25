package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithmType;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public abstract class KnowledgeSource {

	protected KnowledgeSourceAlgorithmType knowledgeSourceAlgorithmType;

	protected String projectKey;
	protected boolean isActivated;
	protected String name;

	public abstract List<Recommendation> getResults(String inputs);


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActivated() {
		return isActivated;
	}

	public void setActivated(boolean activated) {
		isActivated = activated;
	}

	public KnowledgeSourceAlgorithmType getKnowledgeSourceAlgorithmType() {
		return knowledgeSourceAlgorithmType;
	}

	public void setKnowledgeSourceAlgorithmType(KnowledgeSourceAlgorithmType knowledgeSourceAlgorithmType) {
		this.knowledgeSourceAlgorithmType = knowledgeSourceAlgorithmType;
	}
}
