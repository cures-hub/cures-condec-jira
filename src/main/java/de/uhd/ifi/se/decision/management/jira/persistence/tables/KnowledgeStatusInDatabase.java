package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("CondecType")
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
}
