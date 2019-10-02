package de.uhd.ifi.se.decision.management.jira.model.impl;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Node;

public class NodeImpl implements Node {
	private long id;
	private DocumentationLocation location;

	public NodeImpl(DecisionKnowledgeElement element){
		id = element.getId();
		location = element.getDocumentationLocation();
	}

	public long getId() {
		return id;
	}

	@Override
	public DocumentationLocation getDocumentationLocation() {
		return location;
	}
}
