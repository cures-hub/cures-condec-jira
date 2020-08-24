package de.uhd.ifi.se.decision.management.jira.rationale.backlog;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

import java.util.HashMap;
import java.util.Map;

public class KnowledgeCompletion {
	private final Map<KnowledgeType, CompletionCheck> completionCheckMap;

	public KnowledgeCompletion() {
		completionCheckMap = new HashMap<>();
		addCommands();
	}

	public boolean isElementComplete(KnowledgeElement parentElement, KnowledgeElement childElement) {
		CompletionCheck completionCheck = completionCheckMap.get(parentElement.getType());
		return completionCheck != null && completionCheck.execute(childElement);
	}

	private void addCommands() {
		completionCheckMap.put(KnowledgeType.DECISION, new DecisionCompletionCheck());
		completionCheckMap.put(KnowledgeType.ISSUE, new IssueCompletionCheck());
		completionCheckMap.put(KnowledgeType.ALTERNATIVE, new AlternativeCompletionCheck());
	}

	public Map<KnowledgeType, CompletionCheck> getCompletionCheckMap() {
		return completionCheckMap;
	}
}

