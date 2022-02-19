package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.recommendation.DiscardedRecommendationPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendationConfiguration;

/**
 * This class is part of the Decorator design pattern. Provides Component in
 * decorator pattern. Context information providers are concrete decorators
 */
public class ContextInformation extends ContextInformationProvider {

	private KnowledgeElement element;
	private LinkRecommendationConfiguration linkRecommendationConfig;

	/**
	 * @param element
	 *            selected/source element for that link recommendations should be
	 *            made and duplicates be detected.
	 * @param linkRecommendationConfig
	 *            {@link LinkRecommendationConfiguration} object.
	 */
	public ContextInformation(KnowledgeElement element, LinkRecommendationConfiguration linkRecommendationConfig) {
		this.element = element;
		this.linkRecommendationConfig = linkRecommendationConfig;
	}

	/**
	 * @return the top-k {@link LinkRecommendation}s with a
	 *         {@link RecommendationScore} above the threshold after sorting all the
	 *         recommendations by their {@link RecommendationScore}. Discarded
	 *         recommendations are marked as such. Potential duplicates are also
	 *         marked as such by their {@link RecommendationType}.
	 */
	public List<LinkRecommendation> getLinkRecommendations() {
		KnowledgeGraph graph = KnowledgeGraph.getInstance(element.getProject());
		List<KnowledgeElement> unlinkedElements = graph.getUnlinkedElementsAndNotInSameJiraIssue(element);
		List<LinkRecommendation> recommendations = assessRelations(element, unlinkedElements);
		recommendations = getTopKRecommendations(recommendations);
		return markDiscardedRecommendations(recommendations);
	}

	@Override
	public RecommendationScore assessRelation(KnowledgeElement baseElement, KnowledgeElement otherElement) {
		RecommendationScore score = new RecommendationScore();
		score.setExplanation(getName());
		float maxAchievableScore = 0.0f;
		for (ContextInformationProvider contextInformationProvider : linkRecommendationConfig
				.getContextInformationProviders()) {
			if (!contextInformationProvider.isActive()) {
				continue;
			}
			RecommendationScore subScore = contextInformationProvider.assessRelation(baseElement, otherElement);
			float weightValue = contextInformationProvider.getWeightValue();

			if (weightValue > 0) {
				maxAchievableScore += contextInformationProvider.getWeightValue();
			} else {
				subScore.setExplanation("Do not " + subScore.getExplanation().toLowerCase());
			}

			subScore.weightValue(weightValue); // multiplies rule value with weight value
			score.addSubScore(subScore);
		}
		score.normalizeTo(maxAchievableScore);
		return score;
	}

	/**
	 * @param recommendations
	 *            all {@link LinkRecommendation}s as an unsorted collection.
	 * @return the top-k recommendations with a {@link RecommendationScore} after
	 *         sorting all the recommendations by their {@link RecommendationScore}.
	 */
	private List<LinkRecommendation> getTopKRecommendations(List<LinkRecommendation> recommendations) {
		Set<LinkRecommendation> sortedRecommendations = new TreeSet<>(recommendations);
		recommendations.clear();
		int i = 0;
		int k = linkRecommendationConfig.getMaxRecommendations(); // top k
		for (LinkRecommendation recommendation : sortedRecommendations) {
			if (i == k) {
				break;
			}
			recommendations.add(recommendation);
			++i;
		}
		return recommendations;
	}

	/**
	 * @param recommendations
	 *            {@link LinkRecommendation}s, sorting does not matter.
	 * @return same {@link LinkRecommendation}s, but the recommendations that were
	 *         discarded by the user are marked as such.
	 * @see DiscardedRecommendationPersistenceManager
	 */
	private List<LinkRecommendation> markDiscardedRecommendations(List<LinkRecommendation> recommendations) {
		List<KnowledgeElement> discardedElements = DiscardedRecommendationPersistenceManager
				.getDiscardedLinkRecommendations(element);
		for (LinkRecommendation recommendation : recommendations) {
			if (discardedElements.contains(recommendation.getTarget())) {
				recommendation.setDiscarded(true);
			}
		}
		return recommendations;
	}

	/**
	 * Makes recommendations whether one {@link KnowledgeElement} should be linked
	 * to other {@link KnowledgeElement}s that are currently not linked to it or
	 * whether it is even a potential duplicate.
	 *
	 * @param baseElement
	 *            {@link KnowledgeElement} for that new links should be recommended
	 *            (see {@link LinkRecommendation}).
	 * @param knowledgeElements
	 *            other {@link KnowledgeElement}s in the {@link KnowledgeGraph} that
	 *            are not directly linked.
	 * @return list of {@link LinkRecommendation}s. For each recommendation, the
	 *         {@link RecommendationScore} indicates whether the element should be
	 *         linked. Only link recommendations with a score above the threshold
	 *         {@link LinkRecommendationConfiguration#getMinProbability()} are
	 *         returned.
	 */
	public List<LinkRecommendation> assessRelations(KnowledgeElement baseElement,
			List<KnowledgeElement> knowledgeElements) {

		List<LinkRecommendation> linkRecommendations = new ArrayList<>();

		for (KnowledgeElement elementToTest : knowledgeElements) {
			if (elementToTest.getTypeAsString().equals(KnowledgeType.OTHER.toString())) {
				// only recommend relevant decision, project, or system knowledge elements
				continue;
			}
			LinkRecommendation recommendation = new LinkRecommendation(baseElement, elementToTest);
			RecommendationScore score = assessRelation(baseElement, elementToTest);
			if (score.getValue() < linkRecommendationConfig.getMinProbability()) {
				continue;
			}
			recommendation.setScore(score);
			if (score.isPotentialDuplicate()) {
				recommendation.setRecommendationType(RecommendationType.DUPLICATE);
			}
			linkRecommendations.add(recommendation);
		}
		return linkRecommendations;
	}
}