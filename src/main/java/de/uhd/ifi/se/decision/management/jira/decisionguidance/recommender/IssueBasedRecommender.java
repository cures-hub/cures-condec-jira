package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class IssueBasedRecommender extends BaseRecommender<KnowledgeElement> {

	private KnowledgeElement knowledgeElement;

	public IssueBasedRecommender(KnowledgeElement knowledgeElement) {
		this.recommendations = new ArrayList<>();
		this.knowledgeSources = new ArrayList<>();
		this.knowledgeElement = knowledgeElement;
	}

	public IssueBasedRecommender(KnowledgeElement knowledgeElement, List<KnowledgeSource> knowledgeSources) {
		this(knowledgeElement);
		this.addKnowledgeSource(knowledgeSources);
	}

	@Override
	public List<Recommendation> getRecommendation() {
		for (KnowledgeSource knowledgeSource : this.knowledgeSources) {
			this.recommendations.addAll(knowledgeSource.getResults(this.knowledgeElement));
		}
		return this.recommendations;
	}

	@Override
	public BaseRecommender evaluate(KnowledgeElement issue) {
		this.knowledgeElement = issue;
		return this;
	}

	@Override
	public List<Recommendation> execute() {
		return this.knowledgeSources.get(0).getResults(this.knowledgeElement);
	}
}
