package de.uhd.ifi.se.decision.management.jira.model;

import java.util.*;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;

/**
 * Type of decision knowledge element
 */
public enum KnowledgeType {
	ALTERNATIVE, ASSUMPTION, ASSESSMENT, ARGUMENT, PRO, CON, CLAIM, CONTEXT, CONSTRAINT, DECISION, GOAL, ISSUE,
	IMPLICATION, PROBLEM, RATIONALE, SOLUTION, OTHER, QUESTION;

	/**
	 * Get the minimal set of decision knowledge types for the management of
	 * decision knowledge (decision, issue, argument, alternative).
	 *
	 * @return minimal set of decision knowledge types.
	 */
	public static Set<KnowledgeType> getDefaultTypes() {
		return EnumSet.of(DECISION, ISSUE, ARGUMENT, ALTERNATIVE);
	}

	/**
	 * Converts a string to a knowledge type.
	 *
	 * @param type as a String.
	 * @return knowledge type.
	 */
	public static KnowledgeType getKnowledgeType(String type) {
		if (type == null || type.isEmpty()) {
			return KnowledgeType.OTHER;
		}
		for (KnowledgeType knowledgeType : KnowledgeType.values()) {
			if (knowledgeType.name().toLowerCase(Locale.ENGLISH).matches(type.toLowerCase(Locale.ENGLISH) + "(.)*")) {
				return knowledgeType;
			}
		}
		if (type.contains("Pro")) {
			return PRO;
		}
		if (type.contains("Con")) {
			return CON;
		}
		return OTHER;
	}

	/**
	 * Return the argument knowledge type instead of pro-argument or con-argument.
	 */
	public KnowledgeType replaceProAndConWithArgument() {
		return replaceProAndConWithArgument(this);
	}

	public static KnowledgeType replaceProAndConWithArgument(KnowledgeType type) {
		switch (type) {
			case PRO:
				return KnowledgeType.ARGUMENT;
			case CON:
				return KnowledgeType.ARGUMENT;
			default:
				return type;
		}
	}

	public static KnowledgeType replaceProAndConWithArgument(String type) {
		KnowledgeType knowledgeType = getKnowledgeType(type);
		return replaceProAndConWithArgument(knowledgeType);
	}

	/**
	 * Get the parent knowledge type of a knowledge type for link creation in the
	 * knowledge graph. For example, the parent type of argument is decision or
	 * alternative. The parent type of decision or alternative is issue.
	 *
	 * @param type of knowledge
	 * @return parent knowledge type of the knowledge type for link creation in the
	 * knowledge graph.
	 * @see KnowledgeElement
	 */
	public static List<KnowledgeType> getParentTypes(KnowledgeType type) {
		List<KnowledgeType> parentTypes = new ArrayList<KnowledgeType>();
		if (type == null) {
			return parentTypes;
		}
		if (type == ARGUMENT || type == PRO || type == CON) {
			parentTypes.add(ALTERNATIVE);
			parentTypes.add(DECISION);
		} else if (type == DECISION || type == ALTERNATIVE) {
			parentTypes.add(ISSUE);
		}
		return parentTypes;
	}

	/**
	 * Get the super class of a knowledge type in the decision documentation model.
	 * For example, the super type of argument is rationale and the super type of
	 * issue is problem.
	 *
	 * @param type of knowledge
	 * @return super knowledge type of the knowledge type in the decision
	 * documentation model.
	 * @see KnowledgeElement
	 */
	public static KnowledgeType getSuperType(KnowledgeType type) {
		if (type == null) {
			return null;
		}
		switch (type) {
			case ISSUE:
				return KnowledgeType.PROBLEM;
			case GOAL:
				return KnowledgeType.PROBLEM;
			case ALTERNATIVE:
				return KnowledgeType.SOLUTION;
			case CLAIM:
				return KnowledgeType.SOLUTION;
			case CONSTRAINT:
				return KnowledgeType.CONTEXT;
			case ASSUMPTION:
				return KnowledgeType.CONTEXT;
			case IMPLICATION:
				return KnowledgeType.CONTEXT;
			case ARGUMENT:
				return KnowledgeType.RATIONALE;
			case ASSESSMENT:
				return KnowledgeType.RATIONALE;
			default:
				return type;
		}
	}

	/**
	 * Get the super class of a knowledge type in the decision documentation model.
	 * For example, the super type of argument is rationale and the super type of
	 * issue is problem.
	 *
	 * @return super knowledge type of the decision knowledge element.
	 * @see KnowledgeElement
	 */
	public KnowledgeType getSuperType() {
		return getSuperType(this);
	}

	/**
	 * Convert the knowledge type to a String starting with a capital letter, e.g.,
	 * Argument, Decision, or Alternative.
	 *
	 * @return knowledge type as a String starting with a capital letter.
	 */
	@Override
	public String toString() {
		return this.name().substring(0, 1).toUpperCase(Locale.ENGLISH)
				+ this.name().substring(1).toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Converts all knowledge types to a list of String.
	 *
	 * @return list of knowledge types as Strings starting with a capital letter.
	 */
	public static List<String> toList() {
		List<String> knowledgeTypes = new ArrayList<String>();
		for (KnowledgeType knowledgeType : KnowledgeType.values()) {
			knowledgeTypes.add(knowledgeType.toString());
		}
		return knowledgeTypes;
	}

	public String getIconString() {
		switch (this) {
			case PRO:
				return "(y)";
			case CON:
				return "(n)";
			case DECISION:
				return "(/)";
			case ISSUE:
				return "(!)";
			case ALTERNATIVE:
				return "(?)";
			default:
				return "";
		}
	}

	public String getIconUrl() {
		switch (this) {
			case PRO:
				return ComponentGetter.getUrlOfImageFolder() + "argument_pro.png";
			case CON:
				return ComponentGetter.getUrlOfImageFolder() + "argument_con.png";
			default:
				return ComponentGetter.getUrlOfImageFolder() + this.name().toLowerCase(Locale.ENGLISH) + ".png";
		}
	}

	public static String getIconUrl(KnowledgeElement element) {
		if (element == null) {
			return "";
		}
		if (element instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) element).isRelevant()) {
			return ComponentGetter.getUrlOfImageFolder() + "other.png";
		}
		if (element.getType() == OTHER) {
			if (element instanceof PartOfJiraIssueText) {
				return ComponentGetter.getUrlOfImageFolder() + "other.png";
			}
			if (element.getKey().split("-")[0].equals(element.getProject().getProjectKey())
					&& !element.getKey().split("-")[1].matches("\\d+")) {
				return ComponentGetter.getUrlOfImageFolder() + "class.png";
			}
			IssueManager issueManager = ComponentAccessor.getIssueManager();
			Issue issue = issueManager.getIssueByCurrentKey(element.getKey());
			if (issue == null) {
				return ComponentGetter.getUrlOfImageFolder() + "other.png";
			}
			return issue.getIssueType().getCompleteIconUrl();
		}
		return element.getType().getIconUrl();
	}

	public static String getIconUrl(KnowledgeElement element, String linkType) {
		if (linkType == null) {
			return getIconUrl(element);
		}
		switch (element.getType()) {
			case ARGUMENT:
				if (linkType.equals("support")) {
					return ComponentGetter.getUrlOfImageFolder() + "argument_pro.png";
				} else if (linkType.equals("attack")) {
					return ComponentGetter.getUrlOfImageFolder() + "argument_con.png";
				}
			default:
				return getIconUrl(element);
		}
	}
}
