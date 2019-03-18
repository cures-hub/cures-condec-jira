package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import de.uhd.ifi.se.decision.management.jira.filtering.GraphFiltering;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;

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
		this.elementsVisitedTransitively = new ArrayList<DecisionKnowledgeElement>();
	}

	public GraphImplFiltered(String projectKey, String rootElementKey, GraphFiltering filter) {
		super(projectKey, rootElementKey);
		this.filter = filter;
		this.elementsVisitedTransitively = new ArrayList<DecisionKnowledgeElement>();
	}

	@Override
	protected Map<DecisionKnowledgeElement, Link> getLinkedFirstClassElementsAndLinks(
			DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		List<Link> links = this.project.getPersistenceStrategy().getLinks(element);
		for (Link link : links) {
			if (linkIds.contains(link.getId())) {
				continue;
			}
			linkIds.add(link.getId());
			DecisionKnowledgeElement oppositeElement = link.getOppositeElement(element);
			if (oppositeElement == null) {
				continue;
			}
			if (this.filter.getQueryResults().contains(oppositeElement)) {
				linkedElementsAndLinks.put(oppositeElement, link);
			} else {
				List<DecisionKnowledgeElement> transitivelyLinkedElements = getTransitivelyLinkedElements(
						oppositeElement);
				for (DecisionKnowledgeElement transitivelyLinkedElement : transitivelyLinkedElements) {
					Link transitiveLink = new LinkImpl(element, transitivelyLinkedElement);
					transitiveLink.setType("contains");
					linkIds.add(transitiveLink.getId());
					linkedElementsAndLinks.put(transitivelyLinkedElement, transitiveLink);
				}
				Map<DecisionKnowledgeElement, Link> sentencesLinkedToFilteredElement = getLinkedSentencesAndLinks(
						oppositeElement);
				Set<DecisionKnowledgeElement> sentences = sentencesLinkedToFilteredElement.keySet();
				linkSentencesTransitively(element, linkedElementsAndLinks, sentences);
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

	private List<DecisionKnowledgeElement> getTransitivelyLinkedElements(DecisionKnowledgeElement element) {
		if (elementsVisitedTransitively.contains(element) || element == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> transitivelyLinkedElements = new ArrayList<DecisionKnowledgeElement>();
		List<Link> links = this.project.getPersistenceStrategy().getLinks(element);
		for (Link link : links) {
			if (linkIds.contains(link.getId())) {
				continue;
			}
			boolean isFiltered = true;
			int count = 0;
			DecisionKnowledgeElement oppositeElement = link.getOppositeElement(element);
			while (isFiltered && (count < 10)) {
				if (filter.getQueryResults().contains(oppositeElement)) {
					if (!oppositeElement.getType().equals(KnowledgeType.ARGUMENT)) {
						transitivelyLinkedElements.add(oppositeElement);
					}
					isFiltered = false;
				} else {
					if (!elementsVisitedTransitively.contains(oppositeElement)) {
						elementsVisitedTransitively.add(oppositeElement);
					}
					transitivelyLinkedElements.addAll(getTransitivelyLinkedElements(oppositeElement));
					count++;
				}
			}
		}
		return transitivelyLinkedElements;
	}

	@Override
	protected Map<DecisionKnowledgeElement, Link> getLinkedSentencesAndLinks(DecisionKnowledgeElement element) {
		Map<DecisionKnowledgeElement, Link> linkedElementsAndLinks = new HashMap<DecisionKnowledgeElement, Link>();

		if (element == null) {
			return linkedElementsAndLinks;
		}
		List<Link> links = GenericLinkManager.getLinksForElement(element);

		boolean includeElementInGraph = false;

		for (Link link : links) {
			if (link.isInterProjectLink()) {
				continue;
			}
			DecisionKnowledgeElement oppositeElement = link.getOppositeElement(element);
			if (filter.isQueryContainsCreationDate() && oppositeElement instanceof PartOfJiraIssueText) {
				includeElementInGraph = isSentenceIncludedInGraph(oppositeElement);
			} else {
				includeElementInGraph = true;
			}

			if (includeElementInGraph && !this.genericLinkIds.contains(link.getId())) {
				this.genericLinkIds.add(link.getId());
				linkedElementsAndLinks.put(oppositeElement, link);
			}
		}

		// remove irrelevant sentences from graph
		linkedElementsAndLinks.keySet().removeIf(e -> (e instanceof PartOfJiraIssueText && !((PartOfJiraIssueText) e).isRelevant()));

		return linkedElementsAndLinks;
	}

	private boolean isSentenceIncludedInGraph(DecisionKnowledgeElement element) {
		if (filter.getStartDate() <= 0 && element.getCreated().getTime() < filter.getEndDate()) {
			return true;
		} else if (filter.getEndDate() <= 0 && element.getCreated().getTime() > filter.getStartDate()) {
			return true;
		} else if (element.getCreated().getTime() < filter.getEndDate()
				&& element.getCreated().getTime() > filter.getStartDate()) {
			return true;
		}
		return false;
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