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
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.KnowledgeSource;
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
		List<KnowledgeElement> discardedElements = getDiscardedRecommendations(baseElement, RecommendationType.LINK);
		discardedElements.addAll(getDiscardedRecommendations(baseElement, RecommendationType.DUPLICATE));
		return discardedElements;
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
								origin.getProject().getProjectKey(), origin.getId(), RecommendationType.DUPLICATE)));
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

	/**
	 * @param origin
	 *            {@link KnowledgeElement} for which the discarded recommendations should be accessed.
	 * @return Discarded decision guidance {@link ElementRecommendation}s for the given origin.
	 */
	private static List<ElementRecommendation> getDiscardedDecisionGuidanceRecommendations(KnowledgeElement origin) {
		if (origin == null || origin.getProject() == null) {
			return new ArrayList<>();
		}
		List<ElementRecommendation> discardedSuggestions = new ArrayList<>();
		Optional<DiscardedRecommendationInDatabase[]> discardedRecommendations = Optional
			.ofNullable(ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class,
				Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND TYPE = ?",
					origin.getProject().getProjectKey(), origin.getId(), RecommendationType.EXTERNAL)));
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager
			.getInstance(origin.getProject().getProjectKey());

		for (DiscardedRecommendationInDatabase discardedRecommendation : discardedRecommendations
			.orElseGet(() -> new DiscardedRecommendationInDatabase[0])) {
			discardedSuggestions
				.add(new ElementRecommendation(discardedRecommendation.getContents(),
					persistenceManager.getKnowledgeElement(discardedRecommendation.getOriginId(),
						discardedRecommendation.getOriginDocumentationLocation())));
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

	public static DiscardedRecommendationInDatabase[] getDiscardedElementRecommendation(ElementRecommendation recommendation) {
		DiscardedRecommendationInDatabase[] discardedRecommendationsInDatabase = ACTIVE_OBJECTS.find(
			DiscardedRecommendationInDatabase.class,
			Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND DISCARDED_ELEMENT_ID = ? AND TYPE = ?",
				recommendation.getSummary(), recommendation.getRecommendationType()));
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

	public static long saveDiscardedElementRecommendation(ElementRecommendation recommendation) {
		if (recommendation.getSummary() == null) {
			return -1;
		}

		DiscardedRecommendationInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedElementRecommendation(
			recommendation);
		if (discardedLinkSuggestionsInDatabase.length > 0) {
			return discardedLinkSuggestionsInDatabase[0].getId();
		}

		// not null parameter and does not already exist -> create new
		final DiscardedRecommendationInDatabase discardedElementSuggestionInDatabase = ACTIVE_OBJECTS
			.create(DiscardedRecommendationInDatabase.class);
		discardedElementSuggestionInDatabase.setContents(recommendation.getSummary());
		discardedElementSuggestionInDatabase.setType(RecommendationType.EXTERNAL);
		discardedElementSuggestionInDatabase.save();
		return discardedElementSuggestionInDatabase.getId();
	}
}
