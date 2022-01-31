package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;

/**
 * Provides Component in decorator pattern. Context information providers are
 * concrete decorators
 */
public class ContextInformation extends ContextInformationProvider {

	private KnowledgeElement element;
	private LinkRecommendationConfiguration linkRecommendationConfig;

	public ContextInformation(KnowledgeElement element, LinkRecommendationConfiguration linkRecommendationConfig) {
		this.element = element;
		this.linkRecommendationConfig = linkRecommendationConfig;
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
    TreeSet<Recommendation> sortedRecommendations = new TreeSet<Recommendation>();
	recommendations.stream().forEach(recommendation -> {
        if (recommendation.getScore().getValue() >= linkRecommendationConfig.getMinProbability() * 100) {
          sortedRecommendations.add(recommendation);
        }
    });
    int i = 0;
	recommendations.clear();
	// Only the top-5 recommendations are recommended
	for (Recommendation recommendation : sortedRecommendations) {
		recommendations.add(recommendation);
		if (i == 4) {
			break;
		} else {
			i = i + 1;
		}
	}
	return recommendations;
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		RecommendationScore score = new RecommendationScore(0, getName());
		for (ContextInformationProvider contextInformationProvider : linkRecommendationConfig
				.getContextInformationProviders()) {
			if (!contextInformationProvider.isActive()) {
				continue;
			}
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