package de.uhd.ifi.se.decision.management.jira.persistence;

import com.atlassian.activeobjects.external.ActiveObjects;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ConsistencyCheckLogsInDatabase;
import net.java.ao.Query;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class ConsistencyCheckLogHelper {
	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public static long approveCheck(KnowledgeElement knowledgeElement, String user) {
		AtomicLong id = new AtomicLong(-1);
		if (knowledgeElement != null){
			Optional<ConsistencyCheckLogsInDatabase> check = getCheck(knowledgeElement);
			check.ifPresent(presentCheck -> {
				id.set(presentCheck.getId());
				presentCheck.setApprover(user);
				presentCheck.save();
			});
		}
		return id.get();
	}

	public static Optional<ConsistencyCheckLogsInDatabase> getCheck(KnowledgeElement knowledgeElement) {
		ConsistencyCheckLogsInDatabase[] consistencyCheckLogsInDatabase =
			ACTIVE_OBJECTS.find(ConsistencyCheckLogsInDatabase.class,
				Query.select().where("KNOWLEDGE_ID = ? AND PROJECT_KEY = ?", knowledgeElement.getId(), knowledgeElement.getProject().getProjectKey()));
		ConsistencyCheckLogsInDatabase check = consistencyCheckLogsInDatabase.length < 1 ? null : consistencyCheckLogsInDatabase[0];
		return Optional.ofNullable(check);

	}

	public static long addCheck(KnowledgeElement knowledgeElement) {
		AtomicLong id = new AtomicLong();
		//null checks
		if (knowledgeElement == null) {
			id.set(-1);
		} else {
			// if null check passes
			// exists check
			Optional<ConsistencyCheckLogsInDatabase> check = getCheck(knowledgeElement);
			check.ifPresent((presentCheck) -> id.set(presentCheck.getId()));

			if (check.isEmpty()) {
				//not null parameter and does not already exist -> create new
				final ConsistencyCheckLogsInDatabase consistencyCheckLogInDatabase = ACTIVE_OBJECTS.create(ConsistencyCheckLogsInDatabase.class);
				consistencyCheckLogInDatabase.setKnowledgeId(knowledgeElement.getId());
				consistencyCheckLogInDatabase.setProjectKey(knowledgeElement.getProject().getProjectKey());
				consistencyCheckLogInDatabase.setLocation(knowledgeElement.getDocumentationLocationAsString());
				consistencyCheckLogInDatabase.save();
				id.set(consistencyCheckLogInDatabase.getId());
			}
		}
		return id.get();
	}

	/*
	public static void resetCheckApproval(Issue issue) {
		Optional<ConsistencyCheckLogsInDatabase> check = getCheck(issue.getKey());

		//check if: check exists and was approved
		check.ifPresent((existingCheck) -> {
			//true -> reset approver to empty
			existingCheck.setApprover("");
			existingCheck.save();
		});
	}
	*/

	public static boolean doesKnowledgeElementNeedApproval(KnowledgeElement knowledgeElement) {
		Optional<ConsistencyCheckLogsInDatabase> check = getCheck(knowledgeElement);
		//If a check exists in database that needs approval return true;
		return check.isPresent() && !isCheckApproved(check.get());
	}

	public static boolean isCheckApproved(ConsistencyCheckLogsInDatabase check) {
		return check != null && check.getApprover() != null && !check.getApprover().isBlank();
	}

	public static void resetConsistencyCheckLogs() {
		ConsistencyCheckLogsInDatabase[] consistencyCheckLogs = ACTIVE_OBJECTS.find(ConsistencyCheckLogsInDatabase.class,
			Query.select().where("1 = 1"));
		ACTIVE_OBJECTS.delete(consistencyCheckLogs);
	}


	public static void deleteCheck(KnowledgeElement knowledgeElement) {
		getCheck(knowledgeElement).ifPresent(ACTIVE_OBJECTS::delete);

	}
}
