package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.ProjectRecommendation;

import java.util.List;

public class ProjectScoreCalculator implements ScoreCalculator<ProjectRecommendation> {

	@Override
	public int calculateScore(ProjectRecommendation recommendation) {

		List<String> keywords = recommendation.getKeywords();
		KnowledgeElement parentIssue = recommendation.getParentIssue();


		float numberOfKeywords = keywords.size();
		int matchedKeyWord = 0;
		int numberOfIssues = 0;

		for (Link link : parentIssue.getLinks()) {
			if (link.getTarget().getType() == KnowledgeType.ISSUE) {
				numberOfIssues += 1;
				for (String keyword : keywords) {
					if (link.getTarget().getSummary().contains(keyword)) {
						matchedKeyWord += 1;
					}
				}
			}
		}

		float score = 0;
		if (numberOfKeywords != 0)
			score = (matchedKeyWord / (numberOfKeywords * numberOfIssues)) * 100;

		return Math.round(score);
	}

}
