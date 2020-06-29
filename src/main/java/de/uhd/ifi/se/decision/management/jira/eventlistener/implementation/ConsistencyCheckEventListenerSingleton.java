package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.consistency.checktriggers.ConsistencyCheckEventTrigger;
import de.uhd.ifi.se.decision.management.jira.consistency.checktriggers.StatusClosedTrigger;
import de.uhd.ifi.se.decision.management.jira.consistency.checktriggers.WorkflowDoneTrigger;
import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;

import java.util.ArrayList;
import java.util.List;

public class ConsistencyCheckEventListenerSingleton implements IssueEventListener {

	private final List<ConsistencyCheckEventTrigger> consistencyCheckEventTriggerList;
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
		boolean triggered = false;
		for (ConsistencyCheckEventTrigger trigger : this.consistencyCheckEventTriggerList) {
			if (trigger.isActivated(projectKey) && trigger.isTriggered(issueEvent)) {
				ConsistencyCheckLogHelper.addCheck(issueEvent.getIssue());
				triggered = true;
			}
		}
		if (! triggered && "workflow".equals(issueEvent.getParams().get("eventsource"))){
			ConsistencyCheckLogHelper.deleteCheck(issueEvent.getIssue());
		}
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


}
