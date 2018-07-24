package de.uhd.ifi.se.decision.management.jira.decXtract.model;

import java.util.ArrayList;
import java.util.List;

public enum Rationale {
	isRelevant, isIssue, isDecision, isAlternative, isPro, isCon;

	public static Rationale getRationale(String text) {
		if (text == null) {
			return null;
		}
		switch (text) {
		case "isRelevant":
			return Rationale.isRelevant;
		case "isIssue":
			return Rationale.isIssue;
		case "isDecision":
			return Rationale.isDecision;
		case "isAlternative":
			return Rationale.isAlternative;
		case "isPro":
			return Rationale.isPro;
		case "isCon":
			return Rationale.isCon;
		default:
			return null;
		}
	}

	public static String getString(Rationale text) {
		if (text == null) {
			return null;
		}
		switch (text) {
		case isRelevant:
			return "isRelevant";
		case isIssue:
			return "isIssue";
		case isDecision:
			return "isDecision";
		case isAlternative:
			return "isAlternative";
		case isPro:
			return "isPro";
		case isCon:
			return "isCon";
		default:
			return null;
		}
	}

	public static String getOpeningTag(String text) {
		switch (text) {
		case "isRelevant":
			//return "[isR]<b>";
			return "<span style=\"background-color: #F2F5A9\">";

		default:
			return "";
		}
	}

	public static String getClosingTag(String text) {
		switch (text) {
		case "isRelevant":
			//return "</b>[/isR]";
			return "</span>";

		default:
			return "";
		}
	}

	public static List<Rationale> transferRationaleList(double[] ds) {
		List<Rationale> rList = new ArrayList<Rationale>();

		for (int i = 0; i < ds.length; i++) {
			if (ds[i] == 1. && i == 0) {
				rList.add(isIssue);
			}
			if (ds[i] == 1. && i == 1) {
				rList.add(isDecision);
			}
			if (ds[i] == 1. && i == 2) {
				rList.add(isAlternative);
			}
			if (ds[i] == 1. && i == 3) {
				rList.add(isPro);
			}
			if (ds[i] == 1. && i == 4) {
				rList.add(isCon);
			}
		}
		return rList;
	}

	public static String getClosingTag(List<Rationale> classification) {
		String tags = "";
		for (Rationale classi : classification) {
			if(classi == isIssue) {
				tags += "[/isIssue]";
			}
			if (classi == isDecision) {
				tags += "[/isDecision]";
			}
			if (classi == isAlternative) {
				tags += "[/isAlternative]";
			}
			if (classi == isPro) {
				tags += "[/isPro]";
			}
			if (classi == isCon) {
				tags += "[/isCon]";
			}
		}
		return tags;
	}

	public static String getOpeningTag(List<Rationale> classification) {
		String tags = "";
		for (Rationale classi : classification) {
			if(classi == isIssue) {
				tags += "[isIssue]";
			}
			if (classi == isDecision) {
				tags += "[isDecision]";
			}
			if (classi == isAlternative) {
				tags += "[isAlternative]";
			}
			if (classi == isPro) {
				tags += "[isPro]";
			}
			if (classi == isCon) {
				tags += "[isCon]";
			}
		}
		return tags;
	}

}
