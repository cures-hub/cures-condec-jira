package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;

/**
 * Category for Release Notes
 */
public enum ReleaseNotesCategory {
	BUG_FIXES, NEW_FEATURES, IMPROVEMENTS;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public static ReleaseNotesCategory getTargetGroup(String type) {
		if (type == null) {
			return ReleaseNotesCategory.BUG_FIXES;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
		case "bug_fixes":
			return ReleaseNotesCategory.BUG_FIXES;
		case "new_features":
			return ReleaseNotesCategory.NEW_FEATURES;
		case "improvements":
			return ReleaseNotesCategory.IMPROVEMENTS;
		default:
			return ReleaseNotesCategory.BUG_FIXES;
		}
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
	 * @return list of Categories as Strings.
	 */
	public static List<String> toList() {
		List<String> releaseNoteCategoryTypes = new ArrayList<>();
		for (ReleaseNotesCategory releaseNoteCategory : ReleaseNotesCategory.values()) {
			releaseNoteCategoryTypes.add(releaseNoteCategory.toString());
		}
		return releaseNoteCategoryTypes;
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

	/**
	 * @return list of categories.
	 */
	public static List<ReleaseNotesCategory> toOriginalList() {
		List<ReleaseNotesCategory> releaseNoteCategoryTypes = new ArrayList<>();
		for (ReleaseNotesCategory releaseNoteCategory : ReleaseNotesCategory.values()) {
			releaseNoteCategoryTypes.add(releaseNoteCategory);
		}
		return releaseNoteCategoryTypes;
	}

}
