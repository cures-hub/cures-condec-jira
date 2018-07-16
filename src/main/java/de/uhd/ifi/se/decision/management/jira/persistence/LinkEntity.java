package de.uhd.ifi.se.decision.management.jira.persistence;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
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

	long getIdOfSourceElement();

	void setIdOfSourceElement(long id);

	long getIdOfDestinationElement();

	void setIdOfDestinationElement(long id);

	@Ignore
	DecisionKnowledgeElement getDestinationElement();

	@Ignore
	DecisionKnowledgeElement getSourceElement();

	@Ignore
	void setDestinationElement(DecisionKnowledgeElement decisionKnowledgeElement);

	@Ignore
	void setSourceElement(DecisionKnowledgeElement decisionKnowledgeElement);
}