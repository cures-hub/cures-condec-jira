package de.uhd.ifi.se.decision.management.jira.view;

import java.util.List;

public interface ToolTip {

	static String buildToolTip(List<String> qualityProblems) {
		String text = "";
		for (String problem : qualityProblems) {
			if (problem.equalsIgnoreCase("doesNotHaveMinimumCoverage")) {
				text += "Minimum decision coverage is not reached." + System.lineSeparator() + System.lineSeparator();
			} else if (problem.equalsIgnoreCase("hasIncompleteKnowledgeLinked")) {
				text += "Linked decision knowledge is incomplete." + System.lineSeparator() + System.lineSeparator();
			} else {
				text = text.concat(String.join(System.lineSeparator(), problem));
			}
		}
		text = text.strip();
		return text;
	}
}