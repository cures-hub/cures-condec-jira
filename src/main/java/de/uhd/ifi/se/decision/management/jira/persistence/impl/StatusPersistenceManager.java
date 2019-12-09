package de.uhd.ifi.se.decision.management.jira.persistence.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

public class StatusPersistenceManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatusPersistenceManager.class);

	public static void setStatusForElement(DecisionKnowledgeElement decisionKnowledgeElement, KnowledgeStatus status) {
		if (decisionKnowledgeElement == null || status == null) {
			LOGGER.error("Element or Status are null");
			return;
		}
		decisionKnowledgeElement.setStatus(status);
		AbstractPersistenceManagerForSingleLocation manager = KnowledgePersistenceManager
				.getOrCreate(decisionKnowledgeElement.getProject().getProjectKey())
				.getManagerForSingleLocation(decisionKnowledgeElement.getDocumentationLocation());
		DecisionKnowledgeElement element = manager.getDecisionKnowledgeElement(decisionKnowledgeElement.getId());
		if (!setTypeByChange(status, element, manager)) {
			return;
		}
	}

	private static boolean setTypeByChange(KnowledgeStatus status, DecisionKnowledgeElement element,
			AbstractPersistenceManagerForSingleLocation manager) {
		if (element == null) {
			return false;
		}
		if (element.getType().equals(KnowledgeType.DECISION)) {
			if (status.equals(KnowledgeStatus.REJECTED) || status.equals(KnowledgeStatus.IDEA)
					|| status.equals(KnowledgeStatus.DISCARDED)) {
				ApplicationUser user = element.getCreator();
				element.setType(KnowledgeType.ALTERNATIVE);
				manager.updateDecisionKnowledgeElementWithoutStatusChange(element, user);
			}
			if (status.equals(KnowledgeStatus.DECIDED)) {
				return false;
			}
		}
		if (element.getType().equals(KnowledgeType.ALTERNATIVE) && status.equals(KnowledgeStatus.DECIDED)) {
			ApplicationUser user = manager.getCreator(element);
			element.setType(KnowledgeType.DECISION);
			manager.updateDecisionKnowledgeElementWithoutStatusChange(element, user);
		}
		return true;
	}
}
