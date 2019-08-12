package de.uhd.ifi.se.decision.management.jira.persistence;

import com.atlassian.activeobjects.external.ActiveObjects;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.KnowledgeStatusInDatabase;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionStatusManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionStatusManager.class);
	private static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();


	public static void setStatusForElement(DecisionKnowledgeElement element, KnowledgeStatus status) {
		if (element == null || status == null) {
			LOGGER.error("Element or Status are null");
			return;
		}
		if (isStatusInDatabase(element)) {
			for (KnowledgeStatusInDatabase statusInDatabase : ACTIVE_OBJECTS.find(KnowledgeStatusInDatabase.class)) {
				if (statusInDatabase.getId() == element.getId()) {
					setParameters(statusInDatabase, element, status);
					statusInDatabase.save();
				}
			}
		} else {
			KnowledgeStatusInDatabase knowledgeStatusInDatabase = ACTIVE_OBJECTS.create(KnowledgeStatusInDatabase.class);
			setParameters(knowledgeStatusInDatabase, element, status);
			knowledgeStatusInDatabase.save();
		}

	}

	public static KnowledgeStatus getStatusForElement(DecisionKnowledgeElement element) {
		for (KnowledgeStatusInDatabase statusInDatabase : ACTIVE_OBJECTS.find(KnowledgeStatusInDatabase.class)) {
			if(element.getType().equals(KnowledgeType.DECISION)){
				return KnowledgeStatus.DECIDED;
			}
			if (statusInDatabase.getElementId() == element.getId()) {
				return KnowledgeStatus.getKnowledgeStatus(statusInDatabase.getStatus());
			}
		}
		return KnowledgeStatus.UNDEFINED;
	}

	public static boolean isStatusInDatabase(DecisionKnowledgeElement element) {
		for (KnowledgeStatusInDatabase statusInDatabase : ACTIVE_OBJECTS.find(KnowledgeStatusInDatabase.class)) {
			if (statusInDatabase.getElementId() == element.getId()) {
				return true;
			}
		}
		return false;
	}

	public static void deleteStatus(DecisionKnowledgeElement element) {
		for (KnowledgeStatusInDatabase databaseEntry : ACTIVE_OBJECTS.find(KnowledgeStatusInDatabase.class, Query.select().where("ID = ?", element.getId()))) {
			KnowledgeStatusInDatabase.deleteStatus(databaseEntry);
		}
	}

	private static void setParameters(KnowledgeStatusInDatabase statusInDatabase, DecisionKnowledgeElement element, KnowledgeStatus status) {
		statusInDatabase.setDocumentationLocation(element.getDocumentationLocationAsString());
		statusInDatabase.setElementId(element.getId());
		statusInDatabase.setStatus(status.toString());
		return;
	}
}
