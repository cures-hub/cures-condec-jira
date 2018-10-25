package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.GenericLinkImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import net.java.ao.schema.Ignore;

/**
 * Interface for links between knowledge elements. The links are directed, i.e.,
 * they are arrows starting from a source element and ending in a destination element.
 */
@JsonDeserialize(as = GenericLinkImpl.class)
public interface GenericLink extends Link {

	long getId();

	void setId(long id);

	String getType();

	void setType(String type);
	
	String getIdOfSourceElement();
	
	void setIdOfSourceElement(String idOfSourceElement);
	
	String getIdOfDestinationElement();
	
	void setIdOfDestinationElement(String idOfDestinationElement);

	DecisionKnowledgeElement getOpposite(String id);

	List<DecisionKnowledgeElement> getBothElements();
	
	@Ignore
	boolean isValid();
	
	@Ignore
	boolean isInterProjectLink();
}