package de.uhd.ifi.se.decision.management.jira.consistency.implementation;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.consistency.ContextInformationProvider;

import javax.ws.rs.core.Application;

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
		int isCreatorEqual = i1.getCreator().equals(i2.getCreator()) ? 1 : 0;

		ApplicationUser i1Assignee = i1.getAssignee();
		ApplicationUser i2Assignee = i2.getAssignee();

		int isAssigneeEqual = 0;
		if (i1Assignee != null && i1Assignee.equals(i2.getAssignee())) {
			isAssigneeEqual = 1;
		}

		return isCreatorEqual + isAssigneeEqual;
	}
}
