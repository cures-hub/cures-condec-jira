package de.uhd.ifi.se.decision.management.jira.persistence.recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.atlassian.activeobjects.external.ActiveObjects;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DiscardedRecommendationInDatabase;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import net.java.ao.Query;

/**
 * Responsible for the persistence of discarded {@link Recommendation}s.
 *
 * @see DiscardedRecommendationInDatabase
 */
public class DiscardedRecommendationPersistenceManager {

	public static final ActiveObjects ACTIVE_OBJECTS = ComponentGetter.getActiveObjects();

	/**
	 * @param baseElement
	 *            {@link KnowledgeElement} with discarded link recommendations.
	 * @return list of discarded link recommendations for the base element.
	 */
	public static List<KnowledgeElement> getDiscardedLinkRecommendations(KnowledgeElement baseElement) {
		if (baseElement == null || baseElement.getProject() == null) {
			return new ArrayList<>();
		}
		return getDiscardedRecommendations(baseElement, RecommendationType.LINK);
	}

	// ------------------
	// Duplicates
	// ------------------

	public static List<KnowledgeElement> getDiscardedDuplicateRecommendations(KnowledgeElement base) {
		if (base == null || base.getProject() == null) {
			return new ArrayList<>();
		}
		return getDiscardedRecommendations(base, RecommendationType.DUPLICATE);
	}

	// ------------------
	// General Suggestion
	// ------------------

	private static List<KnowledgeElement> getDiscardedRecommendations(KnowledgeElement origin,
			RecommendationType type) {
		List<KnowledgeElement> discardedSuggestions = new ArrayList<>();
		Optional<DiscardedRecommendationInDatabase[]> discardedLinkSuggestions = Optional
				.ofNullable(ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class,
						Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND TYPE = ?",
								origin.getProject().getProjectKey(), origin.getId(), type)));
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager
				.getInstance(origin.getProject().getProjectKey());

		for (DiscardedRecommendationInDatabase discardedLinkSuggestion : discardedLinkSuggestions
				.orElseGet(() -> new DiscardedRecommendationInDatabase[0])) {
			discardedSuggestions
					.add(persistenceManager.getKnowledgeElement(discardedLinkSuggestion.getDiscardedElementId(),
							discardedLinkSuggestion.getDiscElDocumentationLocation()));
		}
		return discardedSuggestions;
	}

	public static DiscardedRecommendationInDatabase[] getDiscardedRecommendation(LinkRecommendation recommendation) {
		DiscardedRecommendationInDatabase[] discardedRecommendationsInDatabase = ACTIVE_OBJECTS.find(
				DiscardedRecommendationInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND DISCARDED_ELEMENT_ID = ? AND TYPE = ?",
						recommendation.getSource().getProject().getProjectKey(), recommendation.getSource().getId(),
						recommendation.getTarget().getId(), recommendation.getRecommendationType()));

		return discardedRecommendationsInDatabase;
	}

	public static boolean removeDiscardedRecommendation(LinkRecommendation recommendation) {
		boolean isRemoved = false;
		DiscardedRecommendationInDatabase[] discardedRecommendationsInDatabase = getDiscardedRecommendation(
				recommendation);
		for (DiscardedRecommendationInDatabase discardedRecommendationInDatabase : discardedRecommendationsInDatabase) {
			DiscardedRecommendationInDatabase.deleteDiscardedRecommendation(discardedRecommendationInDatabase);
			isRemoved = true;
		}
		return isRemoved;
	}

	public static long saveDiscardedRecommendation(LinkRecommendation recommendation) {
		if (recommendation.getSource() == null || recommendation.getSource().getProject() == null
				|| recommendation.getTarget() == null) {
			return -1;
		}

		DiscardedRecommendationInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedRecommendation(
				recommendation);
		if (discardedLinkSuggestionsInDatabase.length > 0) {
			return discardedLinkSuggestionsInDatabase[0].getId();
		}

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
		return discardedLinkSuggestionInDatabase.getId();
	}
}
