package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

import java.sql.SQLException;

@Table("CondecStatus")
public interface KnowledgeStatusInDatabase extends RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	String getStatus();

	void setStatus(String status);

	long getElementId();

	void setElementId(long id);

	String getDocumentationLocation();

	void setDocumentationLocation(String documentationLocation);

	static boolean deleteStatus(KnowledgeStatusInDatabase elementInDatabase) {
		try {
			elementInDatabase.getEntityManager().delete(elementInDatabase);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
}
