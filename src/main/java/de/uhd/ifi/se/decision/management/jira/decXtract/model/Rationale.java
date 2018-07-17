package de.uhd.ifi.se.decision.management.jira.decXtract.model;

import net.java.ao.schema.Case;

public enum Rationale {
	isRelevant,isIssue,isDecision,isArgument,isPro,isCon;

	public static Rationale getRationale(String text) {
		if(text == null) {
			return null;
		}
		switch (text.toLowerCase()) {
		case "isRelevant":
			return Rationale.isRelevant;
		case "isIssue":
			return Rationale.isIssue;
		case "isDecision":
			return Rationale.isDecision;
		case "isPro":
			return Rationale.isPro;
		case "isCon":
			return Rationale.isCon;
		default:
			return null;
		}
	}

	public static String getOpeningTag(String text) {
		switch (text.toLowerCase()) {
		case "isrelevant":
			return "[isR]<b>";

		default:
			return "";
		}
	}
	public static String getClosingTag(String text) {
		switch (text.toLowerCase()) {
		case "isrelevant":
			return "</b>[/isR]";

		default:
			return "";
		}
	}




}
