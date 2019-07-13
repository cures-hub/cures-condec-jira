package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;

/**
 * Possible documentation locations of decision knowledge.
 */
public enum DocumentationLocation {
	JIRAISSUE("i", "JiraIssues"), // store as first-class entities with own JIRA issue type
	ACTIVEOBJECT("a", "ActiveObject"), // store as first-class entities but not as JIRA issues
	JIRAISSUETEXT("s", "JiraIssueText"), // store in the body of existing JIRA issues, e.g. work items or requirements
	COMMIT("c", "Commit"), // store in commit message
	PULLREQUEST("p", "PullRequest"), // store in pull requests (currently not used)
	UNKNOWN("", "Unknown");

	private String identifier;
	private String name;

	private DocumentationLocation(String identifier, String name) {
		this.identifier = identifier;
		this.name = name;
	}

	/**
	 * Converts an identifier to a documentation location objects.
	 *
	 * @param identifier
	 *            of the documentation location as a String, e.g. "i" for JIRA
	 *            issue.
	 */
	public static DocumentationLocation getDocumentationLocationFromIdentifier(String identifier) {
		if (identifier == null) {
			return UNKNOWN;
		}
		// TODO: This should be the default persistence location.
		if (identifier.isEmpty()) {
			return JIRAISSUE;
		}
		for (DocumentationLocation location : values()) {
			if (location.identifier == identifier) {
				return location;
			}
		}
		return UNKNOWN;
	}

	public static String getIdentifier(DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return UNKNOWN.identifier;
		}
		return documentationLocation.identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public static String getName(DocumentationLocation documentationLocation) {
		if (documentationLocation == null) {
			return UNKNOWN.name;
		}
		return documentationLocation.name;
	}

	public String getName() {
		return name;
	}

	public static String getIdentifier(DecisionKnowledgeElement element) {
		if (element == null || element.getDocumentationLocation() == null) {
			return UNKNOWN.identifier;
		}
		if (element instanceof PartOfJiraIssueText
				|| element.getDocumentationLocation().equals(DocumentationLocation.JIRAISSUETEXT)) {
			return JIRAISSUETEXT.identifier;
		}
		return element.getDocumentationLocation().identifier;
	}

	public static DocumentationLocation getDocumentationLocationFromString(String locationString) {
		if (locationString == null) {
			return UNKNOWN;
		}
		if ("JiraIssue".equalsIgnoreCase(locationString)) {
			return JIRAISSUE;
		}
		for (DocumentationLocation location : values()) {
			if (location.name.equalsIgnoreCase(locationString)) {
				return location;
			}
		}
		return UNKNOWN;
	}

	/**
	 * Convert the documentation locations to a String starting with a capital
	 * letter, e.g., Pullrequest, Jiraissue, Commit
	 * 
	 * @return documentation locations as a String starting with a capital letter.
	 */
	@Override
	public String toString() {
		return this.name().substring(0, 1).toUpperCase(Locale.ENGLISH)
				+ this.name().substring(1).toLowerCase(Locale.ENGLISH);
	}

	/**
	 * Converts all documentation locations to a list of Strings.
	 *
	 * @return list of documentation locations as Strings starting with a capital
	 *         letter.
	 */
	public static List<String> getNamesOfDocumentationLocations() {
		List<String> documentationLocations = new ArrayList<String>();
		for (DocumentationLocation documentationLocation : DocumentationLocation.values()) {
			documentationLocations.add(documentationLocation.toString());
		}
		return documentationLocations;
	}

	/**
	 * Returns a list of all valid documentation locations.
	 * 
	 * @return list of documentation locations.
	 */
	public static List<DocumentationLocation> getAllDocumentationLocations() {
		List<DocumentationLocation> locations = new ArrayList<DocumentationLocation>();
		for (DocumentationLocation location : values()) {
			if (location != UNKNOWN) {
				locations.add(location);
			}
		}
		return locations;
	}
}
