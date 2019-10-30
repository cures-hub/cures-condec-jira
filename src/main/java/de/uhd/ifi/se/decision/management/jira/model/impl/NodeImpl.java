package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.Objects;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.Node;

public class NodeImpl implements Node {
	protected long id;
	protected DocumentationLocation documentationLocation;
	protected DecisionKnowledgeProject project;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public DocumentationLocation getDocumentationLocation() {
		if (documentationLocation == null) {
			return DocumentationLocation.UNKNOWN;
		}
		return documentationLocation;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (!(object instanceof Node)) {
			return false;
		}
		Node node = (Node) object;
		return this.id == node.getId() && this.getDocumentationLocation() == node.getDocumentationLocation();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, getDocumentationLocation());
	}

	@Override
	public String toString() {
		return getDocumentationLocation().getIdentifier() + id;
	}
}
