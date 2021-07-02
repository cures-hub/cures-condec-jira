package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.dashboard.RationaleCoverageDashboardItem;

/**
 * Checks whether a code file (i.e. a {@link ChangedFile} instance) fulfilles
 * the criteria of the {@link DefinitionOfDone} (DoD).
 * 
 * Criteria are 1) the line count (small files are not checked, i.e. always
 * fulfill the DoD), 2) whether the file is a test code file (test classes are
 * also not checked), and 3) whether enough decisions are linked within a
 * certain distance in the {@link KnowledgeGraph} (the decision coverage is
 * checked).
 * 
 * @see RationaleCoverageCalculator
 * @see RationaleCoverageDashboardItem
 */
public class CodeCheck implements KnowledgeElementCheck<ChangedFile> {

	private ChangedFile codeFile;
	private String projectKey;

	@Override
	public boolean execute(ChangedFile codeFile) {
		this.codeFile = codeFile;
		projectKey = codeFile.getProject().getProjectKey();
		return isCompleteAccordingToDefault() || isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return codeFile.isTestCodeFile();
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(projectKey);

		int lineNumbersInCodeFile = definitionOfDone.getLineNumbersInCodeFile();
		if (codeFile.getLineCount() < lineNumbersInCodeFile) {
			return true;
		}

		int linkDistanceFromCodeFileToDecision = definitionOfDone.getMaximumLinkDistanceToDecisions();
		int minimumDecisionCoverage = definitionOfDone.getMinimumDecisionsWithinLinkDistance();
		Set<KnowledgeElement> linkedElements = codeFile.getLinkedElements(linkDistanceFromCodeFileToDecision);
		for (KnowledgeElement linkedElement : linkedElements) {
			if (minimumDecisionCoverage == 0) {
				return true;
			}
			if (linkedElement.getType() == KnowledgeType.DECISION) {
				minimumDecisionCoverage--;
			}
		}
		return false;
	}

	@Override
	public List<QualityProblem> getFailedCriteria(ChangedFile knowledgeElement) {
		return new ArrayList<>();
	}
}