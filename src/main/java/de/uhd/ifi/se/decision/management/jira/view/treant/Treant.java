package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.AbstractPersistenceManagerForSingleLocation;

/**
 * Creates a tree data structure from the {@link KnowledgeGraph} according to
 * the given {@link FilterSettings}. Uses the Treant.js framework for
 * visualization of the knowledge tree.
 */
@XmlRootElement(name = "treant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Treant {
	@XmlElement
	private Chart chart;

	@XmlElement
	private TreantNode nodeStructure;

	private KnowledgeGraph graph;
	private FilterSettings filterSettings;
	private boolean isHyperlinked;
	private Set<Link> traversedLinks;

	public Treant(String projectKey, String elementKey, boolean isHyperlinked) {
		this(projectKey, elementKey, isHyperlinked, new FilterSettings(projectKey, null));
	}

	public Treant(String projectKey, String elementKey, FilterSettings filterSettings) {
		this(projectKey, elementKey, false, filterSettings);
	}

	public Treant(String projectKey, String elementKey, boolean isHyperlinked, FilterSettings filterSettings) {
		this.setFilterSettings(filterSettings == null ? new FilterSettings(projectKey, null) : filterSettings);
		this.traversedLinks = new HashSet<Link>();
		this.graph = KnowledgeGraph.getOrCreate(projectKey);

		AbstractPersistenceManagerForSingleLocation persistenceManager;
		// TODO this should not be checked in Treant, instead of the elementKey the
		// entire element should be passed
		if (elementKey.contains(":")) {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
					.getManagerForSingleLocation(DocumentationLocation.JIRAISSUETEXT);
		} else {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey).getJiraIssueManager();
		}
		KnowledgeElement rootElement = persistenceManager.getKnowledgeElement(elementKey);
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, (Link) null, 1));
		this.setHyperlinked(isHyperlinked);
	}

	public Treant(String projectKey, KnowledgeElement element, String treantId, boolean isIssueView,
			FilterSettings filterSettings) {
		this.setFilterSettings(filterSettings == null ? new FilterSettings(projectKey, null) : filterSettings);
		this.traversedLinks = new HashSet<Link>();
		this.graph = KnowledgeGraph.getOrCreate(projectKey);

		// TODO Why is the treantId needed?
		this.setChart(new Chart(treantId));

		// TODO Filtering should be done on the Knowledge Graph directly and the
		// FilteringManager should be used
		Set<Link> usedLinks = new HashSet<>();
		if (isIssueView) {
			usedLinks = getLinksInJiraIssueView(element);
		} else {
			usedLinks = getLinks(element);
		}
		if (this.filterSettings.getLinkDistance() > 0) {
			this.setNodeStructure(this.createNodeStructure(element, usedLinks, 1, isIssueView));
		}
		this.setHyperlinked(false);
	}

	private Set<Link> getLinksInJiraIssueView(KnowledgeElement element) {
		Set<Link> usedLinks = new HashSet<>();
		for (Link link : element.getLinks()) {
			KnowledgeElement source = link.getSource();
			if (source == null) {
				continue;
			}
			if (source.getDocumentationLocation() != DocumentationLocation.COMMIT) {
				continue;
			}
			if (!source.getSummary().contains(".java")) {
				continue;
			}
			FilteringManager filteringManager = new FilteringManager(filterSettings);
			if (!filteringManager.isElementMatchingIsTestCodeFilter(source)) {
				continue;
			}
			if (!filteringManager.isElementMatchingDegreeFilter(source)) {
				continue;
			}
			if (!filteringManager.isElementMatchingSubStringFilter(source)) {
				continue;
			}
			usedLinks.add(link);
		}
		return usedLinks;
	}

	private Set<Link> getLinks(KnowledgeElement element) {
		Set<Link> usedLinks = new HashSet<>();
		if (!filterSettings.isOnlyDecisionKnowledgeShown()) {
			for (Link link : element.getLinks()) {
				KnowledgeElement targetElement = link.getTarget();
				// TODO Get rid of GenericLinkManager
				List<Link> elementLinks = GenericLinkManager.getOutwardLinks(targetElement);
				if (elementLinks != null && elementLinks.size() > 0) {
					usedLinks.add(link);
				}
			}
		} else {
			usedLinks = new HashSet<>(element.getLinks());
		}
		return usedLinks;
	}

	public TreantNode createNodeStructure(KnowledgeElement element, Link link, int currentDepth) {
		if (element == null || element.getProject() == null) {
			return new TreantNode();
		}
		Set<Link> linksToTraverse = graph.edgesOf(element);
		TreantNode node = createTreantNode(element, link, false);
		if (currentDepth == this.filterSettings.getLinkDistance() + 1) {
			return node;
		}
		List<TreantNode> nodes = getChildren(element, linksToTraverse, currentDepth);
		node.setChildren(nodes);
		return node;
	}

	// TODO Remove isIssueView parameter
	public TreantNode createNodeStructure(KnowledgeElement element, Set<Link> links, int currentDepth,
			boolean isIssueView) {
		if (element == null || element.getProject() == null || links == null) {
			return new TreantNode();
		}
		TreantNode node = createTreantNode(element, null, false);
		if (currentDepth == this.filterSettings.getLinkDistance() + 1) {
			return node;
		}
		List<TreantNode> nodes = new ArrayList<TreantNode>();
		// TODO Get rid of AbstractPersistenceManagerForSingleLocation here
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getOrCreate(element.getProject()).getJiraIssueManager();
		for (Link link : links) {
			if (!isIssueView && persistenceManager.getKnowledgeElement(link.getTarget().getId()) != null) {
				TreantNode adult = createTreantNode(link.getTarget(), link, false);
				adult.setChildren(getChildren(link.getTarget(), graph.edgesOf(link.getTarget()), currentDepth));
				nodes.add(adult);
			} else if (isIssueView) {
				TreantNode adult = createTreantNode(link.getSource(), link, false);
				nodes.add(adult);
			}
		}
		if (nodes != null) {
			node.setChildren(nodes);
		}
		return node;
	}

	private TreantNode createTreantNode(KnowledgeElement element, Link link, boolean isCollapsed) {
		TreantNode node;
		if (link != null) {
			node = new TreantNode(element, link, isCollapsed, isHyperlinked);
		} else {
			node = new TreantNode(element, isCollapsed, isHyperlinked);
		}
		return node;
	}

	private List<TreantNode> getChildren(KnowledgeElement rootElement, Set<Link> linksToTraverse, int currentDepth) {
		List<TreantNode> nodes = new ArrayList<TreantNode>();
		for (Link currentLink : linksToTraverse) {
			if (!traversedLinks.add(currentLink)) {
				continue;
			}
			KnowledgeElement oppositeElement = currentLink.getOppositeElement(rootElement);
			if (oppositeElement == null
					|| (oppositeElement.getType() == KnowledgeType.OTHER
							&& filterSettings.isOnlyDecisionKnowledgeShown())
					|| (!filterSettings.isOnlyDecisionKnowledgeShown()
							&& oppositeElement.getType() == KnowledgeType.OTHER
							&& oppositeElement.getDocumentationLocation() != DocumentationLocation.JIRAISSUE)) {
				continue;
			}
			TreantNode newChildNode = createNodeStructure(oppositeElement, currentLink, currentDepth + 1);
			nodes.add(newChildNode);
		}
		return nodes;
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	public TreantNode getNodeStructure() {
		return nodeStructure;
	}

	public void setNodeStructure(TreantNode nodeStructure) {
		this.nodeStructure = nodeStructure;
	}

	public boolean isHyperlinked() {
		return isHyperlinked;
	}

	public void setHyperlinked(boolean isHyperlinked) {
		this.isHyperlinked = isHyperlinked;
	}

	public FilterSettings getFilterSettings() {
		return filterSettings;
	}

	public void setFilterSettings(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
	}
}