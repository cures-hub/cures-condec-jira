package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.LinkSuggestion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserCIP implements ContextInformationProvider {
	private String id = "UserCIP_equalCreatorOrEqualAssignee";
	private String name = "UserCIP";
	private Collection<LinkSuggestion> linkSuggestions;

	public UserCIP() {
		this.linkSuggestions = new ArrayList<>();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}


	@Override
	public void assessRelation(Issue baseIssue, List<Issue> issuesToTest) {
		for (Issue issueToTest : issuesToTest) {
			LinkSuggestion linkSuggestion = new LinkSuggestion(baseIssue, issueToTest);

			linkSuggestion.addToScore(this.isApplicationUserEqual(baseIssue.getCreator(), issueToTest.getCreator())
					+ this.isApplicationUserEqual(baseIssue.getAssignee(), issueToTest.getAssignee())
					+ this.isApplicationUserEqual(baseIssue.getReporter(), issueToTest.getReporter())
					+ this.isApplicationUserEqual(baseIssue.getArchivedByUser(), issueToTest.getArchivedByUser()),
				this.getName()
			);
			linkSuggestions.add(linkSuggestion);

		}
	}

	private Double isApplicationUserEqual(ApplicationUser user1, ApplicationUser user2) {
		int isUserEqual = 0;
		if ((user1 != null && user1.equals(user2))) { //|| (user1 == null && user2 == null)) {
			isUserEqual = 1;
		}
		return Double.valueOf(isUserEqual);
	}

	@Override
	public Collection<LinkSuggestion> getLinkSuggestions() {
		return linkSuggestions;
	}
}
