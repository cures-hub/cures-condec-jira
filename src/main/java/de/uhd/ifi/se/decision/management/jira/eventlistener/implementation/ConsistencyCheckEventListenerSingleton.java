package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.consistency.checktriggers.IssueClosedTrigger;
import de.uhd.ifi.se.decision.management.jira.consistency.checktriggers.TriggerChain;
import de.uhd.ifi.se.decision.management.jira.consistency.checktriggers.WorkflowDoneTrigger;
import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;

import java.util.ArrayList;
import java.util.List;

public class ConsistencyCheckEventListenerSingleton implements IssueEventListener {

	private static IssueEventListener instance;
	private TriggerChain chainStart;

	private ConsistencyCheckEventListenerSingleton() {
		this.chainStart = new IssueClosedTrigger();
		this.chainStart
			.setNextChain(new WorkflowDoneTrigger());
	}

	public static IssueEventListener getInstance() {
		if (ConsistencyCheckEventListenerSingleton.instance == null) {
			ConsistencyCheckEventListenerSingleton.instance = new ConsistencyCheckEventListenerSingleton();
		}
		return ConsistencyCheckEventListenerSingleton.instance;
	}

	public void onIssueEvent(IssueEvent issueEvent) {
		initChainLinks(issueEvent);

		boolean triggered = this.chainStart.calculate();

		if (triggered) {
			ConsistencyCheckLogHelper.addCheck(new KnowledgeElement(issueEvent.getIssue()));
		} else if ("workflow".equals(issueEvent.getParams().get("eventsource"))) {
			ConsistencyCheckLogHelper.deleteCheck(new KnowledgeElement(issueEvent.getIssue()));
		}
	}

	private void initChainLinks(IssueEvent event) {
		TriggerChain currentChainLink = this.chainStart;
		while (currentChainLink != null) {
			currentChainLink.setIssueEvent(event);
			currentChainLink = currentChainLink.getNextChain();
		}
	}


	public boolean doesConsistencyCheckEventTriggerNameExist(String triggerName) {
		return getAllConsistencyCheckEventTriggerNames().stream().anyMatch(name -> name.equals(triggerName));
	}

	public List<String> getAllConsistencyCheckEventTriggerNames() {
		List<String> names = new ArrayList<String>();
		TriggerChain currentChainLink = this.chainStart;
		while (currentChainLink != null) {
			names.add(currentChainLink.getName());
			currentChainLink = currentChainLink.getNextChain();

		}
		return names;
	}


}
