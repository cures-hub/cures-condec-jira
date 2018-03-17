package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * @description Interface for decision knowledge elements used in the
 *              active object persistence strategy
 */
@Table("DECISION")
public interface IDecisionKnowledgeElementEntity extends IDecisionKnowledgeElement, RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	public long getId();
}