package de.uhd.ifi.se.decision.management.jira.view.dashboard;

import java.util.Map;

/**
 * Dashboard item that presents metrics regarding the intra-rationale
 * completeness.
 */
public class RationaleCompletenessDashboardItem extends ConDecDashboardItem {

	@Override
	public Map<String, Object> getAdditionalParameters() {
		return fillAdditionalParameters();
	}
}