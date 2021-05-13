package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import java.sql.SQLException;

import de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.suggestions.SuggestionType;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

//TODO Rename to start with "Condec" to enable easy recognition of database tables by system admin
@Table("DiscSug")
public interface DiscardedSuggestionInDatabase extends RawEntity<Long> {

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

	SuggestionType getType();

	void setType(SuggestionType type);

	static boolean deleteDiscardedDuplicate(DiscardedSuggestionInDatabase elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
