package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("LinksInSentences")
public interface LinksInSentencesEntity extends RawEntity<Integer> {

	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	long getIdOfSourceElement();
	
	void setIdOfSourceElement(long id);
	
	long getIdOfDestinationElement();
	
	void setIdOfDestinationElement(long id);
	
	String getType();
	
	void setType(String type);

}
