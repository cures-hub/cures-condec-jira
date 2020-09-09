package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Maps Jira issue types to release notes categories.
 * 
 * @see ReleaseNotesCategory
 */
public class ReleaseNotesMapping {

	private List<String> jiraIssueTypesForImprovements;
	private List<String> jiraIssueTypesForBugFixes;
	private List<String> jiraIssueTypesForNewFeatures;

	public ReleaseNotesMapping(String projectKey) {
		jiraIssueTypesForImprovements = ConfigPersistenceManager.getReleaseNoteMapping(projectKey,
				ReleaseNotesCategory.IMPROVEMENTS);
		jiraIssueTypesForBugFixes = ConfigPersistenceManager.getReleaseNoteMapping(projectKey,
				ReleaseNotesCategory.BUG_FIXES);
		jiraIssueTypesForNewFeatures = ConfigPersistenceManager.getReleaseNoteMapping(projectKey,
				ReleaseNotesCategory.NEW_FEATURES);
	}

	public List<String> getJiraIssueTypesForImprovements() {
		return jiraIssueTypesForImprovements;
	}

	public List<String> getJiraIssueTypesForBugFixes() {
		return jiraIssueTypesForBugFixes;
	}

	public List<String> getJiraIssueTypesForNewFeatures() {
		return jiraIssueTypesForNewFeatures;
	}
}
