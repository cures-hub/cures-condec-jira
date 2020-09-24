package de.uhd.ifi.se.decision.management.jira.decisionguidance.scorecalculator;

import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.DBPediaRecommendation;

import java.util.Arrays;
import java.util.List;

public class DBPediaScoreCalculator implements ScoreCalculator<DBPediaRecommendation> {


	@Override
	public int calculateScore(DBPediaRecommendation recommendation) {

		List<String> keywords = Arrays.asList(recommendation.getKeywords().split("_"));
		List<String> inputs = Arrays.asList(recommendation.getInput().split(" "));

		float inputLength = inputs.size();
		int match = 0;

		for (String keyword : keywords) {
			if (inputs.contains(keyword)) match += 1;
		}

		float score = (match / inputLength) * 100;

		return Math.round(score);

	}

}
