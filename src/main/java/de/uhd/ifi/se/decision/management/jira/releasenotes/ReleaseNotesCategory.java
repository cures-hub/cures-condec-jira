package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.EnumMap;

/**
 * Category for Release Notes
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

	/**
	 * @return EnumMap with Categories and false
	 */
	public static EnumMap<ReleaseNotesCategory, Boolean> toBooleanMap() {
		EnumMap<ReleaseNotesCategory, Boolean> releaseNoteCategoryTypes = new EnumMap<>(ReleaseNotesCategory.class);
		for (ReleaseNotesCategory releaseNoteCategory : ReleaseNotesCategory.values()) {
			releaseNoteCategoryTypes.put(releaseNoteCategory, false);
		}
		return releaseNoteCategoryTypes;
	}
}