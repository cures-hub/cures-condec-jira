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
 * Model class for a graph of decision knowledge elements
 */
@JsonAutoDetect
public class GraphImpl implements Graph {

	private DecisionKnowledgeElement rootElement;
	private List<Long> linkIds;
	private DecisionKnowledgeProject project;
	private static List<Link> sentenceLinkAlreadyVisited;

	public GraphImpl() {
		linkIds = new ArrayList<>();
		sentenceLinkAlreadyVisited = new ArrayList<>();
	}

	public GraphImpl(String projectKey) {
		this();
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
	}

	public GraphImpl(String projectKey, String rootElementKey) {
		this(projectKey);
		// Support element keys that represent sentences in comments
		String cleanRootElementKey = getRootElementKeyWithoutDoubleDot(rootElementKey);
		this.rootElement = this.project.getPersistenceStrategy().getDecisionKnowledgeElement(cleanRootElementKey);
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
		linkedElementsAndLinks.putAll(this.getAllLinkedSentences(element));
		return linkedElementsAndLinks;
	}

	private String getRootElementKeyWithoutDoubleDot(String rootElementKey) {
		String returnedRootElementKey;
		if (rootElementKey.contains(":")) {
			returnedRootElementKey = rootElementKey.substring(0, rootElementKey.indexOf(":"));
		} else {
			returnedRootElementKey = rootElementKey;
		}
		return returnedRootElementKey;
	}

	private Map<DecisionKnowledgeElement, Link> getAllLinkedSentences(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}
		String preIndex = getIdentifier(element);
		List<GenericLink> list = GenericLinkManager.getGenericLinksForElement(preIndex + element.getId(), false);

		for (GenericLink currentGenericLink : list) {
			try {
				DecisionKnowledgeElement source = currentGenericLink.getBothElements().get(0);
				DecisionKnowledgeElement target = currentGenericLink.getBothElements().get(1);
				if (!source.getProject().getProjectKey().equals(target.getProject().getProjectKey())) {
					continue;
				}
				Link linkBetweenSentenceAndOtherElement = new LinkImpl(source, target);
				linkBetweenSentenceAndOtherElement.setType("contain");
				if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
					GraphImpl.sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
					linkedElementsAndLinks.put(currentGenericLink.getOpposite(preIndex + element.getId()),
							linkBetweenSentenceAndOtherElement);
				}
			} catch (NullPointerException e) {
				// Link in the wrong direction
				continue;
			}
		}
		return linkedElementsAndLinks;
	}

	private boolean linkListContainsLink(Link link2) {
		for (Link link : GraphImpl.sentenceLinkAlreadyVisited) {
			if (link.getDestinationElement().getId() == link2.getDestinationElement().getId()
					&& link.getSourceElement().getId() == link2.getSourceElement().getId()
					|| link.getSourceElement().getId() == link2.getDestinationElement().getId()
							&& link.getSourceElement().getId() == link2.getDestinationElement().getId()) {
				return true;
			}
		}
		return false;
	}

	private String getIdentifier(DecisionKnowledgeElement element) {
		if (element instanceof Sentence) {
			return "s";
		} else {
			return "i";
		}
	}

	@Override
	public List<DecisionKnowledgeElement> getLinkedElements(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = this.getLinkedElementsAndLinks(element);
		List<DecisionKnowledgeElement> linkedElements = new ArrayList<DecisionKnowledgeElement>(
				linkedElementsAndLinks.keySet());
		return linkedElements;
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