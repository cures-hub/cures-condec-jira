package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.issue.fields.config.FieldConfig;

public class MockFieldConfigScheme extends com.atlassian.jira.issue.fields.config.MockFieldConfigScheme {

	@Override
	public FieldConfig getOneAndOnlyConfig() {
		return null;
	}
}
