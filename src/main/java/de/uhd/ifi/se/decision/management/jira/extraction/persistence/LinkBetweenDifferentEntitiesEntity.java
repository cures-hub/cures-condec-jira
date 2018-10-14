package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("GenericLink")
public interface LinkBetweenDifferentEntitiesEntity extends RawEntity<Integer> {
	
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	String getType();

	void setType(String type);

	String getIdOfSourceElement();

	void setIdOfSourceElement(String id);

	String getIdOfDestinationElement();

	void setIdOfDestinationElement(String id);
	
}
