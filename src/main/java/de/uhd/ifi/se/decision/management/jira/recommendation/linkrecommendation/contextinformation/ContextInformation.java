package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyPersistenceHelper;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;

/**
 * Provides Component in decorator pattern.
 *
 */
public class ContextInformation implements ContextInformationProvider {

	private KnowledgeElement element;
	private List<ContextInformationProvider> contextInformationProviders;

	public ContextInformation(KnowledgeElement element) {
		this.element = element;
		// Add context information providers as concrete decorators
		contextInformationProviders = new ArrayList<>();
		contextInformationProviders.add(new TextualSimilarityContextInformationProvider());
		contextInformationProviders.add(new TracingContextInformationProvider());
		contextInformationProviders.add(new TimeContextInformationProvider());
		contextInformationProviders.add(new UserContextInformationProvider());
		// contextInformationProviders.add(new
		// ActiveElementsContextInformationProvider());
	}

	public List<Recommendation> getLinkSuggestions() {
		KnowledgeGraph graph = KnowledgeGraph.getOrCreate(element.getProject());
		List<KnowledgeElement> unlinkedElements = graph.getUnlinkedElementsAndNotInSameJiraIssue(element);
		List<KnowledgeElement> elementsToKeep = filterDiscardedElements(unlinkedElements);
		List<Recommendation> recommendations = assessRelations(element, elementsToKeep);

		LinkRecommendationConfiguration config = ConfigPersistenceManager
				.getLinkRecommendationConfiguration(element.getProject().getProjectKey());
		Recommendation.normalizeRecommendationScore(recommendations);

		return recommendations.stream()
				.filter(recommendation -> recommendation.getScore().getValue() >= config.getMinProbability() * 100)
				.collect(Collectors.toList());
	}

	private List<KnowledgeElement> filterDiscardedElements(List<KnowledgeElement> unlinkedElements) {
		unlinkedElements.removeAll(ConsistencyPersistenceHelper.getDiscardedLinkSuggestions(element));
		return unlinkedElements;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		RecommendationScore score = new RecommendationScore(0, getName());
		for (ContextInformationProvider contextInformationProvider : contextInformationProviders) {
			RecommendationScore scoreValue = contextInformationProvider.assessRelation(baseElement, otherElement);
			score.addSubScore(scoreValue);
		}
		return score;
	}

	/**
	 * Calculates the relationship between one {@link KnowledgeElement} to a list of
	 * other {@link KnowledgeElement}s. Higher values indicate a higher similarity.
	 * The value is called Context Relationship Indicator in the paper.
	 *
	 * @param baseElement
	 * @param knowledgeElements
	 * @return value of relationship in [0, inf]
	 */
	public List<Recommendation> assessRelations(KnowledgeElement baseElement,
			List<KnowledgeElement> knowledgeElements) {

		List<Recommendation> linkRecommendations = new ArrayList<>();
		for (KnowledgeElement elementToTest : knowledgeElements) {
			if (elementToTest.getTypeAsString().equals(KnowledgeType.OTHER.toString())) {
				// only recommend relevant decision, project, or system knowledge elements
				continue;
			}
			Recommendation linkSuggestion = new LinkRecommendation(baseElement, elementToTest);
			RecommendationScore score = assessRelation(baseElement, elementToTest);
			linkSuggestion.setScore(score);
			linkRecommendations.add(linkSuggestion);
		}
		return linkRecommendations;
	}
}
