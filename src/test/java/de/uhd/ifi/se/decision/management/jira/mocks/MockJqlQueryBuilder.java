package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.jql.builder.JqlQueryBuilder;

public class MockJqlQueryBuilder extends JqlQueryBuilder {
	public MockJqlQueryBuilder () {
		super();
		JqlQueryBuilder.newBuilder();
	}
}
