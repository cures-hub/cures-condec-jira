package de.uhd.ifi.se.decision.management.jira.rationale.backlog;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class RationaleBacklog {

	public static boolean isElementComplete(KnowledgeElement parentElement, KnowledgeElement childElement) {
			switch (parentElement.getType()) {
				case DECISION:
					return isDecisionComplete( childElement);
				default:
					throw new IllegalStateException("Unexpected value: " + parentElement.getType());
			}
	}

	private static boolean isDecisionComplete(KnowledgeElement linkedElement) {
		return linkedElement.getType() == KnowledgeType.ISSUE;
	}

}
