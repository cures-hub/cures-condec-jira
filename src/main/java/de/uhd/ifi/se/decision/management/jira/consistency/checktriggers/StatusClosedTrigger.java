package de.uhd.ifi.se.decision.management.jira.consistency.checktriggers;

import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;


public class StatusClosedTrigger implements ConsistencyCheckEventTrigger, TriggerChain<ConsistencyCheckEventTrigger> {

	private IssueEvent issueEvent;
	private TriggerChain nextChainLink;

	public StatusClosedTrigger(IssueEvent issueEvent) {
		this.issueEvent = issueEvent;
	}

	public StatusClosedTrigger() {
	}

	public void setIssueEvent(IssueEvent event) {
		this.issueEvent = event;
	}


	@Override
	public boolean isTriggered() {
		return issueEvent != null && EventType.ISSUE_CLOSED_ID.equals(this.issueEvent.getEventTypeId());
	}

	@Override
	public String getCurrentProjectKey() {
		return this.issueEvent.getProject().getKey();
	}

	@Override
	public String getName() {
		return "closed";
	}

	@Override
	public TriggerChain<ConsistencyCheckEventTrigger> setNextChain(TriggerChain<ConsistencyCheckEventTrigger> chain) {
		this.nextChainLink = chain;
		return chain;
	}

	@Override
	public TriggerChain getNextChain() {
		return this.nextChainLink;
	}

	@Override
	public ConsistencyCheckEventTrigger getChainBase() {
		return this;
	}

	@Override
	public boolean calculate() {
		boolean activated = isTriggered() && isActivated();
		if (!activated && getNextChain() != null) {
			activated = getNextChain().calculate();
		}
		return activated;
	}


}
