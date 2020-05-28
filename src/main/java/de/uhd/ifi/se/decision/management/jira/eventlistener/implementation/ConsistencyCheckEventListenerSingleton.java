package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.consistency.ConsistencyCheckEventTrigger;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.StatusClosedTrigger;
import de.uhd.ifi.se.decision.management.jira.consistency.implementation.WorkflowDoneTrigger;
import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.eventlistener.Observabel;
import de.uhd.ifi.se.decision.management.jira.eventlistener.Subscriber;

import java.util.ArrayList;
import java.util.List;

public class ConsistencyCheckEventListenerSingleton implements IssueEventListener, Observabel {

	private List<ConsistencyCheckEventTrigger> consistencyCheckEventTriggerList;
	private final List<Subscriber> subscribers = new ArrayList<>();
	private static ConsistencyCheckEventListenerSingleton instance;

	private ConsistencyCheckEventListenerSingleton() {
		consistencyCheckEventTriggerList = new ArrayList<>();
		consistencyCheckEventTriggerList.add(new StatusClosedTrigger());
		consistencyCheckEventTriggerList.add(new WorkflowDoneTrigger());

	}

	public static ConsistencyCheckEventListenerSingleton getInstance() {
		if (ConsistencyCheckEventListenerSingleton.instance == null) {
			ConsistencyCheckEventListenerSingleton.instance = new ConsistencyCheckEventListenerSingleton();
		}
		return ConsistencyCheckEventListenerSingleton.instance;
	}

	public void onIssueEvent(IssueEvent issueEvent) {
		String projectKey = getProjectKeyFromEvent(issueEvent);
		for (ConsistencyCheckEventTrigger trigger : this.consistencyCheckEventTriggerList) {
			if (trigger.isActivated(projectKey) && trigger.isTriggered(issueEvent)) {
				notifyAllSubscribers();
			}
		}
	}

	private void notifyAllSubscribers() {
		this.subscribers.forEach((Subscriber::update));
	}

	private String getProjectKeyFromEvent(IssueEvent issueEvent) {
		return issueEvent.getIssue().getProjectObject().getKey();
	}

	public List<ConsistencyCheckEventTrigger> getAllConsistencyCheckEventTriggerNames() {
		return this.consistencyCheckEventTriggerList;
	}

	public boolean doesConsistencyCheckEventTriggerNameExist(String triggerName) {
		return this.getAllConsistencyCheckEventTriggerNames()
			.stream()
			.anyMatch(trigger -> trigger.getName().equals(triggerName));
	}

	@Override
	public void register(Subscriber subscriber) {
		if (subscriber != null)
			this.subscribers.add(subscriber);
	}

	@Override
	public void unregister(Subscriber subscriber) {
		this.subscribers.remove(subscriber);
	}


	public List<Subscriber> getSubscribers() {
		return subscribers;
	}
}
