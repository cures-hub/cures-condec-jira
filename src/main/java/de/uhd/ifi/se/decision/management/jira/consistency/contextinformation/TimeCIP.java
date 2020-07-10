package de.uhd.ifi.se.decision.management.jira.consistency.contextinformation;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.LinkSuggestion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TimeCIP implements ContextInformationProvider {
	private String id = "TimeCIP_ms";
	private String name = "TimeCIP";
	private Collection<LinkSuggestion> linkSuggestions;

	public TimeCIP() {
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
	public Collection<LinkSuggestion> getLinkSuggestions() {
		return this.linkSuggestions;
	}

	@Override
	public void assessRelation(Issue baseIssue, List<Issue> issuesToTest) {
		for (Issue issueToTest : issuesToTest) {
			LinkSuggestion linkSuggestion = new LinkSuggestion(baseIssue, issueToTest);
			long differenceInHours = baseIssue.getCreated().getTime() - issueToTest.getCreated().getTime() / (1000 * 60 * 60);
			linkSuggestion.addToScore((1. / Math.abs(differenceInHours + 1)), this.getName());
			this.linkSuggestions.add(linkSuggestion);

		}
	}
}
