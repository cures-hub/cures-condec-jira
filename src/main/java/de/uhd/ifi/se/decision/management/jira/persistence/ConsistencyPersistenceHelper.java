package de.uhd.ifi.se.decision.management.jira.persistence;


import com.atlassian.activeobjects.external.ActiveObjects;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.linksuggestion.suggestions.SuggestionType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
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
	public static KnowledgePersistenceManager persistenceManager;

	//------------------
	// Link suggestions
	//------------------
	public static List<KnowledgeElement> getDiscardedLinkSuggestions(KnowledgeElement baseElement) {
		if (baseElement == null || baseElement.getProject() == null){
			return new ArrayList<>();
		}
		return ConsistencyPersistenceHelper.getDiscardedSuggestions(baseElement, SuggestionType.LINK);
	}


	public static long addDiscardedLinkSuggestions(KnowledgeElement origin, KnowledgeElement discarded) {
		if (origin == null || discarded == null) {
			return -1;
		}
		return addDiscardedSuggestions(origin, discarded, SuggestionType.LINK);
	}


	//------------------
	// Duplicates
	//------------------


	public static List<KnowledgeElement> getDiscardedDuplicates(KnowledgeElement base) {
		if (base == null || base.getProject() == null){
			return new ArrayList<>();
		}
		return getDiscardedSuggestions(base, SuggestionType.DUPLICATE);
	}

	public static long addDiscardedDuplicate(KnowledgeElement origin, KnowledgeElement target) {
		return addDiscardedSuggestions(origin, target, SuggestionType.DUPLICATE);
	}

	//------------------
	// General Suggestion
	//------------------

	private static List<KnowledgeElement> getDiscardedSuggestions(KnowledgeElement origin, SuggestionType type) {
		List<KnowledgeElement> discardedSuggestions = new ArrayList<>();
		Optional<DiscardedSuggestionInDatabase[]> discardedLinkSuggestions = Optional.ofNullable(ACTIVE_OBJECTS.find(DiscardedSuggestionInDatabase.class,
			Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND TYPE = ?", origin.getProject().getProjectKey(), origin.getId(), type)));
		persistenceManager = KnowledgePersistenceManager.getOrCreate(origin.getProject().getProjectKey());

		for (DiscardedSuggestionInDatabase discardedLinkSuggestion : discardedLinkSuggestions.orElseGet(() -> new DiscardedSuggestionInDatabase[0])) {
			discardedSuggestions.add(persistenceManager.getKnowledgeElement(discardedLinkSuggestion.getDiscardedElementId(), discardedLinkSuggestion.getDiscElDocumentationLocation()));
		}
		return discardedSuggestions;
	}

	private static DiscardedSuggestionInDatabase[] getDiscardedSuggestion(KnowledgeElement origin, KnowledgeElement target, SuggestionType type) {
		DiscardedSuggestionInDatabase[] discardedLinkSuggestions = ACTIVE_OBJECTS.find(DiscardedSuggestionInDatabase.class,
			Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND DISCARDED_ELEMENT_ID = ? AND TYPE = ?", origin.getProject().getProjectKey(), origin.getId(), target.getId(), type));

		return discardedLinkSuggestions;
	}

	public static long addDiscardedSuggestions(KnowledgeElement origin, KnowledgeElement discardedElement, SuggestionType type) {
		long id;
		//null checks
		if (origin == null || origin.getProject() == null|| discardedElement == null || type == null) {
			id = -1;
		} else {
			// if null check passes
			// exists check
			DiscardedSuggestionInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedSuggestion(origin, discardedElement, type);
			if (discardedLinkSuggestionsInDatabase.length > 0) {
				id = discardedLinkSuggestionsInDatabase[0].getId();
			} else {
				//not null parameter and does not already exist -> create new
				final DiscardedSuggestionInDatabase discardedLinkSuggestionInDatabase = ACTIVE_OBJECTS.create(DiscardedSuggestionInDatabase.class);
				discardedLinkSuggestionInDatabase.setOriginId(origin.getId());
				discardedLinkSuggestionInDatabase.setDiscardedElementId(discardedElement.getId());
				discardedLinkSuggestionInDatabase.setType(type);
				discardedLinkSuggestionInDatabase.setProjectKey(origin.getProject().getProjectKey());
				discardedLinkSuggestionInDatabase.setOriginDocumentationLocation(origin.getDocumentationLocationAsString());
				discardedLinkSuggestionInDatabase.setDiscElDocumentationLocation(discardedElement.getDocumentationLocationAsString());

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
