package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.quality.generalmetrics.GeneralMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

public final class DefinitionOfDoneChecker {

	private static final Map<KnowledgeType, KnowledgeElementCheck> knowledgeElementCheckMap = Map.ofEntries(
			entry(KnowledgeType.DECISION, new DecisionCheck()), //
			entry(KnowledgeType.ISSUE, new IssueCheck()), //
			entry(KnowledgeType.ALTERNATIVE, new AlternativeCheck()), //
			entry(KnowledgeType.ARGUMENT, new ArgumentCheck()), //
			entry(KnowledgeType.PRO, new ArgumentCheck()), //
			entry(KnowledgeType.CON, new ArgumentCheck()), //
			entry(KnowledgeType.CODE, new CodeCheck()));

	private DefinitionOfDoneChecker() {
	}

	/**
	 * Checks if the definition of done has been violated for a {@link KnowledgeElement}.
	 *
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
		return !hasIncompleteKnowledgeLinked(knowledgeElement)
				&& !doesNotHaveMinimumCoverage(knowledgeElement, KnowledgeType.DECISION, filterSettings);
	}

	/**
	 * Checks if the definition of done has been violated for a {@link KnowledgeElement}.
	 * USed in {@link GeneralMetricCalculator}.
	 *
	 * @param knowledgeElement
	 *            instance of {@link KnowledgeElement}.
	 * @param calculator uses already existing RationaleCoverageCalculator to
	 * 	                 increase performance when iterating over a set of elements.
	 * @return true if the element is completely documented according to the default
	 *         and configured rules of the {@link DefinitionOfDone}.
	 */
	public static boolean checkDefinitionOfDone(KnowledgeElement knowledgeElement, FilterSettings filterSettings,
												RationaleCoverageCalculator calculator) {
		return !hasIncompleteKnowledgeLinked(knowledgeElement)
			&& !doesNotHaveMinimumCoverage(knowledgeElement, KnowledgeType.DECISION, filterSettings, calculator);
	}

	/**
	 * Checks if the documentation of this {@link KnowledgeElement} is incomplete.
	 *
	 * @return true if this knowledge element is incompletely documented,
	 *         else it returns false.
	 */
	public static boolean isIncomplete(KnowledgeElement knowledgeElement) {
		if (knowledgeElement instanceof ElementRecommendation) {
			return false;
		}
		KnowledgeElementCheck knowledgeElementCheck = knowledgeElementCheckMap.get(knowledgeElement.getType());
		return !(knowledgeElementCheck == null || knowledgeElementCheck.execute(knowledgeElement));
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
	 * @return true if there are at least as many elements of the specified
	 *         {@link KnowledgeType} as the minimum coverage demands, else it
	 *         returns false.
	 */
	public static boolean doesNotHaveMinimumCoverage(KnowledgeElement knowledgeElement, KnowledgeType knowledgeType,
			FilterSettings filterSettings) {
		RationaleCoverageCalculator calculator = new RationaleCoverageCalculator(filterSettings);
		return doesNotHaveMinimumCoverage(knowledgeElement, knowledgeType, filterSettings, calculator);
	}

	/**
	 * Iterates recursively over the knowledge graph of the
	 * {@link KnowledgeElement}.
	 * Used in {@link GeneralMetricCalculator}.
	 *
	 * @param calculator uses already existing RationaleCoverageCalculator to
	 *                   increase performance when iterating over a set of elements.
	 *
	 * @return true if there are at least as many elements of the specified
	 *         {@link KnowledgeType} as the minimum coverage demands, else it
	 *         returns false.
	 */
	public static boolean doesNotHaveMinimumCoverage(KnowledgeElement knowledgeElement, KnowledgeType knowledgeType,
			 FilterSettings filterSettings, RationaleCoverageCalculator calculator) {
		int result = calculator.calculateNumberOfDecisionKnowledgeElementsForKnowledgeElement(knowledgeElement,
			knowledgeType);
		int minimumCoverage = filterSettings.getDefinitionOfDone().getMinimumDecisionsWithinLinkDistance();
		return result < minimumCoverage;
	}

	/**
	 * Iterates recursively over the knowledge graph of the
	 * {@link KnowledgeElement}.
	 *
	 * @return true if there is at least one element of the specified
	 *         {@link KnowledgeType}, else it returns false.
	 */
	public static boolean hasNoCoverage(KnowledgeElement knowledgeElement, KnowledgeType knowledgeType,
			FilterSettings filterSettings) {
		RationaleCoverageCalculator calculator = new RationaleCoverageCalculator(filterSettings);
		return hasNoCoverage(knowledgeElement, knowledgeType, calculator);
	}

	/**
	 * Iterates recursively over the knowledge graph of the
	 * {@link KnowledgeElement}.
	 *
	 * @param calculator uses already existing RationaleCoverageCalculator to
	 *                   increase performance when iterating over a set of elements.
	 *
	 * @return true if there is at least one element of the specified
	 *         {@link KnowledgeType}, else it returns false.
	 */
	public static boolean hasNoCoverage(KnowledgeElement knowledgeElement, KnowledgeType knowledgeType,
			RationaleCoverageCalculator calculator) {
		int result = calculator.calculateNumberOfDecisionKnowledgeElementsForKnowledgeElement(knowledgeElement,
			knowledgeType);
		return result == 0;
	}

	/**
	 * @return a list of {@link QualityProblem} of the {@link KnowledgeElement}.
	 */
	public static List<QualityProblem> getQualityProblems(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		List<QualityProblem> qualityProblems = new ArrayList<>();

		if (DefinitionOfDoneChecker.doesNotHaveMinimumCoverage(knowledgeElement, KnowledgeType.DECISION,
				filterSettings)) {
			if (DefinitionOfDoneChecker.hasNoCoverage(knowledgeElement, KnowledgeType.DECISION, filterSettings)) {
				qualityProblems.add(QualityProblem.NODECISIONCOVERAGE);
			} else {
				qualityProblems.add(QualityProblem.DECISIONCOVERAGETOOLOW);
			}
		}

		if (DefinitionOfDoneChecker.hasIncompleteKnowledgeLinked(knowledgeElement)) {
			qualityProblems.add(QualityProblem.INCOMPLETEKNOWLEDGELINKED);
		}

		if (knowledgeElement.getType().isDecisionKnowledge()) {
			DefinitionOfDone definitionOfDone = ConfigPersistenceManager
					.getDefinitionOfDone(knowledgeElement.getProject().getProjectKey());
			KnowledgeElementCheck knowledgeElementCheck = knowledgeElementCheckMap.get(knowledgeElement.getType());
			qualityProblems.addAll(knowledgeElementCheck.getQualityProblems(knowledgeElement, definitionOfDone));
		}
		return qualityProblems;
	}

	/**
	 * @return a string detailing all {@link QualityProblem} of the
	 *         {@link KnowledgeElement}.
	 */
	public static String getQualityProblemExplanation(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		List<QualityProblem> qualityProblems = getQualityProblems(knowledgeElement, filterSettings);
		String text = "";
		for (QualityProblem problem : qualityProblems) {
			if (problem.equals(QualityProblem.NODECISIONCOVERAGE)) {
				text += problem.getDescription() + System.lineSeparator() + System.lineSeparator();
			} else if (problem.equals(QualityProblem.DECISIONCOVERAGETOOLOW)) {
				text += problem.getDescription() + System.lineSeparator() + System.lineSeparator();
			} else if (problem.equals(QualityProblem.INCOMPLETEKNOWLEDGELINKED)) {
				text += problem.getDescription() + System.lineSeparator() + System.lineSeparator();
			} else {
				text += problem.getDescription() + System.lineSeparator();
			}
		}
		return text.strip();
	}

	/**
	 * @return an ArrayNode of ObjectNodes detailing all {@link QualityProblem} of
	 *         the {@link KnowledgeElement}.
	 */
	public static ArrayNode getQualityProblemsAsJson(KnowledgeElement knowledgeElement, FilterSettings filterSettings) {
		List<QualityProblem> qualityProblems = getQualityProblems(knowledgeElement, filterSettings);
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode qualityProblemsJson = mapper.createArrayNode();
		for (QualityProblem problem : qualityProblems) {
			qualityProblemsJson.add(problem.getJson());
		}

		return qualityProblemsJson;
	}
}
