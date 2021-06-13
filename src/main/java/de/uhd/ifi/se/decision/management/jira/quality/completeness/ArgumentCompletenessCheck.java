package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import java.util.ArrayList;
import java.util.List;

public class ArgumentCompletenessCheck implements CompletenessCheck<KnowledgeElement> {

	private final String ARGUMENTDOESNTHAVEDECISIONORALTERNATIVE = "Argument doesn't have a decision or an alternative!";

	private KnowledgeElement argument;

	@Override
	public boolean execute(KnowledgeElement argument) {
		this.argument = argument;
		return isCompleteAccordingToDefault();
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return argument.hasNeighborOfType(KnowledgeType.DECISION)
				|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE);
	}

	@Override
	public boolean isCompleteAccordingToSettings() {
		return true;
	}

	@Override
	public List<String> getFailedCriteria(KnowledgeElement argument) {
		List<String> failedCriteria = new ArrayList<>();

		if (!(argument.hasNeighborOfType(KnowledgeType.DECISION)
			|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE))) {
			failedCriteria.add(ARGUMENTDOESNTHAVEDECISIONORALTERNATIVE);
		}

		return failedCriteria;
	}

}
