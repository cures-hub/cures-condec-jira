package de.uhd.ifi.se.decision.management.jira.view.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculator;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.ScoreCalculatorFactory;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSourceType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlRootElement(name = "Recommendation")
public class ProjectRecommendation extends Recommendation {

	List<String> keywords;
	KnowledgeElement parentIssue;

	public ProjectRecommendation(String knowledgeSourceName, KnowledgeElement recommendations, List<String> keywords, KnowledgeElement parentIssue) {
		this.knowledgeSourceName = knowledgeSourceName;
		this.recommendations = recommendations;
		this.knowledgeSourceType = KnowledgeSourceType.PROJECT;
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
