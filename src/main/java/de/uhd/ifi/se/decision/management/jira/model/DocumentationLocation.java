package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Locale;

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
}
