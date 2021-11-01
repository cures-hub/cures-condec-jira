package de.uhd.ifi.se.decision.management.jira.releasenotes;

import java.util.Locale;

/**
 * Type of target groups for release notes
 */
public enum TargetGroup {
	DEVELOPER, TESTER, ENDUSER;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public static TargetGroup getTargetGroup(String type) {
		if (type == null) {
			return TargetGroup.DEVELOPER;
		}
		switch (type.toLowerCase(Locale.ENGLISH)) {
		case "tester":
			return TargetGroup.TESTER;
		case "enduser":
			return TargetGroup.ENDUSER;
		default:
			return TargetGroup.DEVELOPER;
		}
	}
}