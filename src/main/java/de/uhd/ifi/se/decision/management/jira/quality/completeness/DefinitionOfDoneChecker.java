package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

public final class DefinitionOfDoneChecker {

	private DefinitionOfDoneChecker() {
	}

	private static final Map<KnowledgeType, CompletenessCheck<? extends KnowledgeElement>> completenessCheckMap = Map
			.ofEntries(entry(KnowledgeType.DECISION, new DecisionCompletenessCheck()),
					entry(KnowledgeType.ISSUE, new DecisionProblemCompletenessCheck()),
					entry(KnowledgeType.ALTERNATIVE, new AlternativeCompletenessCheck()),
					entry(KnowledgeType.ARGUMENT, new ArgumentCompletenessCheck()),
					entry(KnowledgeType.PRO, new ArgumentCompletenessCheck()),
					entry(KnowledgeType.CON, new ArgumentCompletenessCheck()),
					entry(KnowledgeType.CODE, new CodeCompletenessCheck()));

	/**
	 * @issue Should knowledge elements without definition of done be assumed to be
	 *        complete or incomplete?
	 * @decision If no definition of done can be found, the knowledge element is
	 *           assumed to be complete!
	 * 
	 * @param knowledgeElement
	 *            instance of {@link KnowledgeElement}.
	 * @return true if the element is completely documented according to the default
	 *         and configured rules of the {@link DefinitionOfDone}.
	 */
	public static boolean checkDefinitionOfDone(KnowledgeElement knowledgeElement, FilterSettings filterSettings) {
		return !hasIncompleteKnowledgeLinked(knowledgeElement) &&
			!doesNotHaveMinimumCoverage(knowledgeElement, KnowledgeType.DECISION, filterSettings);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean isIncomplete(KnowledgeElement knowledgeElement) {
		if (knowledgeElement instanceof ElementRecommendation) {
			return false;
		}
		CompletenessCheck completenessCheck = completenessCheckMap.get(knowledgeElement.getType());
		return !(completenessCheck == null || completenessCheck.execute(knowledgeElement));
	}

	/**
	 * Iterates recursively over the knowledge graph of the
	 * {@link KnowledgeElement}.
	 * 
	 * @return true if there is at least one incompletely documented knowledge
	 *         element, else it returns false.
	 */
	public static boolean hasIncompleteKnowledgeLinked(KnowledgeElement knowledgeElement) {
		if (isIncomplete(knowledgeElement)) {
			return true;
		}
		for (Link link : knowledgeElement.getLinks()) {
			KnowledgeElement oppositeElement = link.getOppositeElement(knowledgeElement);
			if (isIncomplete(oppositeElement)) {
				return true;
			} else if (hasIncompleteKnowledgeLinked(oppositeElement, knowledgeElement)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasIncompleteKnowledgeLinked(KnowledgeElement oppositeElement,
			KnowledgeElement knowledgeElement) {
		for (Link link : oppositeElement.getLinks()) {
			KnowledgeElement nextElement = link.getOppositeElement(oppositeElement);
			if (nextElement.getId() == knowledgeElement.getId()) {
				continue;
			} else if (isIncomplete(nextElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Iterates recursively over the knowledge graph of the
	 * {@link KnowledgeElement}.
	 *
	 * @return true if there are at least as many issues and decisions as the minimum coverage
	 * 		   demands, else it returns false.
	 */
	public static boolean doesNotHaveMinimumCoverage(KnowledgeElement knowledgeElement, KnowledgeType knowledgeType,
													 FilterSettings filterSettings) {
		RationaleCoverageCalculator calculator = new RationaleCoverageCalculator(filterSettings.getProjectKey());
		int result = calculator.calculateNumberOfDecisionKnowledgeElementsForKnowledgeElement(knowledgeElement, knowledgeType);
		int minimumCoverage = filterSettings.getMinimumDecisionCoverage();
		return result < minimumCoverage;
	}

	/**
	 * Returns a list of failed definition of done criteria.
	 */
	public static List<String> getFailedDefinitionOfDoneCheckCriteria(KnowledgeElement knowledgeElement, FilterSettings filterSettings) {
		List<String> failedCheckCriteria = new ArrayList<>();
		if (DefinitionOfDoneChecker.hasIncompleteKnowledgeLinked(knowledgeElement)) {
			failedCheckCriteria.add("hasIncompleteKnowledgeLinked");
		}
		if (DefinitionOfDoneChecker.doesNotHaveMinimumCoverage(knowledgeElement, KnowledgeType.DECISION, filterSettings)) {
			failedCheckCriteria.add("doesNotHaveMinimumCoverage");
		}
		return failedCheckCriteria;
	}
}
