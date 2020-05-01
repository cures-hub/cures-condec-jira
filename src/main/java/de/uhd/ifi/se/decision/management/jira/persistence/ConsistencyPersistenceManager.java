package de.uhd.ifi.se.decision.management.jira.persistence;


import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DiscardedLinkSuggestionsInDatabase;
import net.java.ao.Query;
import scala.util.parsing.combinator.testing.Str;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class responsible for the persistence of the consistency component.
 * Groups are stored in the internal database of Jira.
 *
 * @see DiscardedLinkSuggestionsInDatabase
 */

public class ConsistencyPersistenceManager {

	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	public static final IssueManager ISSUE_MANAGER = ComponentAccessor.getIssueManager();


	public static List<Issue> getDiscardedSuggestions(Issue baseIssue) {
		return ConsistencyPersistenceManager.getDiscardedSuggestions(baseIssue.getKey());
	}

	public static List<Issue> getDiscardedSuggestions(String baseIssueKey) {
		List<Issue> discardedSuggestions = new ArrayList<>();
		DiscardedLinkSuggestionsInDatabase[] discardedLinkSuggestions = ACTIVE_OBJECTS.find(DiscardedLinkSuggestionsInDatabase.class,
			Query.select().where("ORIGIN_ISSUE_KEY = ?", baseIssueKey));
		for (DiscardedLinkSuggestionsInDatabase discardedLinkSuggestion : discardedLinkSuggestions) {
			discardedSuggestions.add(ISSUE_MANAGER.getIssueObject(discardedLinkSuggestion.getDiscardedIssueKey()));
		}
		return discardedSuggestions;
	}

	public static DiscardedLinkSuggestionsInDatabase[] getDiscardedSuggestion(Issue baseIssue, Issue targetIssue) {
		return ConsistencyPersistenceManager.getDiscardedSuggestion(baseIssue.getKey(), targetIssue.getKey());
	}

	public static DiscardedLinkSuggestionsInDatabase[] getDiscardedSuggestion(String baseIssueKey, String targetIssueKey) {
		DiscardedLinkSuggestionsInDatabase[] discardedLinkSuggestions = ACTIVE_OBJECTS.find(DiscardedLinkSuggestionsInDatabase.class,
			Query.select().where("ORIGIN_ISSUE_KEY = ? AND DISCARDED_ISSUE_KEY = ?", baseIssueKey, targetIssueKey));

		return discardedLinkSuggestions;
	}

	public static long addDiscardedSuggestions(String originIssueKey, String discardedSuggestionKey, String projectKey) {
		long id;
		//null checks
		if (originIssueKey == null || discardedSuggestionKey == null || projectKey == null) {
			id = -1;
		} else {
			// if null check passes
			// exists check
			DiscardedLinkSuggestionsInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedSuggestion(originIssueKey, discardedSuggestionKey);
			if (discardedLinkSuggestionsInDatabase.length > 0) {
				id = discardedLinkSuggestionsInDatabase[0].getId();
			} else {
				//not null parameter and does not already exist -> create new
				final DiscardedLinkSuggestionsInDatabase discardedLinkSuggestionInDatabase = ACTIVE_OBJECTS.create(DiscardedLinkSuggestionsInDatabase.class);
				discardedLinkSuggestionInDatabase.setOriginIssueKey(originIssueKey);
				discardedLinkSuggestionInDatabase.setDiscardedIssueKey(discardedSuggestionKey);
				discardedLinkSuggestionInDatabase.setProjectKey(projectKey);
				discardedLinkSuggestionInDatabase.save();
				id = discardedLinkSuggestionInDatabase.getId();
			}
		}
		return id;
	}

	public static long addDiscardedSuggestions(Issue originIssue, Issue discardedSuggestion) {
		return addDiscardedSuggestions(originIssue.getKey(), discardedSuggestion.getKey(), originIssue.getProjectObject().getKey());
	}
}
