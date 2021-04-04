package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

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
		InputMethod inputMethod = InputMethod.getIssueBasedIn(knowledgeSource);
		return inputMethod.getRecommendations(input);
	}

	@Override
	public RecommenderType getRecommenderType() {
		return RecommenderType.ISSUE;
	}
}
