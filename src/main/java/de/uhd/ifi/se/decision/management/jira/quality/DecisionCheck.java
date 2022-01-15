package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionCheck extends KnowledgeElementCheck {

	private KnowledgeElement decision;

	@Override
	public List<QualityCriterionCheckResult> getQualityCheckResult(KnowledgeElement decision,
			DefinitionOfDone definitionOfDone) {
		this.decision = decision;

		List<QualityCriterionCheckResult> qualityCheckResults = new ArrayList<>();

		if (!hasDecisionProblem()) {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.DECISION_LINKED_TO_ISSUE, true));
		} else {
			qualityCheckResults
					.add(new QualityCriterionCheckResult(QualityCriterionType.DECISION_LINKED_TO_ISSUE, false));
		}

		if (decision.getStatus() == KnowledgeStatus.CHALLENGED) {
			qualityCheckResults.add(new QualityCriterionCheckResult(QualityCriterionType.DECISION_STATUS, true));
		} else {
			qualityCheckResults.add(new QualityCriterionCheckResult(QualityCriterionType.DECISION_STATUS, false));
		}

		if (definitionOfDone.isDecisionIsLinkedToPro()) {
			if (!hasPro()) {
				qualityCheckResults
						.add(new QualityCriterionCheckResult(QualityCriterionType.DECISION_LINKED_TO_PRO, true));
			} else {
				qualityCheckResults
						.add(new QualityCriterionCheckResult(QualityCriterionType.DECISION_LINKED_TO_PRO, false));
			}
		}

		return qualityCheckResults;
	}

	private boolean hasDecisionProblem() {
		return !decision.getLinkedDecisionProblems().isEmpty();
	}

	private boolean hasPro() {
		return decision.hasNeighborOfType(KnowledgeType.PRO);
	}

}
