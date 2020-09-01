package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.Recommendation;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecommender {

	List<KnowledgeSource> knowledgeSources = new ArrayList<>();

	protected void addKnowledgeSource(KnowledgeSource knowledgeSource) {
		this.knowledgeSources.add(knowledgeSource);
	}

	public void addKnowledgeSource(List<? extends KnowledgeSource> knowledgeSources) {
		this.knowledgeSources.addAll(knowledgeSources);
	}

	abstract public List<Recommendation> getResults();


}
