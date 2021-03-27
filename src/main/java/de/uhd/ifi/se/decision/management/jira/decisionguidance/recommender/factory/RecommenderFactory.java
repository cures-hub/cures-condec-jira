package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.factory;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.BaseRecommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.IssueBasedRecommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.KeywordBasedRecommender;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;

public class RecommenderFactory {

	public static BaseRecommender<?> getRecommender(RecommenderType recommenderType) {
		if (recommenderType == null) {
			recommenderType = RecommenderType.getDefault();
		}
		switch (recommenderType) {
		case ISSUE:
			return new IssueBasedRecommender();
		default:
			return new KeywordBasedRecommender();
		}
	}
}
