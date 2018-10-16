package de.uhd.ifi.se.decision.management.jira.model;

import java.util.*;

import de.uhd.ifi.se.decision.management.jira.view.GraphFiltering;
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
	private List<DecisionKnowledgeElement> filteredElements;
	private boolean isFilteredByTime;
	private static List<Link> sentenceLinkAlreadyVisited;
	private long startTime;
	private long endTime;

	public GraphImpl() {
		linkIds = new ArrayList<>();
		sentenceLinkAlreadyVisited = new ArrayList<>();
		isFilteredByTime = false;
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

	public GraphImpl(String projectKey, String rootElementKey, GraphFiltering filter) {
		this(projectKey);
		String cleanRootElementKey = getRootElementKeyWithoutDoubleDot(rootElementKey);
		this.rootElement = this.project.getPersistenceStrategy().getDecisionKnowledgeElement(cleanRootElementKey);
		if (filter != null) {
			this.filteredElements = filter.getQueryResults();
			this.isFilteredByTime = filter.isQueryContainsCreationDate();
			this.startTime = filter.getStartDate();
			this.endTime = filter.getEndDate();
		}
	}

	@Override
	public Map<DecisionKnowledgeElement, Link> getLinkedElementsAndLinks(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (this.filteredElements == null) {
			linkedElementsAndLinks.putAll(this.getElementsLinkedWithOutwardLinks(element));
			linkedElementsAndLinks.putAll(this.getElementsLinkedWithInwardLinks(element));
		} else {
			linkedElementsAndLinks.putAll(this.getElementsLinkedWithInwardLinksFiltered(element));
			linkedElementsAndLinks.putAll(this.getElementsLinkedWithOutwardLinksFiltered(element));
		}
		linkedElementsAndLinks.putAll(this.getAllLinkedSentences(element));
		return linkedElementsAndLinks;
	}

	private String getRootElementKeyWithoutDoubleDot(String rootElementKey){
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
				if (isFilteredByTime) {
					if (startTime <= 0) {
						if (((Sentence) source).getCreated().getTime() < this.endTime) {
							DecisionKnowledgeElement toLink = currentGenericLink.getOpposite(preIndex + element.getId());
							if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
								sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
								linkedElementsAndLinks.put(toLink,
										linkBetweenSentenceAndOtherElement);
							}
						}
					} else if (endTime <= 0) {
						if (((Sentence) source).getCreated().getTime() > this.startTime) {
							DecisionKnowledgeElement toLink = currentGenericLink.getOpposite(preIndex + element.getId());
							if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
								sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
								linkedElementsAndLinks.put(toLink,
										linkBetweenSentenceAndOtherElement);
							}
						}
					} else {
						if ((((Sentence) source).getCreated().getTime() < this.endTime) && (((Sentence) source).getCreated().getTime() > this.startTime)) {
							DecisionKnowledgeElement toLink = currentGenericLink.getOpposite(preIndex + element.getId());
							if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
								sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
								linkedElementsAndLinks.put(toLink,
										linkBetweenSentenceAndOtherElement);
							}
						}
					}
				} else {
					if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
					GraphImpl.sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
					linkedElementsAndLinks.put(currentGenericLink.getOpposite(preIndex + element.getId()),
						linkBetweenSentenceAndOtherElement);
				}
				}

			} catch (NullPointerException e) {
				// Link in the wrong direction
				continue;
			}
		}
		return linkedElementsAndLinks;
	}

	private Map<DecisionKnowledgeElement,Link> linkElementsTransitivelyOverSentences(DecisionKnowledgeElement parentElement,
																				 DecisionKnowledgeElement filteredElement) {
		Map<DecisionKnowledgeElement,Link> result = new HashMap<>();
		List<DecisionKnowledgeElement> elementsMatchingFilter = new ArrayList<>();
		elementsMatchingFilter.addAll(getInwardTransitiveLinkedNodes(filteredElement));
		elementsMatchingFilter.addAll(getOutwardTransitiveLinkedNodes(filteredElement));
		for (DecisionKnowledgeElement element : elementsMatchingFilter) {
			Link transitiveLink = new LinkImpl(parentElement, element);
			transitiveLink.setType("contains");
			linkIds.add(transitiveLink.getId());
			result.put(element, transitiveLink);
		}
		return result;
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

	private Map<DecisionKnowledgeElement, Link> getElementsLinkedWithInwardLinksFiltered(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}

		List<Link> inwardLinks = this.project.getPersistenceStrategy().getInwardLinks(element);
		for (Link link : inwardLinks) {
			if (!linkIds.contains(link.getId())) {
				DecisionKnowledgeElement inwardElement = link.getSourceElement();
				if (inwardElement != null) {
					if (this.filteredElements.contains(inwardElement)) {
						linkIds.add(link.getId());
						linkedElementsAndLinks.put(inwardElement, link);
					} else {
						linkIds.add(link.getId());
//						List<DecisionKnowledgeElement> transitiveLinkedElements = getInwardTransitiveLinkedNodes(inwardElement);
//						for (DecisionKnowledgeElement element1 : transitiveLinkedElements) {
//							Link transitiveLink = new LinkImpl(element, element1);
//							transitiveLink.setType(link.getType());
//							linkIds.add(transitiveLink.getId());
//							linkedElementsAndLinks.put(element1, transitiveLink);
//						}
//						Map<DecisionKnowledgeElement,Link> sentencesLinkedToFilteredElement = getAllLinkedSentences(inwardElement);
//						Set<DecisionKnowledgeElement> sentences = sentencesLinkedToFilteredElement.keySet();
//						for (DecisionKnowledgeElement sentence : sentences) {
//							Link transitiveLink = new LinkImpl(element,sentence);
//							transitiveLink.setType("contains");
//							linkIds.add(transitiveLink.getId());
//							linkedElementsAndLinks.put(sentence,transitiveLink);
//						}
					}
				}
			}
		}

		return linkedElementsAndLinks;
	}

	private List<DecisionKnowledgeElement> getInwardTransitiveLinkedNodes(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> transitiveLinkedNodes = new ArrayList<>();
		List<Link> inwardLinks = this.project.getPersistenceStrategy().getInwardLinks(element);
		for (Link link : inwardLinks) {
			if (!linkIds.contains(link.getId())) {
				boolean isFiltered = true;
				int count = 0;
				DecisionKnowledgeElement currentElement = link.getSourceElement();
				while (isFiltered && (count < 10)) {
					if (this.filteredElements.contains(currentElement)) {
						if (!currentElement.getType().equals(KnowledgeType.ARGUMENT)) {
							transitiveLinkedNodes.add(currentElement);
						}
						isFiltered = false;
					} else {
						transitiveLinkedNodes.addAll(getInwardTransitiveLinkedNodes(currentElement));
						count++;
					}
				}
			}
		}
		return transitiveLinkedNodes;
	}

	private Map<DecisionKnowledgeElement, Link> getElementsLinkedWithOutwardLinksFiltered(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}

		List<Link> outwardLinks = this.project.getPersistenceStrategy().getOutwardLinks(element);
		for (Link link : outwardLinks) {
			if (!linkIds.contains(link.getId())) {
				DecisionKnowledgeElement outwardElement = link.getDestinationElement();
				if (outwardElement != null) {
					if (this.filteredElements.contains(outwardElement)) {
						linkIds.add(link.getId());
						linkedElementsAndLinks.put(outwardElement, link);
					} else {
						linkIds.add(link.getId());
//						List<DecisionKnowledgeElement> transitiveLinkedElements = getOutwardTransitiveLinkedNodes(outwardElement);
//						for (DecisionKnowledgeElement element1 : transitiveLinkedElements) {
//							Link transitiveLink = new LinkImpl(element1, element);
//							transitiveLink.setType(link.getType());
//							linkIds.add(transitiveLink.getId());
//							linkedElementsAndLinks.put(element1, transitiveLink);
//						}
//						Map<DecisionKnowledgeElement,Link> sentencesLinkedToFilteredElement = getAllLinkedSentences(outwardElement);
//						Set<DecisionKnowledgeElement> sentences = sentencesLinkedToFilteredElement.keySet();
//						for (DecisionKnowledgeElement sentence : sentences) {
//							Link transitiveLink = new LinkImpl(element,sentence);
//							transitiveLink.setType("contains");
//							linkIds.add(transitiveLink.getId());
//							linkedElementsAndLinks.put(sentence,transitiveLink);
//						}
					}
				}
			}
		}
		return linkedElementsAndLinks;
	}

	private List<DecisionKnowledgeElement> getOutwardTransitiveLinkedNodes(DecisionKnowledgeElement element) {
		List<DecisionKnowledgeElement> transitiveLinkedNodes = new ArrayList<>();
		List<Link> outwardLinks = this.project.getPersistenceStrategy().getOutwardLinks(element);
		for (Link link : outwardLinks) {
			if (!linkIds.contains(link.getId())) {
				boolean isFiltered = true;
				int count = 0;
				DecisionKnowledgeElement currentElement = link.getDestinationElement();
				while (isFiltered && (count < 10)) {
					if (this.filteredElements.contains(currentElement)) {
						if (!currentElement.getType().equals(KnowledgeType.ARGUMENT)) {
							transitiveLinkedNodes.add(currentElement);
						}
						isFiltered = false;
					} else {
						transitiveLinkedNodes.addAll(getOutwardTransitiveLinkedNodes(currentElement));
						count++;
					}
				}
			}
		}
		return transitiveLinkedNodes;
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