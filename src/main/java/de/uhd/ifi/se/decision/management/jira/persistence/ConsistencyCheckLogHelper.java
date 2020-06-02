package de.uhd.ifi.se.decision.management.jira.persistence;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.ConsistencyCheckLogsInDatabase;
import net.java.ao.Query;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class ConsistencyCheckLogHelper {
	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	public static long approveCheck(Issue issue, String user) {
		AtomicLong id = new AtomicLong(-1);
		if (issue != null){
			Optional<ConsistencyCheckLogsInDatabase> check = getCheck(issue.getKey());
			check.ifPresent(presentCheck -> {
				id.set(presentCheck.getId());
				presentCheck.setApprover(user);
				presentCheck.save();
			});
		}
		return id.get();
	}

	public static Optional<ConsistencyCheckLogsInDatabase> getCheck(String issueKey) {
		ConsistencyCheckLogsInDatabase[] consistencyCheckLogsInDatabase =
			ACTIVE_OBJECTS.find(ConsistencyCheckLogsInDatabase.class,
				Query.select().where("ISSUE_KEY = ?", issueKey));
		ConsistencyCheckLogsInDatabase check = consistencyCheckLogsInDatabase.length < 1 ? null : consistencyCheckLogsInDatabase[0];
		return Optional.ofNullable(check);

	}

	public static long addCheck(Issue issue) {
		AtomicLong id = new AtomicLong();
		//null checks
		if (issue == null) {
			id.set(-1);
		} else {
			// if null check passes
			// exists check
			Optional<ConsistencyCheckLogsInDatabase> check = getCheck(issue.getKey());
			check.ifPresent((presentCheck) -> id.set(presentCheck.getId()));

			if (check.isEmpty()) {
				//not null parameter and does not already exist -> create new
				final ConsistencyCheckLogsInDatabase consistencyCheckLogInDatabase = ACTIVE_OBJECTS.create(ConsistencyCheckLogsInDatabase.class);
				consistencyCheckLogInDatabase.setIssueKey(issue.getKey());
				consistencyCheckLogInDatabase.setProjectKey(issue.getProjectObject().getKey());
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

	public static boolean doesIssueNeedApproval(Issue issue) {
		Optional<ConsistencyCheckLogsInDatabase> check = getCheck(issue.getKey());
		//If a check exists in database that needs approval return true;
		return check.isPresent() && !isCheckApproved(check.get());
	}

	private static boolean isCheckApproved(ConsistencyCheckLogsInDatabase check) {
		return check.getApprover() != null && !check.getApprover().isBlank() && !check.getApprover().isEmpty();
	}

	public static void resetConsistencyCheckLogs() {
		ConsistencyCheckLogsInDatabase[] consistencyCheckLogs = ACTIVE_OBJECTS.find(ConsistencyCheckLogsInDatabase.class,
			Query.select().where("1 = 1"));
		ACTIVE_OBJECTS.delete(consistencyCheckLogs);
	}


	public static void deleteCheck(Issue issue) {
		getCheck(issue.getKey()).ifPresent(ACTIVE_OBJECTS::delete);

	}
}
