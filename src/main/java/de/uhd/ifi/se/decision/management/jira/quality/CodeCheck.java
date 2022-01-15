package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Checks whether a code file fulfills the {@link DefinitionOfDone}.
 */
public class CodeCheck extends KnowledgeElementCheck {

	public CodeCheck(KnowledgeElement elementToBeChecked) {
		super(elementToBeChecked);
	}

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(DefinitionOfDone definitionOfDone) {
		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();
		return qualityCheckResults;
	}

	@Override
	public QualityCriterionCheckResult getCoverageQuality(FilterSettings filterSettings) {
		QualityCriterionCheckResult checkResult = new QualityCriterionCheckResult(
				QualityCriterionType.DECISION_COVERAGE, false);
		if (element instanceof ChangedFile) {
			int lineNumbersInCodeFile = filterSettings.getDefinitionOfDone().getLineNumbersInCodeFile();
			ChangedFile codeFile = (ChangedFile) element;
			if (codeFile.getLineCount() < lineNumbersInCodeFile) {
				checkResult
						.setExplanation("This code file is excluded from coverage measuring because it is too small.");
				return checkResult;
			}
			if (codeFile.isTestCodeFile()) {
				checkResult.setExplanation(
						"This code file is excluded from coverage measuring because it is a test file.");
				return checkResult;
			}
		}
		return super.getCoverageQuality(filterSettings);
	}
}
