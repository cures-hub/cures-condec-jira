package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.Map;

/**
 * Displays the rationale coverage of requirements, code, and other software
 * artifacts (=knowledge elements). For example, displays how many decisions are
 * linked to a requirement.
 */
public class RationaleCoverageDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getAdditionalParameters() {
		return fillAdditionalParameters();
	}
}