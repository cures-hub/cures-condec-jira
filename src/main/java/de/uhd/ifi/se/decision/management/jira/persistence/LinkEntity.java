package de.uhd.ifi.se.decision.management.jira.persistence;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for links between knowledge elements used in the active object
 * persistence strategy
 * @see Link
 */
@Table("LINK")
public interface LinkEntity extends RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	long getLinkId();

	void setLinkId(long linkId);

	String getLinkType();

	void setLinkType(String type);

	long getIdOfSourceElement();

	void setIdOfSourceElement(long id);

	long getIdOfDestinationElement();

	void setIdOfDestinationElement(long id);
}