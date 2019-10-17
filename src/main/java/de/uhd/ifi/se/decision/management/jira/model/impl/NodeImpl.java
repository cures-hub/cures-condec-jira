package de.uhd.ifi.se.decision.management.jira.model.impl;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Node;

public class NodeImpl implements Node {
	protected long id;
	protected DocumentationLocation documentationLocation;
	protected DecisionKnowledgeProject project;

	public long getId() {
		return id;
	}

	@Override
	public DocumentationLocation getDocumentationLocation() {
		return documentationLocation;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (!(o instanceof Node)) {
			return false;
		}
		Node node = (Node) o;
		return this.id == node.getId();
	}
}
