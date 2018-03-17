package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * @description Interface for links between knowledge elements used in the
 *              active object persistence strategy
 */
@Table("LINK")
public interface ILinkEntity extends ILink, RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	public long getId();
}