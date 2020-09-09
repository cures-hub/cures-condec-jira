package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;

public class ScoreCalculatorFactory {

	private KnowledgeSourceType knowledgeSourceType;

	public ScoreCalculatorFactory(KnowledgeSourceType knowledgeSourceType) {
		this.knowledgeSourceType = knowledgeSourceType;
	}

	public ScoreCalculator createScoreCalculator() {
		return new ProjectScoreCalculator();
	}

}
