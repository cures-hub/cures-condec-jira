package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleRecommender extends BaseRecommender {

	private List<Recommendation> recommendations;
	private List<String> keywords;

	public SimpleRecommender(String keywordsString) {
		this.recommendations = new ArrayList<>();
		this.keywords = Arrays.asList(keywordsString.split(" "));
	}

	@Override
	public List<Recommendation> getResults() {
		for(String keyword : this.keywords) {
			if (this.knowledgeSources != null) {
				for (KnowledgeSource knowledgeSource : this.knowledgeSources) {
					if (knowledgeSource.isActivated()) {
						this.recommendations.add(knowledgeSource.getResults(keyword));
					}
				}
			}
		}

		return this.recommendations;
	}
}
