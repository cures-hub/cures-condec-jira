package de.uhd.ifi.se.decision.management.jira.persistence;


import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.consistency.suggestions.SuggestionType;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DiscardedSuggestionInDatabase;
import net.java.ao.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class responsible for the persistence of the consistency component.
 * Groups are stored in the internal database of Jira.
 * This class is called ...Helper because of the codacy checks of the ConDec github project.
 *
 * @see DiscardedSuggestionInDatabase
 */

public class ConsistencyPersistenceHelper {

	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	public static final IssueManager ISSUE_MANAGER = ComponentAccessor.getIssueManager();

	//------------------
	// Link suggestions
	//------------------
	public static List<Issue> getDiscardedLinkSuggestions(Issue baseIssue) {
		return ConsistencyPersistenceHelper.getDiscardedSuggestions(baseIssue.getKey(), SuggestionType.LINK);
	}

	public static long addDiscardedLinkSuggestions(String originIssueKey, String discardedSuggestionKey, String projectKey) {
		return addDiscardedSuggestions(originIssueKey, discardedSuggestionKey, projectKey, SuggestionType.LINK);
	}

	public static long addDiscardedLinkSuggestions(Issue originIssue, Issue discardedSuggestion) {
		if (originIssue == null || discardedSuggestion == null) {
			return -1;
		}
		return addDiscardedSuggestions(originIssue.getKey(), discardedSuggestion.getKey(), originIssue.getProjectObject().getKey(), SuggestionType.LINK);
	}


	//------------------
	// Duplicates
	//------------------


	public static List<Issue> getDiscardedDuplicates(String baseIssueKey) {
		return getDiscardedSuggestions(baseIssueKey, SuggestionType.DUPLICATE);
	}

	public static long addDiscardedDuplicate(String originIssueKey, String targetIssueKey, String projectKey) {
		return addDiscardedSuggestions(originIssueKey, targetIssueKey, projectKey, SuggestionType.DUPLICATE);
	}

	//------------------
	// General Suggestion
	//------------------

	public static List<Issue> getDiscardedSuggestions(String baseIssueKey, SuggestionType type) {
		List<Issue> discardedSuggestions = new ArrayList<>();
		Optional<DiscardedSuggestionInDatabase[]> discardedLinkSuggestions = Optional.ofNullable(ACTIVE_OBJECTS.find(DiscardedSuggestionInDatabase.class,
			Query.select().where("ORIGIN_ISSUE_KEY = ? AND TYPE = ?", baseIssueKey, type)));
		for (DiscardedSuggestionInDatabase discardedLinkSuggestion : discardedLinkSuggestions.orElseGet(() -> new DiscardedSuggestionInDatabase[0])) {
			discardedSuggestions.add(ISSUE_MANAGER.getIssueObject(discardedLinkSuggestion.getDiscardedIssueKey()));
		}
		return discardedSuggestions;
	}

	private static DiscardedSuggestionInDatabase[] getDiscardedSuggestion(String baseIssueKey, String targetIssueKey, SuggestionType type) {
		DiscardedSuggestionInDatabase[] discardedLinkSuggestions = ACTIVE_OBJECTS.find(DiscardedSuggestionInDatabase.class,
			Query.select().where("ORIGIN_ISSUE_KEY = ? AND DISCARDED_ISSUE_KEY = ? AND TYPE = ?", baseIssueKey, targetIssueKey, type));

		return discardedLinkSuggestions;
	}

	public static long addDiscardedSuggestions(String originIssueKey, String discardedSuggestionKey, String projectKey, SuggestionType type) {
		long id;
		//null checks
		if (originIssueKey == null || discardedSuggestionKey == null || projectKey == null) {
			id = -1;
		} else {
			// if null check passes
			// exists check
			DiscardedSuggestionInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedSuggestion(originIssueKey, discardedSuggestionKey, type);
			if (discardedLinkSuggestionsInDatabase.length > 0) {
				id = discardedLinkSuggestionsInDatabase[0].getId();
			} else {
				//not null parameter and does not already exist -> create new
				final DiscardedSuggestionInDatabase discardedLinkSuggestionInDatabase = ACTIVE_OBJECTS.create(DiscardedSuggestionInDatabase.class);
				discardedLinkSuggestionInDatabase.setOriginIssueKey(originIssueKey);
				discardedLinkSuggestionInDatabase.setDiscardedIssueKey(discardedSuggestionKey);
				discardedLinkSuggestionInDatabase.setProjectKey(projectKey);
				discardedLinkSuggestionInDatabase.setType(type);
				discardedLinkSuggestionInDatabase.save();
				id = discardedLinkSuggestionInDatabase.getId();
			}
		}
		return id;
	}


	public static void resetDiscardedSuggestions() {
		DiscardedSuggestionInDatabase[] discardedLinkSuggestions = ACTIVE_OBJECTS.find(DiscardedSuggestionInDatabase.class,
			Query.select().where("1 = 1"));
		ACTIVE_OBJECTS.delete(discardedLinkSuggestions);
	}

}
