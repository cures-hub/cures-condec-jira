package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculator;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculatorFactory;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import java.util.List;

public class ProjectRecommendation extends Recommendation {

	List<String> keywords;
	KnowledgeElement parentIssue;

	public ProjectRecommendation(String knowledgeSourceName, String recommendations, List<String> keywords, KnowledgeElement parentIssue, String url) {
		super(knowledgeSourceName, recommendations,KnowledgeSourceType.PROJECT,url);
		this.parentIssue = parentIssue;
		this.keywords = keywords;
	}

	@Override
	public int getScore() {
		ScoreCalculatorFactory scoreCalculatorFactory = new ScoreCalculatorFactory(this.getKnowledgeSourceType());
		ScoreCalculator scoreCalculator = scoreCalculatorFactory.createScoreCalculator();
		int score = scoreCalculator.calculateScore(this.keywords, parentIssue);
		return score;
	}

}
