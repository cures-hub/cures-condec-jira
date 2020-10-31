package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Identifies Jira issue keys within commit messages.
 */
public interface CommitMessageParser {

	/**
	 * @param commitMessage
	 *            a commit message that should contain a Jira issue key.
	 * @return extracted Jira issue key from the commit message. Empty String if no
	 *         Jira issue key could be found.
	 *
	 * @issue How to identify the Jira issue key(s) in a commit message?
	 * @alternative This is a very simple method to detect the Jira issue key as the
	 *              first word in the message and should be improved!
	 */
	public static String getJiraIssueKey(String commitMessage) {
		if (commitMessage.isEmpty()) {
			return "";
		}
		Set<String> keys = getJiraIssueKeys(commitMessage);
		return keys.isEmpty() ? "" : keys.iterator().next();
	}

	/**
	 * @param commitMessage
	 *            that might contain a Jira issue key, e.g., a commit message,
	 *            branch name, or pull request title.
	 * @return set of all mentioned Jira issue keys in upper case letters (is
	 *         ordered by their appearance in the message).
	 */
	public static Set<String> getJiraIssueKeys(String commitMessage) {
		Set<String> keys = new LinkedHashSet<String>();
		Pattern pattern = Pattern.compile("((?<!([A-Z]{1,10})-?)[A-Z]+-\\d+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(commitMessage);
		while (matcher.find()) {
			keys.add(matcher.group(1).toUpperCase(Locale.ENGLISH));
		}
		return keys;
	}
}
