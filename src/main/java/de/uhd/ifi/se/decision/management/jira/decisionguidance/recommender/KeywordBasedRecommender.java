package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class KeywordBasedRecommender extends BaseRecommender<String> {


	public KeywordBasedRecommender() {
		this.recommendations = new ArrayList<>();
		this.knowledgeSources = new ArrayList<>();
	}

	public KeywordBasedRecommender(String keywordsString) {
		this();
		this.input = keywordsString;
	}

	public KeywordBasedRecommender(String keywordsString, List<KnowledgeSource> knowledgeSources) {
		this(keywordsString);
		this.addKnowledgeSource(knowledgeSources);
	}


	@Override
	public List<Recommendation> getResultFromKnowledgeSource(KnowledgeSource knowledgeSource) {
		knowledgeSource.setRecommenderType(RecommenderType.KEYWORD);
		return knowledgeSource.getResults(input);
	}
}
