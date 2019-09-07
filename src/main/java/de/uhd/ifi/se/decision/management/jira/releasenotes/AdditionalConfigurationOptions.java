package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Locale;

/**
 * Type of Additional Configuration Options for Release Notes
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


	public static AdditionalConfigurationOptions getAdditionalConfigurationOptions(String type) {
		if (type == null) {
			return AdditionalConfigurationOptions.INCLUDE_DECISION_KNOWLEDGE;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
			case "include_decision_knowledge":
				return AdditionalConfigurationOptions.INCLUDE_DECISION_KNOWLEDGE;
			case "include_bug_fixes":
				return AdditionalConfigurationOptions.INCLUDE_BUG_FIXES;
			case "include_breaking_changes":
				return AdditionalConfigurationOptions.INCLUDE_BREAKING_CHANGES;
			case "include_extra_link":
				return AdditionalConfigurationOptions.INCLUDE_EXTRA_LINK;
			case "include_test_instructions":
				return AdditionalConfigurationOptions.INCLUDE_TEST_INSTRUCTIONS;
			case "include_upgrade_guide":
				return AdditionalConfigurationOptions.INCLUDE_UPGRADE_GUIDE;
			default:
				return AdditionalConfigurationOptions.INCLUDE_DECISION_KNOWLEDGE;
		}
	}

	public static String getMarkdownOptionsString(String type) {
		if (type == null) {
			return "";
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
			case "include_breaking_changes":
				return "###Breaking Changes\n Add your breaking changes here\n";
			case "include_extra_link":
				return "###More Documentation\n [Add your link here](https://www.google.com)\n";
			case "include_test_instructions":
				return "###Test Instructions\n Add your test instructions here\n";
			case "include_upgrade_guide":
				return "###Installation/ Upgrade Guide\n Add your installation/ Upgrade guide here\n";
			default:
				return "";
		}

	}

	/**
	 * @param value
	 * @return hashMap of AdditionalConfigurationOptions with integer and boolean value.
	 */
	public static EnumMap<AdditionalConfigurationOptions, Boolean> toBooleanList(Boolean value) {
		EnumMap<AdditionalConfigurationOptions, Boolean> configurationTypes = new EnumMap<>(AdditionalConfigurationOptions.class);
		for (AdditionalConfigurationOptions criteriaType : AdditionalConfigurationOptions.values()) {
			configurationTypes.put(criteriaType, value);
		}
		return configurationTypes;
	}

	/**
	 * @return Array list of AdditionalConfigurationOptions as string.
	 */
	public static ArrayList<String> toList() {
		ArrayList<String> configurationTypes = new ArrayList<String>();
		for (AdditionalConfigurationOptions type : AdditionalConfigurationOptions.values()) {
			configurationTypes.add(type.name());
		}
		return configurationTypes;
	}


}
