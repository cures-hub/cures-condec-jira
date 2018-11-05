package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;

/**
 * Type of decision knowledge element
 */
public enum KnowledgeType {
	ALTERNATIVE, ASSUMPTION, ASSESSMENT, ARGUMENT, CLAIM, CONTEXT, CONSTRAINT, DECISION, GOAL, ISSUE, IMPLICATION, PROBLEM, RATIONALE, SOLUTION, OTHER, QUESTION;

	/**
	 * Get the minimal set of decision knowledge types for the management of
	 * decision knowledge (decision, issue, argument, alternative).
	 *
	 * @return minimal set of decision knowledge types.
	 */
	public static Set<KnowledgeType> getDefaulTypes() {
		return EnumSet.of(DECISION, ISSUE, ARGUMENT, ALTERNATIVE);
	}

	/**
	 * Convert a string to a knowledge type.
	 *
	 * @param type
	 *            as a String.
	 */
	public static KnowledgeType getKnowledgeType(String type) {
		if (type == null) {
			return KnowledgeType.OTHER;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
		case "decision":
			return KnowledgeType.DECISION;
		case "constraint":
			return KnowledgeType.CONSTRAINT;
		case "assumption":
			return KnowledgeType.ASSUMPTION;
		case "implication":
			return KnowledgeType.IMPLICATION;
		case "context":
			return KnowledgeType.CONTEXT;
		case "problem":
			return KnowledgeType.PROBLEM;
		case "issue":
			return KnowledgeType.ISSUE;
		case "goal":
			return KnowledgeType.GOAL;
		case "solution":
			return KnowledgeType.SOLUTION;
		case "claim":
			return KnowledgeType.CLAIM;
		case "alternative":
			return KnowledgeType.ALTERNATIVE;
		case "rationale":
			return KnowledgeType.RATIONALE;
		case "question":
			return KnowledgeType.QUESTION;
		case "argument":
			return KnowledgeType.ARGUMENT;
		case "assessment":
			return KnowledgeType.ASSESSMENT;
		default:
			return KnowledgeType.OTHER;
		}
	}

	/**
	 * Get the super class of a knowledge type in the decision documentation model.
	 * For example, the super type of argument is rationale and the super type of
	 * issue is problem.
	 *
	 * @see DecisionKnowledgeElement
	 * @param type of knowledge
	 * @return super knowledge type of the decision knowledge element.
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
	 * @see DecisionKnowledgeElement
	 * @return super knowledge type of the decision knowledge element.
	 */
	public KnowledgeType getSuperType() {
		return getSuperType(this);
	}

	/**
	 * Convert the knowledge type to a String starting with a capital letter, e.g., Argument, Decision, or Alternative.
	 *
	 * @return knowledge type as a String starting with a capital letter.
	 */
	@Override
	public String toString() {
		return this.name().substring(0, 1).toUpperCase(Locale.ENGLISH)
				+ this.name().substring(1).toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Convert all knowledge types to a list of String.
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
	
	public static String getIconUrl(DecisionKnowledgeElement element) {
		if ( element instanceof Sentence){
			return "";
		}
		switch(element.getType()) {
		case OTHER:
			IssueManager issueManager = ComponentAccessor.getIssueManager();
			Issue issue = issueManager.getIssueByCurrentKey(element.getKey());
			return issue.getIssueType().getCompleteIconUrl();
		default:
			return ComponentGetter.getUrlOfImageFolder() + element.getType().toString() + ".png";
		}		
	}
	
	public static String getIconUrl(DecisionKnowledgeElement element, String linkType) {
		switch(element.getType()) {
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
