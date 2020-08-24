package de.uhd.ifi.se.decision.management.jira.rationale.backlog;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public interface CompletionCheck {
	public boolean execute(KnowledgeElement childElement);
}
