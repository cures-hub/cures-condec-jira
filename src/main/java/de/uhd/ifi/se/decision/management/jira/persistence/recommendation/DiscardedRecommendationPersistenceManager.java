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
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import net.java.ao.Query;
import org.apache.jena.base.Sys;

/**
 * Responsible for the persistence of discarded {@link Recommendation}s.
 *
 * @see DiscardedRecommendationInDatabase
 */
public final class DiscardedRecommendationPersistenceManager {

    /**
     * Manager for active objects in Jira.
     */
    public static final ActiveObjects ACTIVE_OBJECTS =
            ComponentGetter.getActiveObjects();

    /**
     * @param baseElement {@link KnowledgeElement} with discarded link recommendations.
     * @return list of discarded link recommendations for the base element.
     */
    public static List<KnowledgeElement> getDiscardedLinkRecommendations(KnowledgeElement baseElement) {
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

    private static List<KnowledgeElement> getDiscardedRecommendations(KnowledgeElement origin, RecommendationType type) {
        List<KnowledgeElement> discardedSuggestions = new ArrayList<>();
        Optional<DiscardedRecommendationInDatabase[]> discardedLinkSuggestions = Optional.ofNullable(ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class, Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND TYPE = ?", origin.getProject().getProjectKey(), origin.getId(), type)));
        KnowledgePersistenceManager persistenceManager =
                KnowledgePersistenceManager.getInstance(origin.getProject().getProjectKey());

        for (DiscardedRecommendationInDatabase discardedLinkSuggestion
                :
                discardedLinkSuggestions.orElseGet(() -> new DiscardedRecommendationInDatabase[0])) {
            discardedSuggestions.add(persistenceManager.getKnowledgeElement(discardedLinkSuggestion.getDiscardedElementId(), discardedLinkSuggestion.getDiscElDocumentationLocation()));
        }
        return discardedSuggestions;
    }

    /**
     * @param origin {@link KnowledgeElement} for which the discarded recommendations should be accessed.
     * @return Discarded decision guidance {@link ElementRecommendation}s for the given origin.
     */
    public static List<ElementRecommendation> getDiscardedDecisionGuidanceRecommendations(KnowledgeElement origin) {
        List<ElementRecommendation> discardedSuggestions =
                new ArrayList<>();
        if (origin != null && origin.getProject() != null) {
            Optional<DiscardedRecommendationInDatabase[]> discardedRecommendations = Optional.ofNullable(ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class,
                    Query.select().where("TARGET_KEY = ? AND TYPE = ?", origin.getKey(), RecommendationType.EXTERNAL)));
            KnowledgePersistenceManager persistenceManager =
                    KnowledgePersistenceManager.getInstance(origin.getProject().getProjectKey());

            for (DiscardedRecommendationInDatabase discardedRecommendation : discardedRecommendations.orElseGet(() -> new DiscardedRecommendationInDatabase[0])) {
                discardedSuggestions.add(new ElementRecommendation(discardedRecommendation.getSummary(),
                        persistenceManager.getKnowledgeElement(discardedRecommendation.getOriginId(), discardedRecommendation.getOriginDocumentationLocation())));
            }
        }
        return discardedSuggestions;
    }

    /**
     * @param recommendation The recommendation for which the database entry should be returned.
     * @return Database entry for the given discarded {@link LinkRecommendation}.
     */
    public static DiscardedRecommendationInDatabase[] getDiscardedRecommendation(LinkRecommendation recommendation) {
        return ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class,
                Query.select().where("PROJECT_KEY = ? AND ORIGIN_ID = ? AND " + "DISCARDED_ELEMENT_ID = ? AND TYPE = ?", recommendation.getSource().getProject().getProjectKey(), recommendation.getSource().getId(), recommendation.getTarget().getId(), recommendation.getRecommendationType()));
    }

    /**
     * @param recommendation The recommendation for which the database entry should be returned.
     * @return Database entry for the given discarded {@link ElementRecommendation}.
     */
    public static DiscardedRecommendationInDatabase[] getDiscardedElementRecommendation(ElementRecommendation recommendation) {
        return ACTIVE_OBJECTS.find(DiscardedRecommendationInDatabase.class,
                Query.select().where("SUMMARY = ? AND TARGET_KEY = ? AND TYPE" +
                        " " + "= ?", recommendation.getSummary(),
                        recommendation.getTarget().getKey(),
                        RecommendationType.EXTERNAL));
    }

    /**
     * Remove the database entry for a given {@link ElementRecommendation} from the database.
     *
     * @param recommendation The recommendation of which the database entry should be removed.
     * @return true if a database entry has been removed, otherwise false.
     */
    public static boolean removeDiscardedElementRecommendation(ElementRecommendation recommendation) {
        boolean isRemoved = false;
        DiscardedRecommendationInDatabase[] discardedRecommendationsInDatabase = getDiscardedElementRecommendation(recommendation);
        if (discardedRecommendationsInDatabase.length > 0) {
            DiscardedRecommendationInDatabase.deleteDiscardedRecommendation(discardedRecommendationsInDatabase[0]);
            isRemoved = true;
        }
        return isRemoved;
    }

    /**
     * Remove the database entry for a given {@link LinkRecommendation} from the database.
     *
     * @param recommendation The recommendation of which the database entry should be removed.
     * @return true if a database entry has been removed, otherwise false.
     */
    public static boolean removeDiscardedRecommendation(LinkRecommendation recommendation) {
        boolean isRemoved = false;
        DiscardedRecommendationInDatabase[] discardedRecommendationsInDatabase = getDiscardedRecommendation(recommendation);
        if (discardedRecommendationsInDatabase.length > 0) {
            DiscardedRecommendationInDatabase.deleteDiscardedRecommendation(discardedRecommendationsInDatabase[0]);
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
    public static long saveDiscardedRecommendation(LinkRecommendation recommendation) {
        long idInDatabase = -1;
        if (recommendation.getSource() != null && recommendation.getSource().getProject() != null && recommendation.getTarget() != null) {
            DiscardedRecommendationInDatabase[] discardedLinkSuggestionsInDatabase = getDiscardedRecommendation(recommendation);
            if (discardedLinkSuggestionsInDatabase.length > 0) {
                idInDatabase = discardedLinkSuggestionsInDatabase[0].getId();
            } else {
                // not null parameter and does not already exist -> create new
                DiscardedRecommendationInDatabase discardedLinkSuggestionInDatabase = ACTIVE_OBJECTS.create(DiscardedRecommendationInDatabase.class);
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
    public static long saveDiscardedElementRecommendation(ElementRecommendation recommendation) {
        System.out.print("recommendation: ");
        System.out.println(recommendation);
        System.out.print("recommendation.target: ");
        System.out.println(recommendation.getTarget());
        System.out.print("recommendation.target.getProject(): ");
        System.out.println(recommendation.getTarget().getProject());
        System.out.print("recommendation.target.getKey(): ");
        System.out.println(recommendation.getTarget().getKey());
        System.out.print("recommendation.getProject(): ");
        System.out.println(recommendation.getProject());
        long idInDatabase = -1;
        if (recommendation.getSummary() != null) {
            DiscardedRecommendationInDatabase[] discardedSuggestionsInDatabase = getDiscardedElementRecommendation(recommendation);
            if (discardedSuggestionsInDatabase.length > 0) {
                idInDatabase = discardedSuggestionsInDatabase[0].getId();
            } else {
                // not null parameter and does not already exist -> create new
                DiscardedRecommendationInDatabase discardedElementSuggestionInDatabase = ACTIVE_OBJECTS.create(DiscardedRecommendationInDatabase.class);
                discardedElementSuggestionInDatabase.setSummary(recommendation.getSummary());
                discardedElementSuggestionInDatabase.setType(RecommendationType.EXTERNAL);
                discardedElementSuggestionInDatabase.setTargetKey(recommendation.getTarget().getKey());
                discardedElementSuggestionInDatabase.save();
                idInDatabase = discardedElementSuggestionInDatabase.getId();
            }
        }
        return idInDatabase;
    }
}
