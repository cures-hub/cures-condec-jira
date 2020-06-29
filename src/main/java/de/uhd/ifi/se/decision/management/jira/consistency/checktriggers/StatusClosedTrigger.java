package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;


public class StatusClosedTrigger implements ConsistencyCheckEventTrigger {
	@Override
	public boolean isTriggered(IssueEvent issueEvent) {
		return issueEvent != null && EventType.ISSUE_CLOSED_ID.equals(issueEvent.getEventTypeId());
	}

	@Override
	public String getName() {
		return "closed";
	}
}
