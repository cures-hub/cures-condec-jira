package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.Set;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class CodeCompletenessCheck implements CompletenessCheck {

	private KnowledgeElement codeFile;

    @Override
	public boolean execute(KnowledgeElement codeFile) {
		this.codeFile = codeFile;
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		if (codeFile.getLineCount() < 50) {
			return true;
		}
        if (codeFile.getDescription().toLowerCase().startsWith("test")) {
            return true;
        }
		Set<KnowledgeElement> linkedElements = codeFile.getLinkedElements(3);
		for (KnowledgeElement linkedElement : linkedElements) {
			if (linkedElement.getType() == KnowledgeType.ISSUE) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		return true;
	}

}
