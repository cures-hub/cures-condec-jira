package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.recommendation.Recommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationType;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.ElementRecommendation;
import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.LinkRecommendation;
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

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	long getOriginId();

	void setOriginId(long key);

	long getDiscardedElementId();

	void setDiscardedElementId(long key);

	String getProjectKey();

	void setProjectKey(String key);

	String getOriginDocumentationLocation();

	void setOriginDocumentationLocation(String locationIdentifier);

	String getDiscElDocumentationLocation();

	void setDiscElDocumentationLocation(String locationIdentifier);

	RecommendationType getType();

	void setType(RecommendationType type);

	String getContents();

	void setContents(String contents);

	/**
	 * Deletes the {@link DiscardedRecommendationInDatabase} object, i.e., removes
	 * it from database.
	 * 
	 * @param discardedRecommendationToDelete
	 *            {@link DiscardedRecommendationInDatabase} object.
	 * @return true if deletion was successful, false otherwise.
	 */
	static boolean deleteDiscardedRecommendation(DiscardedRecommendationInDatabase discardedRecommendationToDelete) {
		try {
			discardedRecommendationToDelete.getEntityManager().delete(discardedRecommendationToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
