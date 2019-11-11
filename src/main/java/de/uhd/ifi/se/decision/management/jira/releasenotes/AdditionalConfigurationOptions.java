package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Type of additional configuration options for release notes
 */
public enum AdditionalConfigurationOptions {
	INCLUDE_BREAKING_CHANGES, INCLUDE_BUG_FIXES, INCLUDE_DECISION_KNOWLEDGE, INCLUDE_EXTRA_LINK, INCLUDE_TEST_INSTRUCTIONS, INCLUDE_UPGRADE_GUIDE;

	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.ENGLISH);
	}

	public String toUpperString() {
		return this.name();
	}

	public static String getMarkdownOptionsString(String type) {
		if (type == null) {
			return "";
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
		case "include_breaking_changes":
			return "### Breaking Changes\n Add your breaking changes here\n";
		case "include_extra_link":
			return "### More Documentation\n [Add your link here](https://www.google.com)\n";
		case "include_test_instructions":
			return "### Test Instructions\n Add your test instructions here\n";
		case "include_upgrade_guide":
			return "### Installation/ Upgrade Guide\n Add your installation/ Upgrade guide here\n";
		default:
			return "";
		}
	}

	/**
	 * Converts all additional configuration options to a list of String.
	 *
	 * @return list additional configuration options as strings in upper case.
	 */
	public static List<String> toList() {
		ArrayList<String> configurationTypes = new ArrayList<String>();
		for (AdditionalConfigurationOptions type : AdditionalConfigurationOptions.values()) {
			configurationTypes.add(type.name());
		}
		return configurationTypes;
	}

}
