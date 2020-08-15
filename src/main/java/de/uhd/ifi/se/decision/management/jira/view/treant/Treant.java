package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.jgrapht.Graph;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

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

	@JsonIgnore
	private Graph<KnowledgeElement, Link> graph;
	@JsonIgnore
	private FilterSettings filterSettings;
	@JsonIgnore
	private boolean isHyperlinked;
	@JsonIgnore
	private Set<Link> traversedLinks;

	public Treant(FilterSettings filterSettings) {
		this(filterSettings, false);
	}

	public Treant(FilterSettings filterSettings, boolean isHyperlinked) {
		this.traversedLinks = new HashSet<Link>();
		if (filterSettings == null) {
			return;
		}
		this.filterSettings = filterSettings;

		FilteringManager filteringManager = new FilteringManager(filterSettings);
		this.graph = filteringManager.getSubgraphMatchingFilterSettings();
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(filterSettings.getSelectedElement(), (Link) null, 1));
		this.setHyperlinked(isHyperlinked);
	}

	public Treant(String treantId, boolean isIssueView, FilterSettings filterSettings) {
		this.setFilterSettings(filterSettings);
		if (filterSettings == null) {
			return;
		}
		KnowledgeElement element = filterSettings.getSelectedElement();
		this.traversedLinks = new HashSet<Link>();
		FilteringManager filteringManager = new FilteringManager(this.filterSettings);
		this.graph = filteringManager.getSubgraphMatchingFilterSettings();

		// TODO Why is the treantId needed?
		this.setChart(new Chart(treantId));

		// TODO Filtering should be done on the Knowledge Graph directly and the
		// FilteringManager should be used
		Set<Link> usedLinks = new HashSet<>();
		if (isIssueView) {
			usedLinks = getLinksToCodeClasses(element);
		} else {
			usedLinks = new HashSet<>(element.getLinks());
		}
		this.setNodeStructure(this.createNodeStructure(element, usedLinks, 1, isIssueView));
		this.setHyperlinked(false);
	}

	private Set<Link> getLinksToCodeClasses(KnowledgeElement element) {
		Set<Link> usedLinks = new HashSet<>();
		for (Link link : element.getLinks()) {
			KnowledgeElement source = link.getSource();
			if (source == null) {
				continue;
			}
			// TODO Make code class recognition more explicit
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

	public TreantNode createNodeStructure(KnowledgeElement element, Link link, int currentDepth) {
		if (element == null || element.getProject() == null) {
			return new TreantNode();
		}

		TreantNode node = createTreantNode(element, link);
		if (currentDepth == this.filterSettings.getLinkDistance() + 1) {
			return node;
		}

		graph.addVertex(element);
		Set<Link> linksToTraverse = graph.edgesOf(element);
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
		graph.addVertex(element);
		TreantNode node = createTreantNode(element, null);
		if (currentDepth == filterSettings.getLinkDistance() + 1) {
			return node;
		}
		List<TreantNode> nodes = new ArrayList<TreantNode>();

		for (Link link : links) {
			if (!isIssueView && link.getTarget() != null) {
				TreantNode adult = createTreantNode(link.getTarget(), link);
				if (filterSettings.getLinkDistance() > 1) {
					adult.setChildren(getChildren(link.getTarget(), graph.edgesOf(link.getTarget()), currentDepth + 1));
				}
				nodes.add(adult);
			} else if (isIssueView) {
				TreantNode adult = createTreantNode(link.getSource(), link);
				nodes.add(adult);
			}
		}
		if (nodes != null) {
			node.setChildren(nodes);
		}
		return node;
	}

	private TreantNode createTreantNode(KnowledgeElement element, Link link) {
		TreantNode node;
		if (link != null) {
			node = new TreantNode(element, link, false, isHyperlinked);
		} else {
			node = new TreantNode(element, false, isHyperlinked);
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
			if (oppositeElement == null) {
				continue;
			}
			// TODO Add to FilteringManager
			if (filterSettings.isOnlyDecisionKnowledgeShown() && oppositeElement.getType() == KnowledgeType.OTHER) {
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