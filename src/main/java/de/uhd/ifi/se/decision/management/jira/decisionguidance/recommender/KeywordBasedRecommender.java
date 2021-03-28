package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.InputMethod;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputString;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSourceInputString;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

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
	public List<Recommendation> getRecommendations(KnowledgeSource knowledgeSource) {
		InputMethod inputMethod = null;
		if (knowledgeSource instanceof ProjectSource) {
			inputMethod = new ProjectSourceInputString();
		} else {
			inputMethod = new RDFSourceInputString();
		}
		inputMethod.setKnowledgeSource(knowledgeSource);
		return inputMethod.getRecommendations(input);
	}

	@Override
	public RecommenderType getRecommenderType() {
		return RecommenderType.KEYWORD;
	}
}
