package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

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

	public List<KnowledgeSource> getKnowledgeSources() {
		return this.knowledgeSources;
	}

	abstract public List<Recommendation> getResults();


}
