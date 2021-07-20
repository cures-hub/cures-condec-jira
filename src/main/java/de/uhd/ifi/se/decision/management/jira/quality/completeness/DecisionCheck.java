package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class DecisionCheck implements KnowledgeElementCheck {

	private KnowledgeElement decision;

	@Override
	public boolean execute(KnowledgeElement decision) {
		this.decision = decision;
		String projectKey = decision.getProject().getProjectKey();
		DefinitionOfDone definitionOfDone = ConfigPersistenceManager.getDefinitionOfDone(projectKey);
		return isCompleteAccordingToDefault() && isCompleteAccordingToSettings(definitionOfDone);
	}

	@Override
	public boolean isCompleteAccordingToDefault() {
		return hasIssue();
	}

	@Override
	public boolean isCompleteAccordingToSettings(DefinitionOfDone definitionOfDone) {
		return hasPro(definitionOfDone);
	}

	@Override
	public List<QualityProblem> getQualityProblems(KnowledgeElement decision, DefinitionOfDone definitionOfDone) {
		this.decision = decision;

		List<QualityProblem> qualityProblems = new ArrayList<>();

		if (!hasIssue()) {
			qualityProblems.add(QualityProblem.DECISION_DOESNT_HAVE_ISSUE);
		}

		if (!hasPro(definitionOfDone)) {
			qualityProblems.add(QualityProblem.DECISION_DOESNT_HAVE_PRO);
		}

		return qualityProblems;
	}

	private boolean hasIssue() {
		return decision.hasNeighborOfType(KnowledgeType.ISSUE);
	}

	private boolean hasPro(DefinitionOfDone definitionOfDone) {
		boolean hasToBeLinkedToProArgument = definitionOfDone.isDecisionIsLinkedToPro();
		if (hasToBeLinkedToProArgument) {
			return decision.hasNeighborOfType(KnowledgeType.PRO);
		}
		return true;
	}

}
