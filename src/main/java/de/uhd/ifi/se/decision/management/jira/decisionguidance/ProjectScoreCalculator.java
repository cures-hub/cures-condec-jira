package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

import java.util.List;

public class ProjectScoreCalculator implements ScoreCalculator {

	int score;

	public ProjectScoreCalculator() {
		this.score = 0;
	}

	@Override
	public int calculateScore() {
		return 0;
	}

	@Override
	public int calculateScore(List<String> keywords, KnowledgeElement knowledgeElement) {

		float numberOfKeywords = keywords.size();
		int matchedKeyWord = 0;

		for (Link link : knowledgeElement.getLinks()) {
			if (link.getTarget().getType() == KnowledgeType.ISSUE) {
				for (String keyword : keywords) {
					if (link.getTarget().getSummary().contains(keyword)) {
						matchedKeyWord += 1;
					}
				}
			}
		}

		float score = 0;
		if (numberOfKeywords != 0)
			score = (matchedKeyWord / numberOfKeywords) * 100;

		return Math.round(score);
	}
}
