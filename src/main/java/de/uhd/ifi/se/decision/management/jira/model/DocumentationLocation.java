package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Locale;

import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;

/**
 * Possible documentation locations of decision knowledge
 */
public enum DocumentationLocation {
	JIRAISSUE, ACTIVEOBJECT, JIRAISSUETEXT, COMMIT, PULLREQUEST, UNKNOWN;

	/**
	 * Convert a string to a documentation type.
	 *
	 * @param identifier
	 *            as a String.
	 */
	public static DocumentationLocation getDocumentationLocationFromIdentifier(String identifier) {
		if (identifier == null) {
			return DocumentationLocation.UNKNOWN;
		}
		switch (identifier.toLowerCase(Locale.ENGLISH)) {
		case "i":
			return DocumentationLocation.JIRAISSUE;
		case "a":
			return DocumentationLocation.ACTIVEOBJECT;
		case "s":
			return DocumentationLocation.JIRAISSUETEXT;
		case "c":
			return DocumentationLocation.COMMIT;
		case "p":
			return DocumentationLocation.PULLREQUEST;
		case "":
			// TODO This should be the same as the default persistence strategy
			return DocumentationLocation.JIRAISSUE;
		default:
			return DocumentationLocation.UNKNOWN;
		}
	}

	public static String getIdentifier(DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return "";
		}
		switch (documentationLocation) {
		case JIRAISSUE:
			return "i";
		case JIRAISSUETEXT:
			return "s";
		case ACTIVEOBJECT:
			return "a";
		default:
			return "";
		}
	}

	public String getIdentifier() {
		return getIdentifier(this);
	}

	public static String getIdentifier(DecisionKnowledgeElement element) {
		if (element == null || element.getDocumentationLocation() == null) {
			return "";
		} else if (element instanceof PartOfJiraIssueText
				|| element.getDocumentationLocation().equals(DocumentationLocation.JIRAISSUETEXT)) {
			return "s";
		} else if (element.getDocumentationLocation().equals(DocumentationLocation.ACTIVEOBJECT)) {
			return "a";
		} else if (element.getDocumentationLocation().equals(DocumentationLocation.JIRAISSUE)) {
			return "i";
		} else {
			return "";
		}
	}
}
