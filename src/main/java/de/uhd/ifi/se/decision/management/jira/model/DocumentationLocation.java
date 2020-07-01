package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;

/**
 * Possible documentation locations of decision knowledge.
 */
public enum DocumentationLocation {
	JIRAISSUE("i", "JiraIssues"), // store as first-class entities with own Jira issue type
	JIRAISSUETEXT("s", "JiraIssueText"), // store in the body of existing Jira issues, e.g. work items or requirements
	COMMIT("c", "Commit"), // store in commit message (currently not used)
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
	 *            of the documentation location as a String, e.g. "i" for Jira
	 *            issue.
	 * @return {@link DocumentationLocation}.
	 */
	public static DocumentationLocation getDocumentationLocationFromIdentifier(String identifier) {
		if (identifier == null || identifier.isBlank()) {
			return UNKNOWN;
		}
		for (DocumentationLocation location : values()) {
			if (identifier.contains(location.identifier)) {
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

	public static String getIdentifier(KnowledgeElement element) {
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
	 * @return documentation location as a String starting with a capital letter,
	 *         e.g., PullRequest, JiraIssue, Commit.
	 */
	@Override
	public String toString() {
		return this.getName();
	}

	/**
	 * @return list of all valid documentation locations.
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
