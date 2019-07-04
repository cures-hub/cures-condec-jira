package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
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
	 * @param identifier as a String.
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
			case COMMIT:
				return "c";
			case PULLREQUEST:
				return "p";
			case UNKNOWN:
				return "u";
			default:
				return "";
		}
	}

	public String getIdentifier() {
		return getIdentifier(this);
	}

	public static String getName(DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return "";
		}
		switch (documentationLocation) {
			case JIRAISSUE:
				return "JiraIssues";
			case JIRAISSUETEXT:
				return "JiraIssueText";
			case ACTIVEOBJECT:
				return "ActiveObject";
			case COMMIT:
				return "Commit";
			case PULLREQUEST:
				return "PullRequest";
			case UNKNOWN:
				return "Unknown";
			default:
				return "";
		}
	}

	public static String getIdentifier(DecisionKnowledgeElement element) {
		if (element == null || element.getDocumentationLocation() == null) {
			return "";
		} else if (element instanceof PartOfJiraIssueText || element.getDocumentationLocation().equals(DocumentationLocation.JIRAISSUETEXT)) {
			return "s";
		} else if (element.getDocumentationLocation().equals(DocumentationLocation.ACTIVEOBJECT)) {
			return "a";
		} else if (element.getDocumentationLocation().equals(DocumentationLocation.JIRAISSUE)) {
			return "i";
		} else {
			return "";
		}
	}

	public static DocumentationLocation getDocumentationLocationFromString(String locationString) {
		String lowCaseLocationString = locationString.toLowerCase();
		switch (lowCaseLocationString) {
			case "jiraissue":
				return JIRAISSUE;
			case "jiraissuetext":
				return JIRAISSUETEXT;
			case "activeobject":
				return ACTIVEOBJECT;
			case "commit":
				return COMMIT;
			case "pullrequest":
				return PULLREQUEST;
			default:
				return UNKNOWN;

		}
	}

	/**
	 * Convert the documentation locations to a String starting with a capital letter, e.g.,
	 * Pullrequest, Jiraissue, Commit
	 * @return documentation locations as a String starting with a capital letter.
	 */
	@Override
	public String toString() {
		return this.name().substring(0, 1).toUpperCase(Locale.ENGLISH)
				       + this.name().substring(1).toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Convert all documentation locations to a list of String.
	 *
	 * @return list of documentation locations as Strings starting with a capital letter.
	 */
	public static List<String> toList() {
		List<String> documentationLocationList = new ArrayList<String>();
		for (DocumentationLocation documentationLocation : DocumentationLocation.values()) {
			documentationLocationList.add(documentationLocation.toString());
		}
		return documentationLocationList;
	}

	public static List<DocumentationLocation> getAllDocumentationLocations(){
		List<DocumentationLocation> locations = new ArrayList<>();
		for(String location: DocumentationLocation.toList()){
			locations.add(DocumentationLocation.getDocumentationLocationFromString(location));
		}
		return locations;
	}
}
