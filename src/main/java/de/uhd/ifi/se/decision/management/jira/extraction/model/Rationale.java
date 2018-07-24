package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jhlabs.image.Colormap;

public enum Rationale {
	isRelevant, isIssue, isDecision, isAlternative, isPro, isCon;


	@SuppressWarnings("serial")
	private static Map<String, String> rationaleColorMap = new HashMap<String, String>() {{
	    put("isIssue","#F2F5A9");
	    put("isAlternative","#f1ccf9");
	    put("isDecision","#c5f2f9");
	    put("isPro","#b9f7c0");
	    put("isCon","#ffdeb5");
	}};
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
			//return "<span style=\"background-color:"+rationaleColorMap.get("isIssue") +"\">";

		default:
			return "";
		}
	}

	public static String getClosingTag(String text) {
		switch (text) {
		case "isRelevant":
			//return "</b>[/isR]";
			//return "</span>";

		default:
			return "";
		}
	}

	public static List<Rationale> transferRationaleList(double[] ds) {
		List<Rationale> rList = new ArrayList<Rationale>();

		for (int i = 0; i < ds.length; i++) {
			if (ds[i] == 1. && i == 0) {
				rList.add(isAlternative);
			}
			if (ds[i] == 1. && i == 1) {
				rList.add(isPro);
			}
			if (ds[i] == 1. && i == 2) {
				rList.add(isCon);
			}
			if (ds[i] == 1. && i == 3) {
				rList.add(isDecision);
			}
			if (ds[i] == 1. && i == 4) {
				rList.add(isIssue);
			}
		}
		return rList;
	}

	public static String getClosingTag(List<Rationale> classification) {
		String tags = "";
		for (Rationale label : classification) {
			if(label == isIssue) {
				tags += "</span>"+"[/isIssue]";
			}
			if (label == isDecision) {
				tags += "</span>"+"[/isDecision]";
			}
			if (label == isAlternative) {
				tags += "</span>"+"[/isAlternative]";
			}
			if (label == isPro) {
				tags += "</span>"+"[/isPro]";
			}
			if (label == isCon) {

				tags += "</span>"+"[/isCon]";
			}
		}
		return tags;
	}

	public static String getOpeningTag(List<Rationale> classification) {
		String tags = "";
		for (Rationale label : classification) {
			if(label == isIssue) {
				tags += "[isIssue]"+"<span style=\"background-color:"+rationaleColorMap.get("isIssue") +"\">";
			}
			if (label == isDecision) {
				tags += "[isDecision]"+"<span style=\"background-color:"+rationaleColorMap.get("isDecision") +"\">";
			}
			if (label == isAlternative) {
				tags += "[isAlternative]"+"<span style=\"background-color:"+rationaleColorMap.get("isAlternative") +"\">";
			}
			if (label == isPro) {
				tags += "[isPro]"+"<span style=\"background-color:"+rationaleColorMap.get("isPro") +"\">";
			}
			if (label == isCon) {
				tags += "[isCon]"+"<span style=\"background-color:"+rationaleColorMap.get("isCon") +"\">";
			}
		}
		return tags;
	}

}
