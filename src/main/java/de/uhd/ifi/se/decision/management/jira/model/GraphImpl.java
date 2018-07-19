package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;

/**
 * Model class for a graph of decision knowledge elements
 */
@JsonAutoDetect
public class GraphImpl implements Graph {

	private DecisionKnowledgeElement rootElement;
	private List<Long> linkIds;
	private DecisionKnowledgeProject project;

	public GraphImpl() {
		linkIds = new ArrayList<>();
	}

	public GraphImpl(String projectKey) {
		this();
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
	}

	public GraphImpl(String projectKey, String rootElementKey) {
		this(projectKey);
		this.rootElement = this.project.getPersistenceStrategy().getDecisionKnowledgeElement(rootElementKey);
	}

	public GraphImpl(DecisionKnowledgeElement rootElement) {
		this(rootElement.getProject().getProjectKey());
		this.rootElement = rootElement;
	}

	@Override
	public Map<DecisionKnowledgeElement, Link> getLinkedElementsAndLinks(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();
		linkedElementsAndLinks.putAll(this.getElementsLinkedWithOutwardLinks(element));
		linkedElementsAndLinks.putAll(this.getElementsLinkedWithInwardLinks(element));
		return linkedElementsAndLinks;
	}

	@Override
	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = this.getLinkedElementsAndLinks(element);
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>(
				linkedElementsAndLinks.keySet());
		return linkedElements;
	}

	private Map<DecisionKnowledgeElement, Link> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}

		List<Link> outwardLinks = this.project.getPersistenceStrategy().getOutwardLinks(element);
		for (Link link : outwardLinks) {
			if (!linkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement outwardElement = link.getDestinationElement();
				if (outwardElement != null) {
					linkIds.add(link.getLinkId());
					linkedElementsAndLinks.put(outwardElement, link);
				}
			}
		}

		return linkedElementsAndLinks;
	}

	private Map<DecisionKnowledgeElement, Link> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}

		List<Link> inwardLinks = this.project.getPersistenceStrategy().getInwardLinks(element);
		for (Link link : inwardLinks) {
			if (!linkIds.contains(link.getLinkId())) {
				DecisionKnowledgeElement inwardElement = link.getSourceElement();
				if (inwardElement != null) {
					linkIds.add(link.getLinkId());
					linkedElementsAndLinks.put(inwardElement, link);
				}
			}
		}

		return linkedElementsAndLinks;
	}

	@Override
	public DecisionKnowledgeElement getRootElement() {
		return rootElement;
	}

	@Override
	public void setRootElement(DecisionKnowledgeElement rootElement) {
		this.rootElement = rootElement;
	}

	@Override
	public DecisionKnowledgeProject getProject() {
		return project;
	}

	@Override
	public void setProject(DecisionKnowledgeProject project) {
		this.project = project;
	}
}