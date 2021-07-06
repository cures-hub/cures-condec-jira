package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

import java.util.ArrayList;
import java.util.List;

public class ArgumentCheck implements KnowledgeElementCheck<KnowledgeElement> {

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
	public boolean isCompleteAccordingToSettings(DefinitionOfDone definitionOfDone) {
		return true;
	}

	@Override
	public List<QualityProblem> getQualityProblems(KnowledgeElement argument, DefinitionOfDone definitionOfDone) {
		List<QualityProblem> qualityProblems = new ArrayList<>();

		if (!(argument.hasNeighborOfType(KnowledgeType.DECISION)
			|| argument.hasNeighborOfType(KnowledgeType.ALTERNATIVE))) {
			qualityProblems.add(QualityProblem.ARGUMENTDOESNTHAVEDECISIONORALTERNATIVE);
		}

		return qualityProblems;
	}

}
