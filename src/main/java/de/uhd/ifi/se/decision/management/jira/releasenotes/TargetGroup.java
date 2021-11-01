package de.uhd.ifi.se.decision.management.jira.releasenotes;

/**
 * Stakeholders who are interested in release notes.
 */
public enum TargetGroup {
	DEVELOPER, TESTER, ENDUSER;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	public static TargetGroup getTargetGroup(String targetGroupAsString) {
		if (targetGroupAsString == null) {
			return DEVELOPER;
		}
		for (TargetGroup targetGroup : TargetGroup.values()) {
			if (targetGroupAsString.equalsIgnoreCase(targetGroup.name())) {
				return targetGroup;
			}
		}
		return DEVELOPER;
	}
}