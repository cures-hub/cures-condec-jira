package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum KnowledgeStatus {
	IDEA, DISCARDED, DECIDED, REJECTED, RESOLVED, UNRESOLVED, UNDEFINED;

	public static KnowledgeStatus getKnowledgeStatus(String status) {
		if (status == null || status.isEmpty()) {
			return UNDEFINED;
		}
		for (KnowledgeStatus knowledgeStatus : KnowledgeStatus.values()) {
			if (knowledgeStatus.name().toLowerCase(Locale.ENGLISH).matches(status.toLowerCase(Locale.ENGLISH))) {
				return knowledgeStatus;
			}
		}
		return UNDEFINED;
	}

	public static List<String> toList() {
		List<String> knowledgeStatus = new ArrayList<>();
		for (KnowledgeStatus status : KnowledgeStatus.values()) {
			knowledgeStatus.add(status.toString());
		}
		return knowledgeStatus;
	}

	@Override
	public String toString() {
		return this.name().substring(0, 1).toUpperCase(Locale.ENGLISH)
				+ this.name().substring(1).toLowerCase(Locale.ENGLISH);
	}

	public static KnowledgeStatus getDefaultStatus(KnowledgeType type) {
		if (type == null) {
			return UNDEFINED;
		}
		switch (type) {
		case ISSUE:
			return UNRESOLVED;
		case DECISION:
			return DECIDED;
		case ALTERNATIVE:
			return IDEA;
		default:
			return UNDEFINED;
		}
	}

	public static KnowledgeStatus getNewKnowledgeStatusForType(DecisionKnowledgeElement formerElement,
			DecisionKnowledgeElement newElement) {
		return getNewKnowledgeStatusForType(formerElement.getType(), newElement.getType(), newElement.getStatus());
	}

	public static KnowledgeStatus getNewKnowledgeStatusForType(KnowledgeType formerType, KnowledgeType newType,
			KnowledgeStatus newStatus) {
		if (formerType == KnowledgeType.DECISION && newType == KnowledgeType.ALTERNATIVE) {
			return REJECTED;
		}
		if (formerType == KnowledgeType.ALTERNATIVE && newType == KnowledgeType.DECISION) {
			return DECIDED;
		}
		return newStatus;
	}

	public static KnowledgeType getNewKnowledgeTypeForStatus(DecisionKnowledgeElement newElement) {
		return getNewKnowledgeTypeForStatus(newElement.getStatus(), newElement.getType());
	}

	public static KnowledgeType getNewKnowledgeTypeForStatus(KnowledgeStatus newStatus, KnowledgeType formerType) {
		if (formerType == null) {
			return KnowledgeType.OTHER;
		}
		if (newStatus == null) {
			newStatus = getDefaultStatus(formerType);
		}
		if (formerType == KnowledgeType.DECISION && (newStatus == IDEA || newStatus == DISCARDED)) {
			return KnowledgeType.ALTERNATIVE;
		}
		if (formerType == KnowledgeType.ALTERNATIVE && newStatus == DECIDED) {
			return KnowledgeType.DECISION;
		}
		return formerType;
	}

	public static boolean isIssueResolved(DecisionKnowledgeElement formerElement, DecisionKnowledgeElement newElement) {
		return formerElement.getType() == KnowledgeType.ISSUE && newElement.getType() == KnowledgeType.DECISION
				&& newElement.getStatus() == KnowledgeStatus.DECIDED;
	}

	/**
	 * Returns a list of all valid knowledge status.
	 *
	 * @return list of knowledge status.
	 */
	public static List<KnowledgeStatus> getAllKnowledgeStatus() {
		List<KnowledgeStatus> statuses = new ArrayList<KnowledgeStatus>();
		for (KnowledgeStatus status : values()) {
			statuses.add(status);
		}
		return statuses;
	}
}
