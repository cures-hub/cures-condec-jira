package de.uhd.ifi.se.decision.documentation.jira.persistence;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * Interface for links between knowledge elements used in the active object
 * persistence strategy
 */
@Table("LINK")
public interface LinkEntity extends Link, RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	@Ignore
	DecisionKnowledgeElement getDestinationObject();

	@Ignore
	DecisionKnowledgeElement getSourceObject();

	@Ignore
	void setDestinationObject(DecisionKnowledgeElement decisionKnowledgeElement);

	@Ignore
	void setSourceObject(DecisionKnowledgeElement decisionKnowledgeElement);
}