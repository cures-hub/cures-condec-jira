package de.uhd.ifi.se.decision.management.jira.model;

import java.util.*;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.view.GraphFiltering;

/**
 * Model class for a graph of decision knowledge elements that matches filter criteria
 */
@JsonAutoDetect
public class GraphImplFiltered extends GraphImpl {
	private DecisionKnowledgeElement rootElement;
	private List<Long> linkIds;
	private DecisionKnowledgeProject project;
	private GraphFiltering filter;
	private static List<Link> sentenceLinkAlreadyVisited;
	private List<DecisionKnowledgeElement> elementsVisitedTransitively;

	public GraphImplFiltered() {
		linkIds = new ArrayList<>();
		sentenceLinkAlreadyVisited = new ArrayList<>();
	}

	public GraphImplFiltered(String projectKey) {
		this();
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
	}

	public GraphImplFiltered(String projectKey, String rootElementKey, GraphFiltering filter) {
		this(projectKey);
		String cleanRootElementKey = getRootElementKeyWithoutDoubleDot(rootElementKey);
		this.rootElement = this.project.getPersistenceStrategy().getDecisionKnowledgeElement(cleanRootElementKey);
		this.filter = filter;
	}

	@Override
	public Map<DecisionKnowledgeElement, Link> getLinkedElementsAndLinks(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<>();

		linkedElementsAndLinks.putAll(this.getElementsLinkedWithInwardLinksFiltered(element));
		linkedElementsAndLinks.putAll(this.getElementsLinkedWithOutwardLinksFiltered(element));
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
				if (filter.isQueryContainsCreationDate()) {
					if (filter.getStartDate() <= 0) {
						if (((Sentence) source).getCreated().getTime() < filter.getEndDate()) {
							DecisionKnowledgeElement toLink = currentGenericLink
									.getOpposite(preIndex + element.getId());
							if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
								sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
								linkedElementsAndLinks.put(toLink, linkBetweenSentenceAndOtherElement);
							}
						}
					} else if (filter.getEndDate() <= 0) {
						if (((Sentence) source).getCreated().getTime() > filter.getStartDate()) {
							DecisionKnowledgeElement toLink = currentGenericLink
									.getOpposite(preIndex + element.getId());
							if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
								sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
								linkedElementsAndLinks.put(toLink, linkBetweenSentenceAndOtherElement);
							}
						}
					} else {
						if ((((Sentence) source).getCreated().getTime() < filter.getEndDate())
								&& (((Sentence) source).getCreated().getTime() > filter.getStartDate())) {
							DecisionKnowledgeElement toLink = currentGenericLink
									.getOpposite(preIndex + element.getId());
							if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
								sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
								linkedElementsAndLinks.put(toLink, linkBetweenSentenceAndOtherElement);
							}
						}
					}
				} else {
					if (!linkListContainsLink(linkBetweenSentenceAndOtherElement)) {
						GraphImplFiltered.sentenceLinkAlreadyVisited.add(linkBetweenSentenceAndOtherElement);
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

	private boolean linkListContainsLink(Link link2) {
		for (Link link : GraphImplFiltered.sentenceLinkAlreadyVisited) {
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

	private Map<DecisionKnowledgeElement, Link> getElementsLinkedWithInwardLinksFiltered(
			DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}

		List<Link> inwardLinks = this.project.getPersistenceStrategy().getInwardLinks(element);
		for (Link link : inwardLinks) {
			if (!linkIds.contains(link.getId())) {
				DecisionKnowledgeElement inwardElement = link.getSourceElement();
				if (inwardElement != null) {
					if (this.filter.getQueryResults().contains(inwardElement)) {
						linkIds.add(link.getId());
						linkedElementsAndLinks.put(inwardElement, link);
					} else {
						linkIds.add(link.getId());
						List<DecisionKnowledgeElement> transitiveLinkedElements = getInwardTransitiveLinkedNodes(
								inwardElement);
						for (DecisionKnowledgeElement element1 : transitiveLinkedElements) {
							Link transitiveLink = new LinkImpl(element, element1);
							transitiveLink.setType("contains");
							linkIds.add(transitiveLink.getId());
							linkedElementsAndLinks.put(element1, transitiveLink);
						}
						Map<DecisionKnowledgeElement, Link> sentencesLinkedToFilteredElement = getAllLinkedSentences(
								inwardElement);
						Set<DecisionKnowledgeElement> sentences = sentencesLinkedToFilteredElement.keySet();
						linkSentencesTransitively(element, linkedElementsAndLinks, sentences);
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
					if (filter.getQueryResults().contains(currentElement)) {
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

	private Map<DecisionKnowledgeElement, Link> getElementsLinkedWithOutwardLinksFiltered(
			DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}

		List<Link> outwardLinks = this.project.getPersistenceStrategy().getOutwardLinks(element);
		for (Link link : outwardLinks) {
			if (!linkIds.contains(link.getId())) {
				DecisionKnowledgeElement outwardElement = link.getDestinationElement();
				if (outwardElement != null) {
					if (filter.getQueryResults().contains(outwardElement)) {
						linkIds.add(link.getId());
						linkedElementsAndLinks.put(outwardElement, link);
					} else {
						if (!outwardElement.getType().equals(KnowledgeType.ALTERNATIVE)) {
							linkIds.add(link.getId());
							elementsVisitedTransitively = new ArrayList<>();
							List<DecisionKnowledgeElement> transitiveLinkedElements = getOutwardTransitiveLinkedNodes(
									outwardElement);
							if (transitiveLinkedElements != null) {
								for (DecisionKnowledgeElement element1 : transitiveLinkedElements) {
									Link transitiveLink = new LinkImpl(element1, element);
									transitiveLink.setType("contains");
									linkIds.add(transitiveLink.getId());
									linkedElementsAndLinks.put(element1, transitiveLink);
								}
							}
						}
						Map<DecisionKnowledgeElement, Link> sentencesLinkedToFilteredElement = getAllLinkedSentences(
								outwardElement);
						Set<DecisionKnowledgeElement> sentences = sentencesLinkedToFilteredElement.keySet();
						linkSentencesTransitively(element, linkedElementsAndLinks, sentences);
					}
				}
			}
		}
		return linkedElementsAndLinks;
	}

	private void linkSentencesTransitively(DecisionKnowledgeElement element, Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks, Set<DecisionKnowledgeElement> sentences) {
		for (DecisionKnowledgeElement sentence : sentences) {
			Link transitiveLink = new LinkImpl(element, sentence);
			transitiveLink.setType("contains");
			linkIds.add(transitiveLink.getId());
			linkedElementsAndLinks.put(sentence, transitiveLink);
		}
	}

	private List<DecisionKnowledgeElement> getOutwardTransitiveLinkedNodes(DecisionKnowledgeElement element) {
		if (elementsVisitedTransitively.contains(element)) {
			return null;
		} else {
			List<DecisionKnowledgeElement> transitiveLinkedNodes = new ArrayList<>();
			List<Link> outwardLinks = this.project.getPersistenceStrategy().getOutwardLinks(element);
			for (Link link : outwardLinks) {
				if (!linkIds.contains(link.getId())) {
					boolean isFiltered = true;
					DecisionKnowledgeElement currentElement = link.getDestinationElement();
					int count = 0;
					while (isFiltered && count<10) {
						if (filter.getQueryResults().contains(currentElement)) {
							if (!currentElement.getType().equals(KnowledgeType.ARGUMENT)) {
								transitiveLinkedNodes.add(currentElement);
							}
							isFiltered = false;
						} else {
							List<DecisionKnowledgeElement> outwardTransitiveLinkedNodes = getOutwardTransitiveLinkedNodes(currentElement);
							if (outwardTransitiveLinkedNodes != null) {
								transitiveLinkedNodes.addAll(outwardTransitiveLinkedNodes);
								count++;
							}
						}
						if (!elementsVisitedTransitively.contains(currentElement)) {
							elementsVisitedTransitively.add(currentElement);
						}
					}
				}
			}
			return transitiveLinkedNodes;
		}
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