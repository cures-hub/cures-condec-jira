package de.uhd.ifi.se.decision.management.jira.webhook;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookObserver {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebhookObserver.class);
	private String projectKey;
	private WebhookConnector connector;
	private List<Long> elementIds;

	public WebhookObserver(String projectKey) {
		this.elementIds = new ArrayList<Long>();
		this.projectKey = projectKey;
		this.connector = new WebhookConnector(projectKey);
	}

	public boolean sendElementChanges(DecisionKnowledgeElement decisionKnowledgeElement, boolean isDeleted) {
		if(decisionKnowledgeElement == null){
			LOGGER.error("Webhook could not be created because the Element is null");
			return false;
		}
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