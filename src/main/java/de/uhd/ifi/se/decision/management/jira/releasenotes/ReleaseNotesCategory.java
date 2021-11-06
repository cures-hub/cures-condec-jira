package de.uhd.ifi.se.decision.management.jira.releasenotes;

/**
 * Category for {@link ReleaseNotesEntry release notes entries}. The Jira issues
 * are classified either as new feature, improvement, or bug fix.
 */
public enum ReleaseNotesCategory {
	BUG_FIXES("Bug Fixes"), //
	NEW_FEATURES("New Features"), //
	IMPROVEMENTS("Improvements");

	private String name;

	private ReleaseNotesCategory(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public String getName() {
		return name;
	}
}