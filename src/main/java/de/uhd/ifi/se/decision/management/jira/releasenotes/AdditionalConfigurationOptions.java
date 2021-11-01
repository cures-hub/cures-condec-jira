package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.List;

/**
 * Additional configuration options for release notes creation.
 */
public enum AdditionalConfigurationOptions {
	INCLUDE_BREAKING_CHANGES, INCLUDE_BUG_FIXES, INCLUDE_DECISION_KNOWLEDGE, INCLUDE_EXTRA_LINK, INCLUDE_TEST_INSTRUCTIONS, INCLUDE_UPGRADE_GUIDE;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public static String getMarkdownOptionsString(String configurationOptionAsString) {
		if (configurationOptionAsString == null) {
			return "";
		}
		switch (configurationOptionAsString.toLowerCase()) {
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
	 * @return list of additional configuration options as strings in upper case.
	 */
	public static List<String> toList() {
		List<String> configurationTypes = new ArrayList<>();
		for (AdditionalConfigurationOptions type : AdditionalConfigurationOptions.values()) {
			configurationTypes.add(type.name());
		}
		return configurationTypes;
	}
}