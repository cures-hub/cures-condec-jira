package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

import java.sql.SQLException;

@Table("ConsChecks")
public interface ConsistencyCheckLogsInDatabase extends RawEntity<Long> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	long getKnowledgeId();

	void setKnowledgeId(long id);

	String getLocation();

	void setLocation(String location);

	String getProjectKey();

	void setProjectKey(String projectKey);

	String getApprover();

	void setApprover(String userId);


	static boolean deleteElement(ConsistencyCheckLogsInDatabase elementToDelete) {
		try {
			elementToDelete.getEntityManager().delete(elementToDelete);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

}
