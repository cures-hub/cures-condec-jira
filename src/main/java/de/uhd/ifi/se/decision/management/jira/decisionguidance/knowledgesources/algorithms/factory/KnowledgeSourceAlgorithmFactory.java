package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithm;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms.KnowledgeSourceAlgorithmType;

public abstract class KnowledgeSourceAlgorithmFactory<T extends KnowledgeSourceAlgorithm> {

	public abstract T getAlgorithm(KnowledgeSourceAlgorithmType knowledgeSourceAlgorithmType);
}
