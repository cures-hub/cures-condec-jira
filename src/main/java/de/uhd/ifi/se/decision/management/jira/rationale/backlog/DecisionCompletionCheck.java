package de.uhd.ifi.se.decision.management.jira.rationale.backlog;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class DecisionCompletionCheck implements CompletionCheck {

	@Override
	public boolean execute(KnowledgeElement childElement) {
		return childElement.getType() == KnowledgeType.ISSUE;
	}
}
