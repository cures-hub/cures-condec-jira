package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class SimpleRecommender extends BaseRecommender {

	private List<Recommendation> recommendations;
	private String keyword;

	public SimpleRecommender(String keyword) {
		this.recommendations = new ArrayList<>();
		this.keyword = keyword;
	}

	@Override
	public List<Recommendation> getResults() {
		if(this.knowledgeSources != null) {
			for(KnowledgeSource knowledgeSource : this.knowledgeSources) {
				if(knowledgeSource.isActivated()) {
					this.recommendations.add(knowledgeSource.getResults(this.keyword));
				}
			}
		}

		return this.recommendations;
	}
}
