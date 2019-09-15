package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;

/**
 * Type of Category for Release Notes
 */
public enum ReleaseNoteCategory {
	BUG_FIXES, NEW_FEATURES, IMPROVEMENTS;


	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}


	public static ReleaseNoteCategory getTargetGroup(String type) {
		if (type == null) {
			return ReleaseNoteCategory.BUG_FIXES;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
			case "bug_fixes":
				return ReleaseNoteCategory.BUG_FIXES;
			case "new_features":
				return ReleaseNoteCategory.NEW_FEATURES;
			case "improvements":
				return ReleaseNoteCategory.IMPROVEMENTS;
			default:
				return ReleaseNoteCategory.BUG_FIXES;
		}
	}
	public static String getTargetGroupReadable(ReleaseNoteCategory type) {
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
	 * Convert all Categories to strings.
	 *
	 * @return list of Categories  as Strings.
	 */
	public static List<String> toList() {
		List<String> releaseNoteCategoryTypes = new ArrayList<String>();
		for (ReleaseNoteCategory releaseNoteCategory : ReleaseNoteCategory.values()) {
			releaseNoteCategoryTypes.add(releaseNoteCategory.toString());
		}
		return releaseNoteCategoryTypes;
	}

	/**
	 * Creates EnumMap with Categories and boolean
	 *
	 * @return EnumMap with Categories and false
	 */
	public static EnumMap<ReleaseNoteCategory, Boolean> toBooleanMap() {
		EnumMap<ReleaseNoteCategory, Boolean> releaseNoteCategoryTypes = new EnumMap<ReleaseNoteCategory, Boolean>(ReleaseNoteCategory.class);
		for (ReleaseNoteCategory releaseNoteCategory : ReleaseNoteCategory.values()) {
			releaseNoteCategoryTypes.put(releaseNoteCategory, false);
		}
		return releaseNoteCategoryTypes;
	}
	/**
	 *
	 * @return list of Categories .
	 */
	public static List<ReleaseNoteCategory> toOriginalList() {
		List<ReleaseNoteCategory> releaseNoteCategoryTypes = new ArrayList<ReleaseNoteCategory>();
		for (ReleaseNoteCategory releaseNoteCategory : ReleaseNoteCategory.values()) {
			releaseNoteCategoryTypes.add(releaseNoteCategory);
		}
		return releaseNoteCategoryTypes;
	}

}
