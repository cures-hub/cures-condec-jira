package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.algorithms;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.List;

public interface KnowledgeSourceAlgorithm {

	List<Recommendation> getResults(String inputs);
}
