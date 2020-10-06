package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithm;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithmType;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.projectsource.ProjectSourceSubstringAlgorithm;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.projectsource.ProjectSourceTokenizedAlgorithm;

public class ProjectKnowledgeSourceAlgorithmFactory extends KnowledgeSourceAlgorithmFactory {

	private String projectKey;
	private String knowledgeSourceName;

	public ProjectKnowledgeSourceAlgorithmFactory() {

	}

	public ProjectKnowledgeSourceAlgorithmFactory(String projectKey, String knowledgeSourceName) {
		this.projectKey = projectKey;
		this.knowledgeSourceName = knowledgeSourceName;
	}

	public KnowledgeSourceAlgorithm getAlgorithm(KnowledgeSourceAlgorithmType knowledgeSourceAlgorithmType) {
		if (knowledgeSourceAlgorithmType == null)
			return new ProjectSourceSubstringAlgorithm();
		switch (knowledgeSourceAlgorithmType) {
			case TOKENIZED:
				return new ProjectSourceTokenizedAlgorithm();
			default:
				return new ProjectSourceSubstringAlgorithm();
		}
	}
}
