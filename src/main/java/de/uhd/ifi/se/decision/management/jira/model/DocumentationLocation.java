package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Locale;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;

/**
 * Possible documentation locations of decision knowledge
 */
public enum DocumentationLocation {
	JIRAISSUE, ACTIVEOBJECT, JIRAISSUECOMMENT, COMMIT, PULLREQUEST, UNKNOWN;

	/**
	 * Convert a string to a documentation type.
	 *
	 * @param type
	 *            as a String.
	 */
	public static DocumentationLocation getDocumentationType(String type) {
		switch (type.toLowerCase(Locale.ENGLISH)) {
		case "i":
			return DocumentationLocation.JIRAISSUE;
		case "a":
			return DocumentationLocation.ACTIVEOBJECT;
		case "s":
			return DocumentationLocation.JIRAISSUECOMMENT;
		case "c":
			return DocumentationLocation.COMMIT;
		case "p":
			return DocumentationLocation.PULLREQUEST;
		default:
			return DocumentationLocation.UNKNOWN;
		}
	}

	public static String getIdentifier(DocumentationLocation documentationLocation) {
		switch (documentationLocation) {
		case JIRAISSUE:
			return "i";
		case JIRAISSUECOMMENT:
			return "s";
		case ACTIVEOBJECT:
			return "a";
		default:
			return "";
		}
	}

	public static String getIdentifier(DecisionKnowledgeElement element) {
		if (element instanceof Sentence) {
			return "s";
		}
		if (element.getDocumentationLocation() == null) {
			return "";
		}
		if (element.getDocumentationLocation().equals(DocumentationLocation.ACTIVEOBJECT)) {
			return "a";
		} else if (element.getDocumentationLocation().equals(DocumentationLocation.JIRAISSUE)) {
			return "i";
		} else {
			return "";
		}

	}
	
	@Override
	public String toString() {
		return this.name().substring(0, 1).toUpperCase(Locale.ENGLISH)
				+ this.name().substring(1).toLowerCase(Locale.ENGLISH);
	}
}
