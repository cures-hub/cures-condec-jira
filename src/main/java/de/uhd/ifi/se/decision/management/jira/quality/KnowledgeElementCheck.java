package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.List;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;

/**
 * Checks whether a given {@link KnowledgeElement} is documented correctly
 * according to the {@link DefinitionOfDone} (DoD).
 *
 * For example, an argument needs to be linked to at least one solution option
 * (decision or alternative) in the {@link KnowledgeGraph}. Otherwise, it is
 * incomplete, i.e., its documentation needs to be improved.
 */
public abstract class KnowledgeElementCheck {

	protected KnowledgeElement element;

	public KnowledgeElementCheck(KnowledgeElement elementToBeChecked) {
		this.element = elementToBeChecked;
	}

	/**
	 * Executes the completeness check for the given knowledge element.
	 * 
	 * @param knowledgeElement
	 *            instance of {@link KnowledgeElement} or of a subclass, e.g.
	 *            {@link ChangedFile}, {@link PartOfJiraIssueText}, or
	 *            {@link Recommendation}.
	 * @return true if the element is completely documented according to the default
	 *         and configured rules of the {@link DefinitionOfDone}.
	 */
	public boolean isDefinitionOfDoneFulfilled() {
		String projectKey = element.getProject().getProjectKey();
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(projectKey);
		return getQualityCheckResult(definitionOfDone).stream()
				.noneMatch(checkResult -> checkResult.isCriterionViolated());
	}

	/**
	 * Checks the default and configurable rules that a knowledge element needs to
	 * fulfill to be correctly documented according to the {@link DefinitionOfDone}.
	 * For example, a default rule is that each decision problem (=issue) needs to
	 * be linked to a decision to be complete. A configurable rule is that each
	 * decision needs to be linked to at least one pro-argument to be complete.
	 * 
	 * @param knowledgeElement
	 *            instance of {@link KnowledgeElement} or of a subclass, e.g.
	 *            {@link ChangedFile}, {@link PartOfJiraIssueText}, or
	 *            {@link Recommendation}.
	 * @param definitionOfDone
	 *            instance of {@link DefinitionOfDone}.
	 * @return a list of {@link QualityCriterionCheckResult}s according to the
	 *         default and configured rules of the {@link DefinitionOfDone}.
	 */
	abstract List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone);

	public QualityCriterionCheckResult getCoverageQuality(FilterSettings filterSettings) {
		QualityCriterionCheckResult checkResult = new QualityCriterionCheckResult(
				QualityCriterionType.DECISION_COVERAGE, false);
		int linkDistance = filterSettings.getDefinitionOfDone().getMaximumLinkDistanceToDecisions();
		int minimumCoverage = filterSettings.getDefinitionOfDone().getMinimumDecisionsWithinLinkDistance();
		String requiredCoverage = filterSettings.getDefinitionOfDone().getRequiredCoverageExplanation();
		Set<KnowledgeElement> linkedElements = element.getLinkedElements(linkDistance);
		checkResult.setExplanation(requiredCoverage);
		for (KnowledgeElement linkedElement : linkedElements) {
			if (linkedElement.getType() == KnowledgeType.DECISION) {
				minimumCoverage--;
			}
			if (minimumCoverage <= 0) {
				checkResult.appendExplanation("This coverage or more is reached.");
				return checkResult;
			}
		}

		checkResult.setCriterionViolated(true);

		if (minimumCoverage < filterSettings.getDefinitionOfDone().getMinimumDecisionsWithinLinkDistance()) {
			checkResult.appendExplanation(createCoverageExplanation(minimumCoverage));
		} else {
			checkResult.appendExplanation("No decisions are reached.");
		}

		return checkResult;
	}

	public static String createCoverageExplanation(int coverage) {
		return "Only " + coverage + " decision" + (coverage > 1 ? "s are" : " is") + " reached.";
	}
}
