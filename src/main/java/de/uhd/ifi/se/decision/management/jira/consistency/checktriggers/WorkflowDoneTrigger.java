package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;


import com.atlassian.jira.event.issue.IssueEvent;

public class WorkflowDoneTrigger extends TriggerChain {

	private IssueEvent issueEvent;

	public WorkflowDoneTrigger(IssueEvent issueEvent) {
		this.issueEvent = issueEvent;
	}

	public WorkflowDoneTrigger() {
	}


	public void setIssueEvent(IssueEvent event) {
		this.issueEvent = event;
	}

	@Override
	public boolean isTriggered() {
		return issueEvent != null &&
			"workflow".equals(issueEvent.getParams().get("eventsource"))
			&& "done".equals(issueEvent.getIssue().getStatus().getStatusCategory().getKey());
	}


	@Override
	public String getCurrentProjectKey() {
		return this.issueEvent.getProject().getKey();
	}

	@Override
	public String getName() {
		return "done";
	}


}
