package de.uhd.ifi.se.decision.management.jira.consistency.implementation;


import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.consistency.ConsistencyCheckEventTrigger;

public class WorkflowDoneTrigger implements ConsistencyCheckEventTrigger {
	@Override
	public boolean isTriggered(IssueEvent issueEvent) {
		return "workflow".equals(issueEvent.getParams().get("eventsource"))
			&& "done".equals(issueEvent.getIssue().getStatus().getSimpleStatus().getStatusCategory().getKey());
	}

	@Override
	public String getName() {
		return "done";
	}
}
