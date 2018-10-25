package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;

/**
 * Model class for a graph of decision knowledge elements (no filter-criteria
 * given)
 */
@JsonAutoDetect
public class GraphImpl implements Graph {

	private DecisionKnowledgeElement rootElement;
	private DecisionKnowledgeProject project;
	private List<Long> linkIds;
	private List<Long> genericLinkIds;

	public GraphImpl() {
		linkIds = new ArrayList<Long>();
		genericLinkIds = new ArrayList<Long>();
	}

	public GraphImpl(String projectKey) {
		this();
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
	}

	public GraphImpl(DecisionKnowledgeElement rootElement) {
		this(rootElement.getProject().getProjectKey());
		this.rootElement = rootElement;
	}

	public GraphImpl(String projectKey, String rootElementKey) {
		this(projectKey);
		// Support keys of decision knowledge elements documented in comments
		String issueKey = getJiraIssueKey(rootElementKey);
		this.rootElement = this.project.getPersistenceStrategy().getDecisionKnowledgeElement(issueKey);
	}

	private static String getJiraIssueKey(String rootElementKey) {
		if (rootElementKey.contains(":")) {
			return rootElementKey.substring(0, rootElementKey.indexOf(":"));
		}
		return rootElementKey;
	}

	@Override
	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = this.getLinkedElementsAndLinks(element);
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>(
				linkedElementsAndLinks.keySet());
		return linkedElements;
	}

	@Override
	public Map<DecisionKnowledgeElement, Link> getLinkedElementsAndLinks(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();
		linkedElementsAndLinks.putAll(this.getElementsLinkedWithOutwardLinks(element));
		linkedElementsAndLinks.putAll(this.getElementsLinkedWithInwardLinks(element));
		linkedElementsAndLinks.putAll(this.getAllLinkedSentences(element));
		return linkedElementsAndLinks;
	}

	private Map<DecisionKnowledgeElement, Link> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}

		List<Link> outwardLinks = this.project.getPersistenceStrategy().getOutwardLinks(element);
		for (Link link : outwardLinks) {
			if (!linkIds.contains(link.getId())) {
				DecisionKnowledgeElement outwardElement = link.getDestinationElement();
				if (outwardElement != null) {
					linkIds.add(link.getId());
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
			if (!linkIds.contains(link.getId())) {
				DecisionKnowledgeElement inwardElement = link.getSourceElement();
				if (inwardElement != null) {
					linkIds.add(link.getId());
					linkedElementsAndLinks.put(inwardElement, link);
				}
			}
		}
		return linkedElementsAndLinks;
	}

	private Map<DecisionKnowledgeElement, Link> getAllLinkedSentences(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}

		String prefix = getIdentifier(element);
		List<GenericLink> links = GenericLinkManager.getGenericLinksForElement(prefix + element.getId(), false);

		for (GenericLink currentLink : links) {
			try {
				DecisionKnowledgeElement source = currentLink.getBothElements().get(0);
				DecisionKnowledgeElement target = currentLink.getBothElements().get(1);
				if (!source.getProject().getProjectKey().equals(target.getProject().getProjectKey())) {
					continue;
				}
				currentLink.setType("contain");
				if (!this.genericLinkIds.contains(currentLink.getId())) {
					this.genericLinkIds.add(currentLink.getId());
					linkedElementsAndLinks.put(currentLink.getOpposite(prefix + element.getId()),
							currentLink);
				}
			} catch (NullPointerException e) {
				// Link in the wrong direction
				continue;
			}
		}
		return linkedElementsAndLinks;
	}

	private String getIdentifier(DecisionKnowledgeElement element) {
		if (element instanceof Sentence) {
			return "s";
		} else {
			return "i";
		}
	}

	@Override
	public List<DecisionKnowledgeElement> getAllElements() {
		List<DecisionKnowledgeElement> allElements = new ArrayList<DecisionKnowledgeElement>();
		allElements.add(this.getRootElement());
		allElements.addAll(this.getLinkedElements(this.getRootElement()));
		ListIterator<DecisionKnowledgeElement> iterator = allElements.listIterator();
		while (iterator.hasNext()) {
			List<DecisionKnowledgeElement> linkedElements = this.getLinkedElements(iterator.next());
			for (DecisionKnowledgeElement element : linkedElements) {
				if (!IteratorUtils.toList(iterator).contains(element)) {
					iterator.add(element);
				}
			}
		}
		return allElements;
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