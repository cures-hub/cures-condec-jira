package de.uhd.ifi.se.decision.management.jira.model;

/**
 * The {@link Origin} indicates the source of a knowledge element and might be
 * different from the current {@link DocumentationLocation}.
 * 
 * Commit messages are transcribed into Jira issue comments. Thus, their
 * documentation location is {@link DocumentationLocation#JIRAISSUETEXT} but
 * their origin is {@link Origin#COMMIT}.
 */
public enum Origin {
	COMMIT, DOCUMENTATION_LOCATION

}
