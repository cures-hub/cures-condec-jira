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
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import net.java.ao.Query;

/**
 * Responsible for the persistence of discarded
 * {@link
 * de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation
 * }s.
 *
 * @see DiscardedRecommendationInDatabase
 */
public final class DiscardedRecommendationPersistenceManager {

    /**
     * Active objects in Jira.
     */
    public static final ActiveObjects ACTIVE_OBJECTS =
            ComponentGetter.getActiveObjects();

    /**
     * This class with only static methods and fields should not be
     * instantiated.
     */
    private DiscardedRecommendationPersistenceManager() {
    }

    /**
     * @param baseElement {@link KnowledgeElement} with discarded link
     *                    recommendations.
     * @return list of discarded link recommendations for the base element.
     */
    public static List<KnowledgeElement> getDiscardedLinkRecommendations(final KnowledgeElement baseElement) {
        List<KnowledgeElement> discardedElements;
        if (baseElement == null || baseElement.getProject() == null) {
            discardedElements = new ArrayList<>();
        } else {
            discardedElements = getDiscardedRecommendations(baseElement,
                    RecommendationType.LINK);
            discardedElements.addAll(getDiscardedRecommendations(baseElement,
                    RecommendationType.DUPLICATE));

        }
        return discardedElements;
    }

    // ------------------
    // General Suggestion
    // ------------------

    private static List<KnowledgeElement> getDiscardedRecommendations(final KnowledgeElement origin, final RecommendationType type) {
        final List<KnowledgeElement> discardedSuggestions = new ArrayList<>();
        final Optional<DiscardedRecommendationInDatabase[]> discardedLinkSuggestions = Optional.ofNullable(ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class, Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND TYPE = ?", origin.getProject().getProjectKey(), origin.getId(), type)));
        final KnowledgePersistenceManager persistenceManager =
                KnowledgePersistenceManager.getInstance(origin.getProject().getProjectKey());

        for (final DiscardedRecommendationInDatabase discardedLinkSuggestion
                :
                discardedLinkSuggestions.orElseGet(() -> new DiscardedRecommendationInDatabase[0])) {
            discardedSuggestions.add(persistenceManager.getKnowledgeElement(discardedLinkSuggestion.getDiscardedElementId(), discardedLinkSuggestion.getDiscElDocumentationLocation()));
        }
        return discardedSuggestions;
    }

    /**
     * @param origin {@link KnowledgeElement} for which the discarded
     *               recommendations
     *               should be accessed.
     * @return Discarded decision guidance {@link ElementRecommendation}s for
     * the
     * given origin.
     */
    public static List<ElementRecommendation> getDiscardedDecisionGuidanceRecommendations(final KnowledgeElement origin) {
        final List<ElementRecommendation> discardedSuggestions =
                new ArrayList<>();
        if (origin != null && origin.getProject() != null) {
            final Optional<DiscardedRecommendationInDatabase[]> discardedRecommendations = Optional.ofNullable(ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class, Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND TYPE = ?", origin.getProject().getProjectKey(), origin.getId(), RecommendationType.EXTERNAL)));
            final KnowledgePersistenceManager persistenceManager =
                    KnowledgePersistenceManager.getInstance(origin.getProject().getProjectKey());

            for (final DiscardedRecommendationInDatabase discardedRecommendation : discardedRecommendations.orElseGet(() -> new DiscardedRecommendationInDatabase[0])) {
                discardedSuggestions.add(new ElementRecommendation(discardedRecommendation.getContents(), persistenceManager.getKnowledgeElement(discardedRecommendation.getOriginId(), discardedRecommendation.getOriginDocumentationLocation())));
            }
        }
        return discardedSuggestions;
    }

    /**
     * Get the database entry for a discarded {@link LinkRecommendation}.
     *
     * @param recommendation The recommendation for which the database entry
     *                       should be returned.
     * @return Database entry for the given recommendation.
     */
    public static DiscardedRecommendationInDatabase[] getDiscardedRecommendation(final LinkRecommendation recommendation) {
        return ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class,
                Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND " + "DISCARDED_ELEMENT_ID = ? AND TYPE = ?", recommendation.getSource().getProject().getProjectKey(), recommendation.getSource().getId(), recommendation.getTarget().getId(), recommendation.getRecommendationType()));
    }

    /**
     * Get the database entry for a discarded {@link ElementRecommendation}.
     *
     * @param recommendation The recommendation for which the database entry
     *                       should be returned.
     * @return Database entry for the given recommendation.
     */
    public static DiscardedRecommendationInDatabase[] getDiscardedElementRecommendation(final ElementRecommendation recommendation) {
        return ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class,
                Query.select().where("CONTENTS = ? AND ORIGIN_ID = ? AND TYPE" +
                        " " + "= ?", recommendation.getSummary(),
                        recommendation.getTarget(),
                        RecommendationType.EXTERNAL));
    }

    /**
     * Remove the database entry/-ies for a given {@link LinkRecommendation}
     * from the database.
     *
     * @param recommendation The recommendation of which the database
     *                       entry/-ies should be removed.
     * @return true, if at least one database entry has been removed,
     * otherwise false.
     */
    public static boolean removeDiscardedRecommendation(final LinkRecommendation recommendation) {
        boolean isRemoved = false;
        final DiscardedRecommendationInDatabase[] discardedRecommendationsInDatabase = getDiscardedRecommendation(recommendation);
        for (final DiscardedRecommendationInDatabase discardedRecommendationInDatabase : discardedRecommendationsInDatabase) {
            DiscardedRecommendationInDatabase.deleteDiscardedRecommendation(discardedRecommendationInDatabase);
            isRemoved = true;
        }
        return isRemoved;
    }

    /**
     * Save a discarded {@link LinkRecommendation} in the database.
     *
     * @param recommendation The recommendation to be saved in the database.
     * @return -1 if the recommendation could not be saved, otherwise the ID
     * of the newly created or, in case there already was one, existing
     * entry in the database.
     */
    public static long saveDiscardedRecommendation(final LinkRecommendation recommendation) {
        long idInDatabase = -1;
        if (recommendation.getSource() != null && recommendation.getSource().getProject() != null && recommendation.getTarget() != null) {
            final DiscardedRecommendationInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedRecommendation(recommendation);
            if (discardedLinkSuggestionsInDatabase.length > 0) {
                idInDatabase = discardedLinkSuggestionsInDatabase[0].getId();
            } else {
                // not null parameter and does not already exist -> create new
                final DiscardedRecommendationInDatabase discardedLinkSuggestionInDatabase = ACTIVE_OBJECTS.create(DiscardedRecommendationInDatabase.class);
                discardedLinkSuggestionInDatabase.setOriginId(recommendation.getSource().getId());
                discardedLinkSuggestionInDatabase.setDiscardedElementId(recommendation.getTarget().getId());
                discardedLinkSuggestionInDatabase.setType(recommendation.getRecommendationType());
                discardedLinkSuggestionInDatabase.setProjectKey(recommendation.getSource().getProject().getProjectKey());
                discardedLinkSuggestionInDatabase.setOriginDocumentationLocation(recommendation.getSource().getDocumentationLocationAsString());
                discardedLinkSuggestionInDatabase.setDiscElDocumentationLocation(recommendation.getTarget().getDocumentationLocationAsString());

                discardedLinkSuggestionInDatabase.save();
                idInDatabase = discardedLinkSuggestionInDatabase.getId();
            }
        }
        return idInDatabase;
    }

    /**
     * Save a discarded {@link ElementRecommendation} in the database.
     *
     * @param recommendation The recommendation to be saved in the database.
     * @return -1 if the recommendation could not be saved, otherwise the ID
     * of the newly created or, in case there already was one, existing
     * entry in the database.
     */
    public static long saveDiscardedElementRecommendation(final ElementRecommendation recommendation) {
        long idInDatabase = -1;
        if (recommendation.getSummary() != null) {
            final DiscardedRecommendationInDatabase[] discardedSuggestionsInDatabase = getDiscardedElementRecommendation(recommendation);
            if (discardedSuggestionsInDatabase.length > 0) {
                idInDatabase = discardedSuggestionsInDatabase[0].getId();
            } else {
                // not null parameter and does not already exist -> create new
                final DiscardedRecommendationInDatabase discardedElementSuggestionInDatabase = ACTIVE_OBJECTS.create(DiscardedRecommendationInDatabase.class);
                discardedElementSuggestionInDatabase.setContents(recommendation.getSummary());
                discardedElementSuggestionInDatabase.setType(RecommendationType.EXTERNAL);
                discardedElementSuggestionInDatabase.save();
                idInDatabase = discardedElementSuggestionInDatabase.getId();
            }
        }
        return idInDatabase;
    }
}
