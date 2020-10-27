package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Identifies Jira issue keys within commit messages.
 */
public interface CommitMessageParser {

	/**
	 * @param commitMessage
	 *            commit message that is parsed for Jira issue keys.
	 * @param projectKey
	 *            key of the Jira project that every Jira issue key starts with.
	 * @return list of all mentioned Jira issue keys in upper case letters (might
	 *         contain duplicates and is ordered by their appearance in the
	 *         message).
	 */
	public static Set<String> getJiraIssueKeys(String commitMessage, String projectKey) {
		Set<String> keys = new LinkedHashSet<String>();
		if (projectKey == null) {
			return keys;
		}
		String[] words = commitMessage.split("[\\s,:]+");
		String baseKey = projectKey.toUpperCase(Locale.ENGLISH);
		for (String word : words) {
			word = word.toUpperCase(Locale.ENGLISH);
			if (word.contains(baseKey + "-")) {
				keys.add(word);
			}
		}
		return keys;
	}

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
		String[] split = commitMessage.split("[\\s,:]+");
		return split[0].toUpperCase(Locale.ENGLISH);
	}
}
