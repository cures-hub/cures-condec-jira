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
	 * @param status as a String.
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
	 * @return list of all knowledge status as Strings in lower case.
	 */
	public static List<String> toStringList() {
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
	 * @param type {@link KnowledgeType} object.
	 * @return default status that is set when a new decision knowledge element is
	 * created. The default status for alternatives is "idea", for decision
	 * is "decided", and for issues is "unresolved".
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
	 * @param formerElement {@link KnowledgeElement} before the change.
	 * @param newElement    {@link KnowledgeElement} after it was updated.
	 * @return new status after the change of the {@link KnowledgeType}.
	 */
	public static KnowledgeStatus getNewKnowledgeStatusForType(KnowledgeElement formerElement,
															   KnowledgeElement newElement) {
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
	 * @param newElement {@link KnowledgeElement} after it was updated.
	 * @return new {@link KnowledgeType} after the change of the
	 * {@link KnowledgeStatus}.
	 */
	public static KnowledgeType getNewKnowledgeTypeForStatus(KnowledgeElement newElement) {
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

	public static boolean isIssueResolved(KnowledgeElement parentElement, KnowledgeElement childElement) {
		return parentElement.getType() == KnowledgeType.ISSUE && childElement.getType() == KnowledgeType.DECISION
				&& childElement.getStatus() == KnowledgeStatus.DECIDED;
	}

	/**
	 * @return list of all valid knowledge status.
	 */
	public static List<KnowledgeStatus> getAllKnowledgeStatus() {
		List<KnowledgeStatus> statuses = new ArrayList<KnowledgeStatus>();
		for (KnowledgeStatus status : values()) {
			statuses.add(status);
		}
		return statuses;
	}

	public String getColor() {
		switch (this) {
			case UNRESOLVED:
				return "red";
			case DISCARDED:
				return "gray";
			case REJECTED:
				return "gray";
			default:
				return "";
		}
	}
}
