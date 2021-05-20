package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.atlassian.activeobjects.external.ActiveObjects;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DiscardedRecommendationInDatabase;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
import net.java.ao.Query;

/**
 * Class responsible for the persistence of the consistency component. Groups
 * are stored in the internal database of Jira. This class is called ...Helper
 * because of the codacy checks of the ConDec github project.
 *
 * @see DiscardedRecommendationInDatabase
 */

public class ConsistencyPersistenceHelper {

	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	public static KnowledgePersistenceManager persistenceManager;

	// ------------------
	// Link suggestions
	// ------------------
	public static List<KnowledgeElement> getDiscardedLinkSuggestions(KnowledgeElement baseElement) {
		if (baseElement == null || baseElement.getProject() == null) {
			return new ArrayList<>();
		}
		return ConsistencyPersistenceHelper.getDiscardedSuggestions(baseElement, RecommendationType.LINK);
	}

	public static long addDiscardedLinkSuggestions(KnowledgeElement origin, KnowledgeElement discarded) {
		if (origin == null || discarded == null) {
			return -1;
		}
		return saveDiscardedRecommendation(origin, discarded, RecommendationType.LINK);
	}

	// ------------------
	// Duplicates
	// ------------------

	public static List<KnowledgeElement> getDiscardedDuplicates(KnowledgeElement base) {
		if (base == null || base.getProject() == null) {
			return new ArrayList<>();
		}
		return getDiscardedSuggestions(base, RecommendationType.DUPLICATE);
	}

	public static long addDiscardedDuplicate(KnowledgeElement origin, KnowledgeElement target) {
		return saveDiscardedRecommendation(origin, target, RecommendationType.DUPLICATE);
	}

	// ------------------
	// General Suggestion
	// ------------------

	private static List<KnowledgeElement> getDiscardedSuggestions(KnowledgeElement origin, RecommendationType type) {
		List<KnowledgeElement> discardedSuggestions = new ArrayList<>();
		Optional<DiscardedRecommendationInDatabase[]> discardedLinkSuggestions = Optional
				.ofNullable(ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class,
						Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND TYPE = ?",
								origin.getProject().getProjectKey(), origin.getId(), type)));
		persistenceManager = KnowledgePersistenceManager.getOrCreate(origin.getProject().getProjectKey());

		for (DiscardedRecommendationInDatabase discardedLinkSuggestion : discardedLinkSuggestions
				.orElseGet(() -> new DiscardedRecommendationInDatabase[0])) {
			discardedSuggestions
					.add(persistenceManager.getKnowledgeElement(discardedLinkSuggestion.getDiscardedElementId(),
							discardedLinkSuggestion.getDiscElDocumentationLocation()));
		}
		return discardedSuggestions;
	}

	private static DiscardedRecommendationInDatabase[] getDiscardedSuggestion(KnowledgeElement origin,
			KnowledgeElement target, RecommendationType type) {
		DiscardedRecommendationInDatabase[] discardedLinkSuggestions = ACTIVE_OBJECTS.find(
				DiscardedRecommendationInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND DISCARDED_ELEMENT_ID = ? AND TYPE = ?",
						origin.getProject().getProjectKey(), origin.getId(), target.getId(), type));

		return discardedLinkSuggestions;
	}

	public static long saveDiscardedRecommendation(KnowledgeElement origin, KnowledgeElement discardedElement,
			RecommendationType type) {
		long id;
		// null checks
		if (origin == null || origin.getProject() == null || discardedElement == null || type == null) {
			id = -1;
		} else {
			// if null check passes
			// exists check
			DiscardedRecommendationInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedSuggestion(origin,
					discardedElement, type);
			if (discardedLinkSuggestionsInDatabase.length > 0) {
				id = discardedLinkSuggestionsInDatabase[0].getId();
			} else {
				// not null parameter and does not already exist -> create new
				final DiscardedRecommendationInDatabase discardedLinkSuggestionInDatabase = ACTIVE_OBJECTS
						.create(DiscardedRecommendationInDatabase.class);
				discardedLinkSuggestionInDatabase.setOriginId(origin.getId());
				discardedLinkSuggestionInDatabase.setDiscardedElementId(discardedElement.getId());
				discardedLinkSuggestionInDatabase.setType(type);
				discardedLinkSuggestionInDatabase.setProjectKey(origin.getProject().getProjectKey());
				discardedLinkSuggestionInDatabase
						.setOriginDocumentationLocation(origin.getDocumentationLocationAsString());
				discardedLinkSuggestionInDatabase
						.setDiscElDocumentationLocation(discardedElement.getDocumentationLocationAsString());

				discardedLinkSuggestionInDatabase.save();
				id = discardedLinkSuggestionInDatabase.getId();
			}
		}
		return id;
	}

	public static void resetDiscardedSuggestions() {
		DiscardedRecommendationInDatabase[] discardedLinkSuggestions = ACTIVE_OBJECTS
				.find(DiscardedRecommendationInDatabase.class, Query.select().where("1 = 1"));
		ACTIVE_OBJECTS.delete(discardedLinkSuggestions);
	}

}
