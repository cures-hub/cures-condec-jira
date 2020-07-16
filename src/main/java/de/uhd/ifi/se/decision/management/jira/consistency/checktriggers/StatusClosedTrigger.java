package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;


public class StatusClosedTrigger extends TriggerChain {

	public StatusClosedTrigger(IssueEvent issueEvent) {
		super.setIssueEvent(issueEvent);
	}

	public StatusClosedTrigger() {
	}

	@Override
	public boolean isTriggered() {
		return super.getIssueEvent() != null && EventType.ISSUE_CLOSED_ID.equals(super.getIssueEvent().getEventTypeId());
	}

	@Override
	public String getName() {
		return "closed";
	}


}
