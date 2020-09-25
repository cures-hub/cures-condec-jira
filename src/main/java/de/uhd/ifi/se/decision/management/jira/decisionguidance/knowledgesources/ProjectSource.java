package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithm;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithmType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.ProjectSourceSubstringAlgorithm;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public class ProjectSource extends KnowledgeSource {

	public ProjectSource(String projectKey) {
		this.projectKey = projectKey;
		this.knowledgeSourceAlgorithmType = KnowledgeSourceAlgorithmType.SUBSTRING;
		this.isActivated = false;
	}

	public ProjectSource(String projectKey, String projectSourceName, boolean isActivated) {
		this.projectKey = projectKey;
		this.name = projectSourceName;
		this.isActivated = isActivated;
		this.knowledgeSourceAlgorithmType = KnowledgeSourceAlgorithmType.SUBSTRING;
	}


	@Override
	public List<Recommendation> getResults(String inputs) {
		KnowledgeSourceAlgorithm knowledgeSourceAlgorithm = new ProjectSourceSubstringAlgorithm(projectKey, name, inputs);
		if (this.knowledgeSourceAlgorithmType == null) return knowledgeSourceAlgorithm.getResults();
		switch (this.knowledgeSourceAlgorithmType) {
			case SUBSTRING:
				knowledgeSourceAlgorithm = new ProjectSourceSubstringAlgorithm(projectKey, name, inputs);
				return knowledgeSourceAlgorithm.getResults();
			default:
				return knowledgeSourceAlgorithm.getResults();
		}
	}

}
