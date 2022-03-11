package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
import java.sql.SQLException;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for discarded {@link Recommendation}s, i.e. for
 * {@link LinkRecommendation}s or {@link ElementRecommendation}s. Determines
 * which table columns are used for their storage in the database.
 */
@Table("CondecDiscRec")
public interface DiscardedRecommendationInDatabase extends RawEntity<Long> {

	/**
	 * @return Id of the database entry for the discarded {@link Recommendation} (primary key).
	 */
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	/**
	 * @param id Id of the database entry for the discarded {@link Recommendation} (primary key).
	 */
	void setId(long id);

	/**
	 * @return Id of the {@link KnowledgeElement} for which the discarded {@link Recommendation} was given.
	 *         For {@link LinkRecommendation}s that is the Element to which the link was suggested, for
	 *         {@link ElementRecommendation}s that is the Decision Issue to which the solution option was
	 *         suggested.
	 */
	long getOriginId();

	/**
	 * @param key Id of the {@link KnowledgeElement} for which the discarded {@link Recommendation} was given.
	 *         For {@link LinkRecommendation}s that is the Element to which the link was suggested, for
	 *         {@link ElementRecommendation}s that is the Decision Issue to which the solution option was
	 *         suggested.
	 */
	void setOriginId(long key);

	/**
	 * @return For discarded {@link LinkRecommendation}s the ID of the discarded Element.
	 */
	long getDiscardedElementId();

	/**
	 * @param key For discarded {@link LinkRecommendation}s the ID of the discarded Element.
	 */
	void setDiscardedElementId(long key);

	/**
	 * @return Key of the {@link DecisionKnowledgeProject} in which the recommendation has been discarded.
	 */
	String getProjectKey();

	/**
	 * @param key Key of the {@link DecisionKnowledgeProject} in which the recommendation has been discarded.
	 */
	void setProjectKey(String key);

	/**
	 * @return {@link DocumentationLocation} of the {@link KnowledgeElement} for which the discarded
	 *         {@link Recommendation} had been suggested.
	 */
	String getOriginDocumentationLocation();

	/**
	 * @param locationIdentifier {@link DocumentationLocation} of the {@link KnowledgeElement} for which the discarded
	 *                           {@link Recommendation} had been suggested.
	 */
	void setOriginDocumentationLocation(String locationIdentifier);

	/**
	 * @return For {@link LinkRecommendation}s the {@link DocumentationLocation} of the discarded Element.
	 */
	String getDiscElDocumentationLocation();

	/**
	 * @param locationIdentifier For {@link LinkRecommendation}s the {@link DocumentationLocation} of the discarded Element.
	 */
	void setDiscElDocumentationLocation(String locationIdentifier);

	/**
	 * @return {@link RecommendationType} of the discarded {@link Recommendation}.
	 */
	RecommendationType getType();

	/**
	 * @param type {@link RecommendationType} of the discarded {@link Recommendation}.
	 */
	void setType(RecommendationType type);

	/**
	 * @return summary of the discarded {@link ElementRecommendation}.
	 */
	String getSummary();

	/**
	 * @param summary of the discarded {@link ElementRecommendation} to be stored.
	 */
	void setSummary(String summary);

	/**
	 * Delete the {@link DiscardedRecommendationInDatabase} object, i.e., remove
	 * it from database.
	 *
	 * @param discardedRecommendationToDelete
	 *            {@link DiscardedRecommendationInDatabase} object.
	 * @return true if deletion was successful, false otherwise.
	 */
	static boolean deleteDiscardedRecommendation(DiscardedRecommendationInDatabase discardedRecommendationToDelete) {
		boolean deletionSuccessful;
		try {
			discardedRecommendationToDelete.getEntityManager().delete(discardedRecommendationToDelete);
			deletionSuccessful = true;
		} catch (SQLException e) {
			deletionSuccessful = false;
		}
		return deletionSuccessful;
	}
}
