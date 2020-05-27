package de.uhd.ifi.se.decision.management.jira.consistency.implementation;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import de.uhd.ifi.se.decision.management.jira.consistency.ConsistencyCheckEventTrigger;


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
