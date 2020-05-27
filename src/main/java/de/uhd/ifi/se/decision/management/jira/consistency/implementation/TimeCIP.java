package de.uhd.ifi.se.decision.management.jira.consistency.implementation;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.ContextInformationProvider;

public class TimeCIP implements ContextInformationProvider {
	private String id = "TimeCIP_ms";
	private String name = "TimeCIP";

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public double assessRelation(Issue i1, Issue i2) {
		long differenceInDays = i1.getCreated().getTime() - i2.getCreated().getTime() / (1000 * 60 * 60 * 24);

		return 1. / Math.abs(differenceInDays + 1);
	}
}
