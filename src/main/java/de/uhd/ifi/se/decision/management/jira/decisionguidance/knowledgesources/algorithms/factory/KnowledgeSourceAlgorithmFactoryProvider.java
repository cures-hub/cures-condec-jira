package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;

public class KnowledgeSourceAlgorithmFactoryProvider {

	public static KnowledgeSourceAlgorithmFactory getFactory(KnowledgeSourceType knowledgeSourceType) {
		switch (knowledgeSourceType) {
			default:
				return new ProjectKnowledgeSourceAlgorithmFactory();
		}
	}
}
