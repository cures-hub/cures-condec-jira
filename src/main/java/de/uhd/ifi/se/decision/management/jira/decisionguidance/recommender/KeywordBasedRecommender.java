package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class KeywordBasedRecommender extends BaseRecommender<String> {


	private String keywords;

	public KeywordBasedRecommender(String keywordsString) {
		this.recommendations = new ArrayList<>();
		this.knowledgeSources = new ArrayList<>();
		this.keywords = keywordsString;
	}

	public KeywordBasedRecommender(String keywordsString, List<KnowledgeSource> knowledgeSources) {
		this(keywordsString);
		this.addKnowledgeSource(knowledgeSources);
	}

	@Override
	public List<Recommendation> getRecommendation() {
		for (KnowledgeSource knowledgeSource : this.knowledgeSources) {
			this.recommendations.addAll(knowledgeSource.getResults(this.keywords));
		}
		return this.recommendations;
	}

	@Override
	public BaseRecommender evaluate(String keywords) {
		this.keywords = keywords;
		return this;
	}

	@Override
	public List<Recommendation> execute() {
		return this.knowledgeSources.get(0).getResults(this.keywords);
	}


}
