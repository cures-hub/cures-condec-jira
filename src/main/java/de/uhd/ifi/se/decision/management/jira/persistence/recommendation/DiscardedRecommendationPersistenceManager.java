package de.uhd.ifi.se.decision.management.jira.persistence.recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.atlassian.activeobjects.external.ActiveObjects;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DiscardedRecommendationInDatabase;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import net.java.ao.Query;

/**
 * Class responsible for the persistence of the consistency component. Groups
 * are stored in the internal database of Jira. This class is called ...Helper
 * because of the codacy checks of the ConDec github project.
 *
 * @see DiscardedRecommendationInDatabase
 */

public class DiscardedRecommendationPersistenceManager {

	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();
	public static KnowledgePersistenceManager persistenceManager;

	// ------------------
	// Link suggestions
	// ------------------
	public static List<KnowledgeElement> getDiscardedLinkSuggestions(KnowledgeElement baseElement) {
		if (baseElement == null || baseElement.getProject() == null) {
			return new ArrayList<>();
		}
		return DiscardedRecommendationPersistenceManager.getDiscardedSuggestions(baseElement, RecommendationType.LINK);
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

	public static long saveDiscardedRecommendation(LinkRecommendation recommendation) {
		long id;
		// null checks
		if (recommendation.getSource() == null || recommendation.getSource().getProject() == null
				|| recommendation.getTarget() == null) {
			return -1;
		}
		// if null check passes
		// exists check
		DiscardedRecommendationInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedSuggestion(
				recommendation.getSource(), recommendation.getTarget(), recommendation.getRecommendationType());
		if (discardedLinkSuggestionsInDatabase.length > 0) {
			id = discardedLinkSuggestionsInDatabase[0].getId();
		} else {
			// not null parameter and does not already exist -> create new
			final DiscardedRecommendationInDatabase discardedLinkSuggestionInDatabase = ACTIVE_OBJECTS
					.create(DiscardedRecommendationInDatabase.class);
			discardedLinkSuggestionInDatabase.setOriginId(recommendation.getSource().getId());
			discardedLinkSuggestionInDatabase.setDiscardedElementId(recommendation.getTarget().getId());
			discardedLinkSuggestionInDatabase.setType(recommendation.getRecommendationType());
			discardedLinkSuggestionInDatabase.setProjectKey(recommendation.getSource().getProject().getProjectKey());
			discardedLinkSuggestionInDatabase
					.setOriginDocumentationLocation(recommendation.getSource().getDocumentationLocationAsString());
			discardedLinkSuggestionInDatabase
					.setDiscElDocumentationLocation(recommendation.getTarget().getDocumentationLocationAsString());

			discardedLinkSuggestionInDatabase.save();
			id = discardedLinkSuggestionInDatabase.getId();
		}

		return id;
	}

	public static void resetDiscardedSuggestions() {
		DiscardedRecommendationInDatabase[] discardedLinkSuggestions = ACTIVE_OBJECTS
				.find(DiscardedRecommendationInDatabase.class, Query.select().where("1 = 1"));
		ACTIVE_OBJECTS.delete(discardedLinkSuggestions);
	}

}
