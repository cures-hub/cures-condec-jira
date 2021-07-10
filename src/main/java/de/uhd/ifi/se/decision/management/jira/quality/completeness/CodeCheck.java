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
public class CodeCheck implements KnowledgeElementCheck {

	private ChangedFile codeFile;

	@Override
	public boolean execute(KnowledgeElement codeFile) {
		if (!(codeFile instanceof ChangedFile)) {
			return true;
		}
		this.codeFile = (ChangedFile) codeFile;
		String projectKey = codeFile.getProject().getProjectKey();
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(projectKey);
		return isCompleteAccordingToDefault() || isCompleteAccordingToSettings(definitionOfDone);
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return codeFile.isTestCodeFile();
	}

	@Override
	public boolean isCompleteAccordingToSettings(DefinitionOfDone definitionOfDone) {
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
	public List<QualityProblem> getQualityProblems(KnowledgeElement codeFile, DefinitionOfDone definitionOfDone) {
		return new ArrayList<>();
	}
}