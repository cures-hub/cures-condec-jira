package de.uhd.ifi.se.decision.management.jira.model.impl;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Node;

public class NodeImpl implements Node {
    protected long id;
    protected DocumentationLocation documentationLocation;
	protected DecisionKnowledgeProject project;

    public NodeImpl() {
    }

	public NodeImpl(DecisionKnowledgeElement element) {
		this.id = element.getId();
		this.documentationLocation = element.getDocumentationLocation();
		this.project = element.getProject();
	}

    public long getId() {
        return id;
    }

    @Override
    public DocumentationLocation getDocumentationLocation() {
        return documentationLocation;
    }
}
