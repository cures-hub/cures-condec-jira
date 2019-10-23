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
 * Treant.js framework.
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

	public Treant() {
		traversedLinks = new HashSet<Link>();
	}

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
		this();
		graph = KnowledgeGraph.getOrCreate(projectKey);

		AbstractPersistenceManagerForSingleLocation persistenceManager;
		if (elementKey.contains(":")) {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
					.getPersistenceManager(DocumentationLocation.JIRAISSUETEXT);
		} else {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey).getDefaultPersistenceManager();
		}
		DecisionKnowledgeElement rootElement = persistenceManager.getDecisionKnowledgeElement(elementKey);
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, null, depth, 1));
		this.setHyperlinked(isHyperlinked);
	}

	public TreantNode createNodeStructure(DecisionKnowledgeElement element, Link link, int depth, int currentDepth) {
		if (element == null || element.getProject() == null) {
			return new TreantNode();
		}

		if (graph == null) {
			graph = KnowledgeGraph.getOrCreate(element.getProject().getProjectKey());
		}
		Set<Link> linksToTraverse = graph.edgesOf(element);
		//linksToTraverse.removeAll(traversedLinks);

		boolean isCollapsed = false;
		if (currentDepth == depth && linksToTraverse.size() != 0) {
			isCollapsed = true;
		}

		TreantNode node;
		if (link != null) {
			node = new TreantNode(element, link, isCollapsed, isHyperlinked);
		} else {
			node = new TreantNode(element, isCollapsed, isHyperlinked);
		}

		if (currentDepth == depth + 1) {
			return node;
		}

		List<TreantNode> nodes = new ArrayList<TreantNode>();
		for (Link currentLink : linksToTraverse) {
			if (!traversedLinks.add(currentLink)) {
				continue;
			}
			DecisionKnowledgeElement oppositeElement = currentLink.getOppositeElement(element);
			if (oppositeElement.getType() == KnowledgeType.OTHER) {
				continue;
			}
			TreantNode newChildNode = createNodeStructure(oppositeElement, currentLink, depth, currentDepth + 1);
			nodes.add(newChildNode);
		}
		node.setChildren(nodes);

		return node;
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