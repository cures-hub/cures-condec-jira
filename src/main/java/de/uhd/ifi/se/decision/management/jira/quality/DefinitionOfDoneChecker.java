package de.uhd.ifi.se.decision.management.jira.quality;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

public class DefinitionOfDoneChecker {

	private static final Map<KnowledgeType, KnowledgeElementCheck> knowledgeElementCheckMap = Map.ofEntries(
			entry(KnowledgeType.DECISION, new DecisionCheck()), //
			entry(KnowledgeType.SOLUTION, new DecisionCheck()), //
			entry(KnowledgeType.ISSUE, new IssueCheck()), //
			entry(KnowledgeType.PROBLEM, new IssueCheck()), //
			entry(KnowledgeType.ALTERNATIVE, new AlternativeCheck()), //
			entry(KnowledgeType.ARGUMENT, new ArgumentCheck()), //
			entry(KnowledgeType.PRO, new ArgumentCheck()), //
			entry(KnowledgeType.CON, new ArgumentCheck()), //
			entry(KnowledgeType.CODE, new CodeCheck()), //
			entry(KnowledgeType.OTHER, new OtherCheck()));

	/**
	 * Checks if the definition of done has been violated for a
	 * {@link KnowledgeElement}.
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
	 * Checks if the documentation of this {@link KnowledgeElement} is complete.
	 *
	 * @return true if this knowledge element is completely documented, else it
	 *         returns false.
	 */
	public static boolean isComplete(KnowledgeElement knowledgeElement) {
		if (knowledgeElement instanceof ElementRecommendation) {
			return true;
		}
		KnowledgeElementCheck knowledgeElementCheck = knowledgeElementCheckMap.get(knowledgeElement.getType());
		return knowledgeElementCheck == null || knowledgeElementCheck.execute(knowledgeElement);
	}

	/**
	 * Iterates recursively over the knowledge graph of the
	 * {@link KnowledgeElement}.
	 * 
	 * @return true if there is at least one incompletely documented knowledge
	 *         element, else it returns false.
	 */
	public static boolean hasIncompleteKnowledgeLinked(KnowledgeElement knowledgeElement) {
		if (!isComplete(knowledgeElement)) {
			return true;
		}
		for (Link link : knowledgeElement.getLinks()) {
			KnowledgeElement oppositeElement = link.getOppositeElement(knowledgeElement);
			if (!isComplete(oppositeElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Iterates recursively over the knowledge graph of the {@link KnowledgeElement}
	 * and checks if it fulfills the minimum coverage. {@link ChangedFile} that are
	 * test files or with less lines of codes than defined in the
	 * {@link DefinitionOfDone} don't require any coverage.
	 *
	 * @return true if there are at least as many elements of the specified
	 *         {@link KnowledgeType} as the minimum coverage demands, else it
	 *         returns false.
	 */
	public static boolean doesNotHaveMinimumCoverage(KnowledgeElement knowledgeElement, KnowledgeType knowledgeType,
			FilterSettings filterSettings) {
		if (!shouldCoverageOfKnowledgeElementBeChecked(knowledgeElement, filterSettings)) {
			return false;
		}

		int linkDistance = filterSettings.getDefinitionOfDone().getMaximumLinkDistanceToDecisions();
		int minimumCoverage = filterSettings.getDefinitionOfDone().getMinimumDecisionsWithinLinkDistance();
		Set<KnowledgeElement> linkedElements = knowledgeElement.getLinkedElements(linkDistance);
		for (KnowledgeElement linkedElement : linkedElements) {
			if (linkedElement.getType() == knowledgeType) {
				minimumCoverage--;
			}
			if (minimumCoverage <= 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Iterates recursively over the knowledge graph of the {@link KnowledgeElement}
	 * and checks if it has any coverage. {@link ChangedFile} that are test files or
	 * with less lines of codes than defined in the {@link DefinitionOfDone} don't
	 * require any coverage.
	 *
	 * @return true if there is at least one element of the specified
	 *         {@link KnowledgeType}, else it returns false.
	 */
	public static boolean hasNoCoverage(KnowledgeElement knowledgeElement, KnowledgeType knowledgeType,
			FilterSettings filterSettings) {
		if (!shouldCoverageOfKnowledgeElementBeChecked(knowledgeElement, filterSettings)) {
			return false;
		}

		int linkDistance = filterSettings.getDefinitionOfDone().getMaximumLinkDistanceToDecisions();
		Set<KnowledgeElement> linkedElements = knowledgeElement.getLinkedElements(linkDistance);
		for (KnowledgeElement linkedElement : linkedElements) {
			if (linkedElement.getType() == knowledgeType) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param knowledgeElement
	 *            {@link KnowledgeElement}, e.g. code file or decision knowledge
	 *            element.
	 * @param filterSettings
	 *            {@link FilterSettings} with {@link DefinitionOfDone}.
	 * @return true if the element should be checked, i.e. decision coverage should
	 *         be measured. Returns false for small code files, test files, and
	 *         irrelevant parts of text.
	 */
	private static boolean shouldCoverageOfKnowledgeElementBeChecked(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		if (knowledgeElement instanceof ChangedFile) {
			int lineNumbersInCodeFile = filterSettings.getDefinitionOfDone().getLineNumbersInCodeFile();
			ChangedFile codeFile = (ChangedFile) knowledgeElement;
			return codeFile.getLineCount() >= lineNumbersInCodeFile && !codeFile.isTestCodeFile();
		}
		return knowledgeElement.getDocumentationLocation() != DocumentationLocation.JIRAISSUETEXT
				|| knowledgeElement.getType() != KnowledgeType.OTHER;
	}

	/**
	 * @return {@link QualityCriterionCheckResult}s of the {@link KnowledgeElement}.
	 */
	public static List<QualityCriterionCheckResult> getQualityCheckResults(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();

		if (DefinitionOfDoneChecker.hasIncompleteKnowledgeLinked(knowledgeElement)) {
			qualityCheckResults.add(new QualityCriterionCheckResult(QualityCriterionType.QUALITY_OF_LINKED_KNOWLEDGE));
		} else {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.QUALITY_OF_LINKED_KNOWLEDGE, false));
		}

		DefinitionOfDone definitionOfDone = ConfigPersistenceManager
				.getDefinitionOfDone(knowledgeElement.getProject().getProjectKey());
		KnowledgeElementCheck knowledgeElementCheck = knowledgeElementCheckMap.get(knowledgeElement.getType());
		qualityCheckResults.add(knowledgeElementCheck.getCoverageQuality(knowledgeElement, filterSettings));
		qualityCheckResults.addAll(knowledgeElementCheck.getQualityCheckResult(knowledgeElement, definitionOfDone));

		return qualityCheckResults;
	}

	/**
	 * @return {@link QualityCriterionCheckResult}s of the {@link KnowledgeElement}
	 *         that violate the DoD.
	 */
	public static List<QualityCriterionCheckResult> getQualityProblems(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		return getQualityCheckResults(knowledgeElement, filterSettings).stream()
				.filter(checkResult -> checkResult.isCriterionViolated()).collect(Collectors.toList());
	}

	/**
	 * @return a string detailing all {@link QualityCriterionType} of the
	 *         {@link KnowledgeElement}.
	 */
	public static String getQualityProblemExplanation(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		if (knowledgeElement.getProject() == null) {
			return "";
		}
		List<QualityCriterionCheckResult> qualityProblems = getQualityProblems(knowledgeElement, filterSettings);
		StringBuilder text = new StringBuilder();
		for (QualityCriterionCheckResult problem : qualityProblems) {
			text.append(problem.getExplanation()).append(System.lineSeparator());
		}
		return text.toString().strip();
	}
}
