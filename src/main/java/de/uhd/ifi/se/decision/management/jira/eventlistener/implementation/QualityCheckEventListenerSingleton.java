package de.uhd.ifi.se.decision.management.jira.eventlistener.implementation;

import com.atlassian.jira.event.issue.IssueEvent;
import de.uhd.ifi.se.decision.management.jira.eventlistener.IssueEventListener;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConsistencyCheckLogHelper;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.IssueClosedTrigger;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.TriggerChain;
import de.uhd.ifi.se.decision.management.jira.quality.checktriggers.WorkflowDoneTrigger;

import java.util.ArrayList;
import java.util.List;

public class QualityCheckEventListenerSingleton implements IssueEventListener {

	private static IssueEventListener instance;
	private final TriggerChain chainStart;

	private QualityCheckEventListenerSingleton() {
		this.chainStart = new IssueClosedTrigger();
		this.chainStart
			.setNextChain(new WorkflowDoneTrigger());
	}

	public static IssueEventListener getInstance() {
		if (QualityCheckEventListenerSingleton.instance == null) {
			QualityCheckEventListenerSingleton.instance = new QualityCheckEventListenerSingleton();
		}
		return QualityCheckEventListenerSingleton.instance;
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


	public boolean doesQualityCheckEventTriggerNameExist(String triggerName) {
		return getAllQualityCheckEventTriggerNames().stream().anyMatch(name -> name.equals(triggerName));
	}

	public List<String> getAllQualityCheckEventTriggerNames() {
		List<String> names = new ArrayList<>();
		TriggerChain currentChainLink = this.chainStart;
		while (currentChainLink != null) {
			names.add(currentChainLink.getName());
			currentChainLink = currentChainLink.getNextChain();

		}
		return names;
	}


}
