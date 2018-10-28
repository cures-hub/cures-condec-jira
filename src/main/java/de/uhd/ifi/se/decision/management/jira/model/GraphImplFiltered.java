package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.view.GraphFiltering;

/**
 * Model class for a graph of decision knowledge elements that matches filter
 * criteria
 */
@JsonAutoDetect
public class GraphImplFiltered extends GraphImpl {

	private GraphFiltering filter;
	private List<DecisionKnowledgeElement> elementsVisitedTransitively;

	public GraphImplFiltered() {
		super();
	}

	public GraphImplFiltered(String projectKey, String rootElementKey, GraphFiltering filter) {
		super(projectKey, rootElementKey);
		this.filter = filter;
	}

	protected Map<DecisionKnowledgeElement, Link> getAllLinkedSentences(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}
		String prefix = DocumentationLocation.getIdentifier(element);
		List<Link> links = GenericLinkManager.getLinksForElement(prefix + element.getId(), false);

		for (Link link : links) {
			if (link.isInterProjectLink()) {
				continue;
			}
			DecisionKnowledgeElement source = link.getSourceElement();
			link.setType("contain");
			if (filter.isQueryContainsCreationDate()) {
				if (filter.getStartDate() <= 0) {
					if (((Sentence) source).getCreated().getTime() < filter.getEndDate()) {
						DecisionKnowledgeElement toLink = link.getOppositeElement(prefix + element.getId());
						if (!this.genericLinkIds.contains(link.getId())) {
							this.genericLinkIds.add(link.getId());
							linkedElementsAndLinks.put(toLink, link);
						}
					}
				} else if (filter.getEndDate() <= 0) {
					if (((Sentence) source).getCreated().getTime() > filter.getStartDate()) {
						DecisionKnowledgeElement toLink = link.getOppositeElement(prefix + element.getId());
						if (!this.genericLinkIds.contains(link.getId())) {
							this.genericLinkIds.add(link.getId());
							linkedElementsAndLinks.put(toLink, link);
						}
					}
				} else {
					if ((((Sentence) source).getCreated().getTime() < filter.getEndDate())
							&& (((Sentence) source).getCreated().getTime() > filter.getStartDate())) {
						DecisionKnowledgeElement toLink = link.getOppositeElement(prefix + element.getId());
						if (!this.genericLinkIds.contains(link.getId())) {
							this.genericLinkIds.add(link.getId());
							linkedElementsAndLinks.put(toLink, link);
						}
					}
				}
			} else {
				if (!this.genericLinkIds.contains(link.getId())) {
					this.genericLinkIds.add(link.getId());
					linkedElementsAndLinks.put(link.getOppositeElement(prefix + element.getId()), link);
				}
			}
		}
		return linkedElementsAndLinks;
	}

	protected Map<DecisionKnowledgeElement, Link> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element) {
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

	protected Map<DecisionKnowledgeElement, Link> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
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

	private void linkSentencesTransitively(DecisionKnowledgeElement element,
			Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks, Set<DecisionKnowledgeElement> sentences) {
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
					while (isFiltered && count < 10) {
						if (filter.getQueryResults().contains(currentElement)) {
							if (!currentElement.getType().equals(KnowledgeType.ARGUMENT)) {
								transitiveLinkedNodes.add(currentElement);
							}
							isFiltered = false;
						} else {
							List<DecisionKnowledgeElement> outwardTransitiveLinkedNodes = getOutwardTransitiveLinkedNodes(
									currentElement);
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