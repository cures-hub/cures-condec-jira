package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.ArrayList;
import java.util.Collections;
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

	public static String getRelevantTag(Boolean isRelevant) {
		if (isRelevant) {
			return "<span class=\"isRelevant\">";
		} else {
			return "<span class=\"isNotRelevant\">";
		}
	}

	public static String getRelevantClosingTag() {
		return "</span> ";
	}

	public static List<Rationale> transferRationaleList(double[] ds) {
		List<Rationale> rList = new ArrayList<Rationale>();

		for (int i = 0; i < ds.length; i++) {
			if (ds[i] == 1. && i == 0) {
				rList.add(isAlternative);
				break;
			}
			if (ds[i] == 1. && i == 1) {
				rList.add(isPro);
				break;
			}
			if (ds[i] == 1. && i == 2) {
				rList.add(isCon);
				break;
			}
			if (ds[i] == 1. && i == 3) {
				rList.add(isDecision);
				break;
			}
			if (ds[i] == 1. && i == 4) {
				rList.add(isIssue);
				break;
			}
		}
		return rList;
	}

	public static String getClosingTag(List<Rationale> classification) {
		Collections.reverse(classification);// reverse list, so last opened tag will be first closed
		for (Rationale label : classification) {
			if (label == isIssue) {
				return  "<span class =tag>[/Issue]</span>";
			}
			if (label == isDecision) {
				return  "<span class =tag>[/Decision]</span>";
			}
			if (label == isAlternative) {
				return "<span class =tag>[/Alternative]</span>";
			}
			if (label == isPro) {
				return  "<span class =tag>[/Pro]</span>";
			}
			if (label == isCon) {
				return  "<span class =tag>[/Con]</span>";
			}
		}
		return "<span class =tag ></span>" ;
	}

	public static String getOpeningTag(List<Rationale> classification) {
		for (Rationale label : classification) {
			if (label == isIssue) {
				return "<span class =tag>[Issue]</span>" ;
			}
			if (label == isDecision) {
				return "<span class =tag>[Decision]</span>" ;
			}
			if (label == isAlternative) {
				return "<span class =tag>[Alternative]</span>" ;
			}
			if (label == isPro) {
				return "<span class =tag>[Pro]</span>" ;
			}
			if (label == isCon) {
				return "<span class =tag>[Con]</span>" ;
			}
		}
		return "<span class =tag ></span>" ;
	}

}
