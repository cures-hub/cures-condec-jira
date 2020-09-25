package de.uhd.ifi.se.decision.management.jira.decisionguidance.scorecalculator;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;

public class ScoreCalculatorFactory {

	private KnowledgeSourceType knowledgeSourceType;

	public ScoreCalculatorFactory(KnowledgeSourceType knowledgeSourceType) {
		this.knowledgeSourceType = knowledgeSourceType;
	}

	public ScoreCalculator createScoreCalculator() {
		if (this.knowledgeSourceType == null) return null;
		switch (this.knowledgeSourceType) {
			case RDF:
				return new DBPediaScoreCalculator();
			case PROJECT:
				return new ProjectScoreCalculator();
			default:
				return null;
		}
	}

}
