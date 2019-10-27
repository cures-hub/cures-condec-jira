package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;

/**
 * Creates a tree data structure from the {@link KnowledgeGraph}. Uses the
 * Treant.js framework for visualization of the knowledge tree.
 */
@XmlRootElement(name = "treant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Treant {
	@XmlElement
	private Chart chart;

	@XmlElement
	private TreantNode nodeStructure;

	private KnowledgeGraph graph;
	private boolean isHyperlinked;
	private Set<Link> traversedLinks;
	private int depth;

	public Treant(String projectKey, String elementKey, int depth, boolean isHyperlinked) {
		this(projectKey, elementKey, depth, null, null, isHyperlinked);
	}

	public Treant(String projectKey, String elementKey, int depth) {
		this(projectKey, elementKey, depth, false);
	}

	public Treant(String projectKey, String elementKey, int depth, String query, ApplicationUser user) {
		this(projectKey, elementKey, depth, query, user, false);
	}

	public Treant(String projectKey, String elementKey, int depth, String query, ApplicationUser user,
			boolean isHyperlinked) {
		this.traversedLinks = new HashSet<Link>();
		this.depth = depth;
		this.graph = KnowledgeGraph.getOrCreate(projectKey);

		AbstractPersistenceManagerForSingleLocation persistenceManager;
		// TODO pass entire element object to the constructors instead of only the key
		// and use the documentation location of the element
		if (elementKey.contains(":")) {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
					.getPersistenceManager(DocumentationLocation.JIRAISSUETEXT);
		} else {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey).getDefaultPersistenceManager();
		}
		DecisionKnowledgeElement rootElement = persistenceManager.getDecisionKnowledgeElement(elementKey);
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, null, 1));
		this.setHyperlinked(isHyperlinked);
	}

	public TreantNode createNodeStructure(DecisionKnowledgeElement element, Link link, int currentDepth) {
		if (element == null || element.getProject() == null) {
			return new TreantNode();
		}
		Set<Link> linksToTraverse = graph.edgesOf(element);
		boolean isCollapsed = isNodeCollapsed(linksToTraverse, currentDepth);
		TreantNode node = createTreantNode(element, link, isCollapsed);

		if (currentDepth == depth + 1) {
			return node;
		}

		List<TreantNode> nodes = getChildren(element, linksToTraverse, currentDepth);
		node.setChildren(nodes);

		return node;
	}

	private boolean isNodeCollapsed(Set<Link> linksToTraverse, int currentDepth) {
		boolean isCollapsed = false;
		if (currentDepth == depth && !traversedLinks.containsAll(linksToTraverse)) {
			isCollapsed = true;
		}
		return isCollapsed;
	}

	private TreantNode createTreantNode(DecisionKnowledgeElement element, Link link, boolean isCollapsed) {
		TreantNode node;
		if (link != null) {
			node = new TreantNode(element, link, isCollapsed, isHyperlinked);
		} else {
			node = new TreantNode(element, isCollapsed, isHyperlinked);
		}
		return node;
	}

	private List<TreantNode> getChildren(DecisionKnowledgeElement rootElement, Set<Link> linksToTraverse,
			int currentDepth) {
		List<TreantNode> nodes = new ArrayList<TreantNode>();
		for (Link currentLink : linksToTraverse) {
			if (!traversedLinks.add(currentLink)) {
				continue;
			}
			DecisionKnowledgeElement oppositeElement = currentLink.getOppositeElement(rootElement);
			if (oppositeElement == null || oppositeElement.getType() == KnowledgeType.OTHER) {
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
}