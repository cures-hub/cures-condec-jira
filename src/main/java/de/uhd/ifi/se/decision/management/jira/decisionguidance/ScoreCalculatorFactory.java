package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;

public class ScoreCalculatorFactory {

	private KnowledgeSourceType knowledgeSourceType;

	public ScoreCalculatorFactory(KnowledgeSourceType knowledgeSourceType) {
		this.knowledgeSourceType = knowledgeSourceType;
	}

	public ScoreCalculator createScoreCalculator() {
		switch (this.knowledgeSourceType) {

			case RDF:
				return new DBPediaScoreCalculator();
			case PROJECT:
				return new ProjectScoreCalculator();
		}
		return new ProjectScoreCalculator();
	}

}
