package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @description Type of decision knowledge element
 */
public enum KnowledgeType {
	ALTERNATIVE, ASSUMPTION, ASSESSMENT, ARGUMENT, CLAIM, CONTEXT, CONSTRAINT, DECISION, GOAL, ISSUE, IMPLICATION, PROBLEM, RATIONALE, SOLUTION, OTHER, QUESTION;

	public static KnowledgeType getKnowledgeType(String type) {
		if(type == null){
			return KnowledgeType.OTHER;
		}
		switch (type.toLowerCase()) {
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

	public static KnowledgeType getSuperType(KnowledgeType type) {
		if(type == null){
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

	public KnowledgeType getSuperType() {
		return getSuperType(this);
	}

	public String toString() {
		return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
	}

	public static List<String> toList() {
		List<String> knowledgeTypes = new ArrayList<String>();
		for (KnowledgeType knowledgeType : KnowledgeType.values()) {
			knowledgeTypes.add(knowledgeType.toString());
		}
		return knowledgeTypes;
	}
}
