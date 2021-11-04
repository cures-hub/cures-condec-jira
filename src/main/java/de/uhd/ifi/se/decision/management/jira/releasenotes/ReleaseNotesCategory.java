package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.EnumMap;

/**
 * Category for Release Notes
 */
public enum ReleaseNotesCategory {
	BUG_FIXES, NEW_FEATURES, IMPROVEMENTS;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public static String getTargetGroupReadable(ReleaseNotesCategory type) {
		if (type == null) {
			return "";
		}
		switch (type) {
		case BUG_FIXES:
			return "Bug Fixes";
		case NEW_FEATURES:
			return "New Features";
		case IMPROVEMENTS:
			return "Improvements";
		default:
			return "";
		}
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