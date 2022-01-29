package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;

/**
 * Provides Component in decorator pattern.
 *
 */
public class ContextInformation extends ContextInformationProvider {

	private KnowledgeElement element;
	private List<ContextInformationProvider> contextInformationProviders;

	public ContextInformation(KnowledgeElement element, LinkRecommendationConfiguration linkRecommendationConfig) {
		this.element = element;
		// Add context information providers as concrete decorators
		contextInformationProviders = linkRecommendationConfig.getContextInformationProviders();
	}

	public List<Recommendation> getLinkRecommendations() {
		KnowledgeGraph graph = KnowledgeGraph.getInstance(element.getProject());
		List<KnowledgeElement> unlinkedElements = graph.getUnlinkedElementsAndNotInSameJiraIssue(element);
		List<Recommendation> recommendations = assessRelations(element, unlinkedElements);
		RecommendationScore duplicateScore = assessRelation(element, element);
		recommendations = Recommendation.normalizeRecommendationScore(duplicateScore.getSumOfSubScores(),
				recommendations);
		recommendations = filterUselessRecommendations(recommendations);
		return markDiscardedRecommendations(recommendations);
	}

	private List<Recommendation> markDiscardedRecommendations(List<Recommendation> recommendations) {
		List<KnowledgeElement> discardedElements = DiscardedRecommendationPersistenceManager
				.getDiscardedLinkRecommendations(element);
		return markDiscardedRecommendations(recommendations, discardedElements);
	}

	public static List<Recommendation> markDiscardedRecommendations(List<Recommendation> recommendations,
			List<KnowledgeElement> discardedElements) {
		recommendations.stream()
				.filter(recommendation -> discardedElements.contains(((LinkRecommendation) recommendation).getTarget()))
				.map(recommendation -> {
					recommendation.setDiscarded(true);
					return recommendation;
				}).collect(Collectors.toList());
		return recommendations;
	}

	private List<Recommendation> filterUselessRecommendations(List<Recommendation> recommendations) {
		LinkRecommendationConfiguration config = ConfigPersistenceManager
				.getLinkRecommendationConfiguration(element.getProject().getProjectKey());
		return recommendations.stream()
				.filter(recommendation -> recommendation.getScore().getValue() >= config.getMinProbability() * 100)
				.collect(Collectors.toList());
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
	 * Makes recommendations whether one {@link KnowledgeElement} should be linked
	 * to other {@link KnowledgeElement}s that are currently not linked to it.
	 *
	 * @param baseElement
	 *            {@link KnowledgeElement} for that new links should be recommended
	 *            (see {@link LinkRecommendation}).
	 * @param knowledgeElements
	 *            other {@link KnowledgeElement}s in the {@link KnowledgeGraph} that
	 *            are not directly linked.
	 * @return list of {@link Recommendation}s. For each {@link Recommendation}, the
	 *         {@link RecommendationScore} indicates whether the element should be
	 *         linked.
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
