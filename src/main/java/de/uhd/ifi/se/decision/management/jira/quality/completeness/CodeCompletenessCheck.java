package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class CodeCompletenessCheck implements CompletenessCheck<ChangedFile> {

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
		if (codeFile.getDescription().toLowerCase().startsWith("test")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		int linkDistanceFromCodeFileToDecision = ConfigPersistenceManager.getDefinitionOfDone(projectKey)
				.getMaximumLinkDistanceToDecisions();
		int lineNumbersInCodeFile = ConfigPersistenceManager.getDefinitionOfDone(projectKey).getLineNumbersInCodeFile();

		if (codeFile.getLineCount() < lineNumbersInCodeFile) {
			return true;
		}
		Set<KnowledgeElement> linkedElements = codeFile.getLinkedElements(linkDistanceFromCodeFileToDecision);
		for (KnowledgeElement linkedElement : linkedElements) {
			if (linkedElement.getType() == KnowledgeType.DECISION) {
				return true;
			}
		}
		return false;
	}
}
