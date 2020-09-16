package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class SimpleRecommender extends BaseRecommender {

	private List<Recommendation> recommendations;
	private String keywords;
	KnowledgeElement rootElement;

	public SimpleRecommender(String keywordsString) {
		this.recommendations = new ArrayList<>();
		this.keywords = keywordsString;
	}

	public SimpleRecommender(String keywordsString, List<KnowledgeSource> knowledgeSources) {
		this(keywordsString);
		this.addKnowledgeSource(knowledgeSources);
	}

	@Override
	public List<Recommendation> getRecommendation() {
		if (this.knowledgeSources != null) {
			for (KnowledgeSource knowledgeSource : this.knowledgeSources) {
				if (knowledgeSource.isActivated()) {
					this.recommendations.addAll(knowledgeSource.getResults(this.keywords));
				}
			}
		}
		return this.recommendations;
	}
}
