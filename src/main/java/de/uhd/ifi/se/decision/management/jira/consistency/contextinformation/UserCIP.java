package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;

public class UserCIP implements ContextInformationProvider {
	private String id = "UserCIP_equalCreatorOrEqualAssignee";
	private String name = "UserCIP";

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}


	@Override
	public double assessRelation(Issue i1, Issue i2) {
		return this.isApplicationUserEqual(i1.getCreator(), i2.getCreator())
			+ this.isApplicationUserEqual(i1.getAssignee(), i2.getAssignee())
			+ this.isApplicationUserEqual(i1.getReporter(), i2.getReporter())
			+ this.isApplicationUserEqual(i1.getArchivedByUser(), i2.getArchivedByUser());
	}

	private int isApplicationUserEqual(ApplicationUser user1, ApplicationUser user2) {
		int isUserEqual = 0;
		if ((user1 != null && user1.equals(user2))) { //|| (user1 == null && user2 == null)) {
			isUserEqual = 1;
		}
		return isUserEqual;
	}
}
