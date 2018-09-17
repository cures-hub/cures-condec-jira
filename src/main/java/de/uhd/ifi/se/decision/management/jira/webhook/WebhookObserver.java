package de.uhd.ifi.se.decision.management.jira.webhook;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;

public class WebhookObserver {
	private String projectKey;
	private WebhookConnector connector;
	private List<Long> elementIds;

	public WebhookObserver(String projectKey) {
		this.elementIds = new ArrayList<Long>();
		this.projectKey = projectKey;
		this.connector = new WebhookConnector(projectKey);
	}

	public boolean sendElementChanges(DecisionKnowledgeElement decisionKnowledgeElement, boolean isDeleted) {
		ArrayList<DecisionKnowledgeElement> workItems = getWebhookRootElements(decisionKnowledgeElement);
		if (isDeleted && decisionKnowledgeElement.getType() == KnowledgeType.TASK) {
			workItems.remove(decisionKnowledgeElement);
		}
		boolean submitted = true;
		for (DecisionKnowledgeElement workItem : workItems) {
			if (!connector.postKnowledge(projectKey, workItem.getKey())) {
				submitted = false;
			}
		}
		return submitted;
	}

	public boolean sendElementChanges(DecisionKnowledgeElement decisionKnowledgeElement) {
		return sendElementChanges(decisionKnowledgeElement, false);
	}

	private ArrayList<DecisionKnowledgeElement> getWebhookRootElements(DecisionKnowledgeElement element) {
		ArrayList<DecisionKnowledgeElement> webhookRootElements = new ArrayList<DecisionKnowledgeElement>();

		AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
		List<DecisionKnowledgeElement> linkedElements = strategy.getLinkedElements(element);
		for (DecisionKnowledgeElement linkedElement : linkedElements) {
			if (elementIds.contains(linkedElement.getId())) {
				continue;
			}
			elementIds.add(linkedElement.getId());
			if (linkedElement.getType().equals(KnowledgeType.TASK)) {
				webhookRootElements.add(linkedElement);
			}
			webhookRootElements.addAll(getWebhookRootElements(linkedElement));
		}
		return webhookRootElements;
	}
}