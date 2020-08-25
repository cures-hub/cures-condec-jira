package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static java.util.Map.entry;

import java.util.Map;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

// TODO Add Java Doc and avoid using maps because they are hard to understand
public class KnowledgeCompletion {
	private static final Map<KnowledgeType, CompletionCheck> completionCheckMap = Map.ofEntries(
			entry(KnowledgeType.DECISION, new DecisionCompletionCheck()),
			entry(KnowledgeType.ISSUE, new IssueCompletionCheck()),
			entry(KnowledgeType.ALTERNATIVE, new AlternativeCompletionCheck()));

	public static boolean isElementComplete(KnowledgeElement element) {
		CompletionCheck completionCheck = completionCheckMap.get(element.getType());
		return completionCheck != null && completionCheck.execute(element);
	}

	public Map<KnowledgeType, CompletionCheck> getCompletionCheckMap() {
		return completionCheckMap;
	}
}
