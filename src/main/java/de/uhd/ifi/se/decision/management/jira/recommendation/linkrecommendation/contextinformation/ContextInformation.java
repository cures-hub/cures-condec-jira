package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
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
		for (Recommendation recommendation : recommendations) {
			if (discardedElements.contains(((LinkRecommendation) recommendation).getTarget())) {
				recommendation.setDiscarded(true);
			}
		}
		return recommendations;
	}

	/**
	 * @param recommendations
	 *            all recommendations as an unsorted collection.
	 * @return
	 */
	private List<Recommendation> filterUselessRecommendations(List<Recommendation> recommendations) {
		TreeSet<Recommendation> sortedRecommendations = new TreeSet<Recommendation>(recommendations);
		recommendations.clear();
		int i = 0;
		int k = linkRecommendationConfig.getMaxRecommendations(); // top k
		for (Recommendation recommendation : sortedRecommendations) {
			if (i == k) {
				break;
			}
			if (recommendation.getScore().getValue() >= linkRecommendationConfig.getMinProbability()) {
				recommendations.add(recommendation);
			}
			++i;
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
			RecommendationScore subScore = contextInformationProvider.assessRelation(baseElement, otherElement);

			float weightValue = contextInformationProvider.getWeightValue();

			// Reverse rule effect if weight is negative
			if (weightValue < 0) {
				subScore.setValue(1 - subScore.getValue());
			}
			// Apply weight onto rule impact
			subScore.weightValue(Math.abs(weightValue));
			score.addSubScore(subScore);
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
			LinkRecommendation linkSuggestion = new LinkRecommendation(baseElement, elementToTest);
			RecommendationScore score = assessRelation(baseElement, elementToTest);
			
			// Go through the selected rules and increase the max available score accordingly,
			// used to normalize the final score
			double maxAchievableScore = 0.0;
			for (ContextInformationProvider contextInformationProvider : linkRecommendationConfig
					.getContextInformationProviders()) {
				if (!contextInformationProvider.isActive()) {
					continue;
				}
				maxAchievableScore += Math.abs(contextInformationProvider.getWeightValue());
			}
			score.setValue((float) (score.getValue() / maxAchievableScore));
			linkSuggestion.setScore(score);
			if (score.isPotentialDuplicate()) {
				linkSuggestion.setRecommendationType(RecommendationType.DUPLICATE);
			}
			linkRecommendations.add(linkSuggestion);
		}
		return linkRecommendations;
	}
}