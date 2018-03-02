package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * @author Ewald Rode
 * @description Model class for links between decision components
 */
@Table("LINK")
public interface ILinkEntity extends ILink, RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	public long getId();
}