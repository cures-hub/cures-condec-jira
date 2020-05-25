package de.uhd.ifi.se.decision.management.jira.persistence.tables;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("ConsConfig")
public interface ConsistencyEventListenerInDatabase extends RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);


}
