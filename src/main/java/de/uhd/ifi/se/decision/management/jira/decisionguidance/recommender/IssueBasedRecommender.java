package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class IssueBasedRecommender extends BaseRecommender<KnowledgeElement> {

	public IssueBasedRecommender() {
		this.recommendations = new ArrayList<>();
		this.knowledgeSources = new ArrayList<>();
	}

	public IssueBasedRecommender(KnowledgeElement knowledgeElement) {
		this();
		this.input = knowledgeElement;
	}

	public IssueBasedRecommender(KnowledgeElement knowledgeElement, List<KnowledgeSource> knowledgeSources) {
		this(knowledgeElement);
		this.addKnowledgeSource(knowledgeSources);
	}

	@Override
	public List<Recommendation> getRecommendations(KnowledgeSource knowledgeSource) {
		knowledgeSource.setRecommenderType(RecommenderType.ISSUE);
		return knowledgeSource.getRecommendations(input);
	}

	@Override
	public RecommenderType getRecommenderType() {
		return RecommenderType.ISSUE;
	}
}
