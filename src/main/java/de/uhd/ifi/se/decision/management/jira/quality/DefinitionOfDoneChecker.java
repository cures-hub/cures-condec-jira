package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;

/**
 * Checks the quality of the knowledge documentation according to the
 * {@link DefinitionOfDone}.
 * 
 * @issue How to determine the responsible check class for a knowledge element?
 * @alternative We could use the chain of responsibility pattern to determine
 *              the responsible check class for a knowledge element.
 * @pro Well-known GoF design pattern, helps to decouple the sender of a request
 *      from its receiver.
 * @con The type of the element needs to passed to the check methods. This would
 *      be one parameter more than with the current solution.
 * @decision We use a simple switch case statement to determine the responsible
 *           check class for a knowledge element in the DoDChecker!
 * @pro We can pass the element as a parameter for the check class constructor.
 *      This saves a parameter when requiring the check results for the
 *      different DoD criteria.
 */
public class DefinitionOfDoneChecker {

	/**
	 * @param element
	 *            {@link KnowledgeElement} to be checked.
	 * @return {@link KnowledgeElementCheck} object that offers operations for
	 *         assessing whether the element fulfills or violates the
	 *         {@link DefinitionOfDone}.
	 */
	private static KnowledgeElementCheck createElementCheck(KnowledgeElement element) {
		switch (element.getType()) {
		case DECISION:
			return new DecisionCheck(element);
		case SOLUTION:
			return new DecisionCheck(element);
		case ISSUE:
			return new IssueCheck(element);
		case PROBLEM:
			return new IssueCheck(element);
		case ALTERNATIVE:
			return new AlternativeCheck(element);
		case ARGUMENT:
			return new ArgumentCheck(element);
		case PRO:
			return new ArgumentCheck(element);
		case CON:
			return new ArgumentCheck(element);
		case CODE:
			return new CodeCheck(element);
		default:
			return new OtherCheck(element);
		}
	}

	/**
	 * Checks if the definition of done has been violated or fulfilled for a
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
		return getQualityProblems(knowledgeElement, filterSettings).isEmpty();
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
		KnowledgeElementCheck elementCheck = createElementCheck(knowledgeElement);
		return elementCheck == null || elementCheck.isDefinitionOfDoneFulfilled();
	}

	/**
	 * Iterates recursively over the knowledge graph of the
	 * {@link KnowledgeElement}.
	 * 
	 * @return true if there is at least one incompletely documented knowledge
	 *         element, else it returns false.
	 */
	public static boolean hasIncompleteKnowledgeLinked(KnowledgeElement knowledgeElement) {
		for (Link link : knowledgeElement.getLinks()) {
			KnowledgeElement oppositeElement = link.getOppositeElement(knowledgeElement);
			if (!isComplete(oppositeElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return {@link QualityCriterionCheckResult}s of the {@link KnowledgeElement}.
	 */
	public static List<QualityCriterionCheckResult> getQualityCheckResults(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();
		KnowledgeElementCheck elementCheck = createElementCheck(knowledgeElement);
		qualityCheckResults.add(elementCheck.getCoverageQuality(filterSettings));
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager
				.getDefinitionOfDone(knowledgeElement.getProject().getProjectKey());
		qualityCheckResults.addAll(elementCheck.getQualityCheckResult(definitionOfDone));
		qualityCheckResults.add(checkLinkKnowledgeQuality(knowledgeElement, qualityCheckResults));
		return qualityCheckResults;
	}

	private static QualityCriterionCheckResult checkLinkKnowledgeQuality(KnowledgeElement element,
			List<QualityCriterionCheckResult> qualityCheckResults) {
		QualityCriterionCheckResult checkResult = new QualityCriterionCheckResult(
				QualityCriterionType.QUALITY_OF_LINKED_KNOWLEDGE);
		if (!getQualityProblems(qualityCheckResults).isEmpty()) {
			checkResult.setExplanation("This knowledge element violates the DoD (see criteria above). "
					+ "You need to fix these violations first, then the linked knowledge will be checked.");
			return checkResult;
		}
		if (DefinitionOfDoneChecker.hasIncompleteKnowledgeLinked(element)) {
			return checkResult;
		}
		return new QualityCriterionCheckResult(QualityCriterionType.QUALITY_OF_LINKED_KNOWLEDGE, false);
	}

	/**
	 * @return {@link QualityCriterionCheckResult}s of the {@link KnowledgeElement}
	 *         that violate the DoD.
	 */
	public static List<QualityCriterionCheckResult> getQualityProblems(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		return getQualityProblems(getQualityCheckResults(knowledgeElement, filterSettings));
	}

	public static List<QualityCriterionCheckResult> getQualityProblems(List<QualityCriterionCheckResult> checkResults) {
		return checkResults.stream().filter(checkResult -> checkResult.isCriterionViolated())
				.collect(Collectors.toList());
	}

	/**
	 * @return a string detailing all failed {@link QualityCriterionCheckResult}s of
	 *         the {@link KnowledgeElement}, i.e. the quality problems.
	 */
	public static String getQualityProblemExplanation(KnowledgeElement knowledgeElement,
			FilterSettings filterSettings) {
		if (knowledgeElement.getProject() == null) {
			return "";
		}
		List<QualityCriterionCheckResult> qualityProblems = getQualityProblems(knowledgeElement, filterSettings);
		StringBuilder text = new StringBuilder();
		for (QualityCriterionCheckResult problem : qualityProblems) {
			text.append(problem.getExplanation()).append(System.lineSeparator()).append(System.lineSeparator());
		}
		return text.toString().strip();
	}
}
