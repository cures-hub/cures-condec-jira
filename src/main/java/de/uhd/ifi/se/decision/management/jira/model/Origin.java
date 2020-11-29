package de.uhd.ifi.se.decision.management.jira.model;

import com.atlassian.jira.issue.comments.Comment;

/**
 * The {@link Origin} indicates the source of a knowledge element and might be
 * different from the current {@link DocumentationLocation}.
 * 
 * Commit messages are transcribed into Jira issue comments. Thus, their
 * documentation location is {@link DocumentationLocation#JIRAISSUETEXT} but
 * their origin is {@link Origin#COMMIT}.
 */
public enum Origin {
	COMMIT, DOCUMENTATION_LOCATION;

	public static Origin determineOrigin(Comment comment) {
		if (comment == null) {
			return DOCUMENTATION_LOCATION;
		}
		return determineOrigin(comment.getBody());
	}

	public static Origin determineOrigin(String commentBody) {
		if (commentBody != null && commentBody.contains("Hash:")) {
			return COMMIT;
		}
		return DOCUMENTATION_LOCATION;
	}

}
