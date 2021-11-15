package de.uhd.ifi.se.decision.management.jira.model;

import com.atlassian.jira.issue.comments.Comment;

/**
 * The {@link Origin} indicates the source of a knowledge element and might be
 * different from the current {@link DocumentationLocation}.
 * 
 * Commit messages are transcribed into Jira issue comments so that they can be
 * annotated and improved there. Thus, their documentation location is
 * {@link DocumentationLocation#JIRAISSUETEXT} but their origin is
 * {@link Origin#COMMIT}.
 */
public enum Origin {
	COMMIT, DOCUMENTATION_LOCATION;

	/**
	 * @param comment
	 *            Jira issue comment that a decision knowledge element is part of.
	 * @return {@link Origin#COMMIT} if the commit message was transcribed into the
	 *         Jira issue comment.
	 */
	public static Origin determineOrigin(Comment comment) {
		if (comment == null) {
			return DOCUMENTATION_LOCATION;
		}
		return determineOrigin(comment.getBody());
	}

	/**
	 * @param commentBody
	 *            of a Jira issue comment that a decision knowledge element is part
	 *            of.
	 * @return {@link Origin#COMMIT} if the commit message was transcribed into the
	 *         Jira issue comment.
	 */
	public static Origin determineOrigin(String commentBody) {
		if (commentBody != null && commentBody.contains("Hash:")) {
			return COMMIT;
		}
		return DOCUMENTATION_LOCATION;
	}

}
