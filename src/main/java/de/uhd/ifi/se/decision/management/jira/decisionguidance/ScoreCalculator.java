package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import java.util.List;

public interface ScoreCalculator {

	int calculateScore();

	int calculateScore(List<String> keywords, KnowledgeElement knowledgeElement);

}
