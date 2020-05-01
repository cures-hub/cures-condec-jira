package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

import java.sql.SQLException;
import java.util.List;

@Table("CondecConsistency")
public interface DiscardedLinkSuggestionsInDatabase extends RawEntity<Long> {
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	String getOriginIssueKey();

	void setOriginIssueKey(String key);

	String getDiscardedIssueKey();

	void setDiscardedIssueKey(String key);

	String getProjectKey();

	void setProjectKey(String key);

	static boolean deleteDiscardedLink(DiscardedLinkSuggestionsInDatabase elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
