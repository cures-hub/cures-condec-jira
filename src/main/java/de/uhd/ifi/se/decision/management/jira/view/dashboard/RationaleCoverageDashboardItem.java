package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.Map;

public class RationaleCoverageDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getAdditionalParameters() {
		return fillAdditionalParameters();
	}
}