package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Status of decision problems (=issues) and solution options (=decisions and
 * alternatives). Issues can have the state "resolved" or "unresolved".
 */
public enum KnowledgeStatus {
	IDEA, DISCARDED, DECIDED, REJECTED, RESOLVED, UNRESOLVED, UNDEFINED;

	/**
	 * Converts a string to a knowledge status.
	 *
	 * @param status
	 *            as a String.
	 * @return knowledge status.
	 */
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

	/**
	 * Converts all knowledge status to a list of String.
	 *
	 * @return list of knowledge status as Strings in lower case.
	 */
	public static List<String> toList() {
		List<String> knowledgeStatus = new ArrayList<String>();
		for (KnowledgeStatus status : KnowledgeStatus.values()) {
			knowledgeStatus.add(status.toString());
		}
		return knowledgeStatus;
	}

	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Returns the default status that is set when a new decision knowledge element
	 * is created. The default status for alternatives is "idea", for decision is
	 * "decided", and for issues is "unresolved".
	 * 
	 * @param type
	 *            {@link KnowledgeType} object.
	 * @return default status that is set when a new decision knowledge element is
	 *         created, e.g. "idea" for alternative.
	 */
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

	/**
	 * If the knowledge type changes, the status might change as well. For example,
	 * when an alternative is picked as the decision, the status changes to
	 * "decided".
	 * 
	 * @param formerElement
	 *            {@link DecisionKnowledgeElement} before the change.
	 * @param newElement
	 *            {@link DecisionKnowledgeElement} after it was updated.
	 * @return new status after the change of the {@link KnowledgeType}.
	 */
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

	/**
	 * If the knowledge status changes, the knowledge type might change as well. For
	 * example, when the status of an alternative is changed to "decided", the
	 * alternative becomes a decision.
	 * 
	 * @param newElement
	 *            {@link DecisionKnowledgeElement} after it was updated.
	 * @return new {@link KnowledgeType} after the change of the
	 *         {@link KnowledgeStatus}.
	 */
	public static KnowledgeType getNewKnowledgeTypeForStatus(DecisionKnowledgeElement newElement) {
		return getNewKnowledgeTypeForStatus(newElement.getStatus(), newElement.getType());
	}

	public static KnowledgeType getNewKnowledgeTypeForStatus(KnowledgeStatus newStatus, KnowledgeType formerType) {
		if (formerType == null) {
			return KnowledgeType.OTHER;
		}
		if (formerType == KnowledgeType.DECISION && (newStatus == IDEA || newStatus == DISCARDED)) {
			return KnowledgeType.ALTERNATIVE;
		}
		if (formerType == KnowledgeType.ALTERNATIVE && newStatus == DECIDED) {
			return KnowledgeType.DECISION;
		}
		return formerType;
	}

	public static boolean isIssueResolved(DecisionKnowledgeElement parentElement, DecisionKnowledgeElement childElement) {
		return parentElement.getType() == KnowledgeType.ISSUE && childElement.getType() == KnowledgeType.DECISION
				&& childElement.getStatus() == KnowledgeStatus.DECIDED;
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
