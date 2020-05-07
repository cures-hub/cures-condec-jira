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
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.AbstractPersistenceManagerForSingleLocation;

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

	public Treant(String projectKey, KnowledgeElement element, int depth, String query, String treantId,
				  boolean checkboxflag, boolean isIssueView, int minLinkNumber, int maxLinkNumber) {
		this.traversedLinks = new HashSet<Link>();
		this.depth = depth;
		this.graph = KnowledgeGraph.getOrCreate(projectKey);
		this.setChart(new Chart(treantId));
		Set<Link> usedLinks = new HashSet<>();
		if (isIssueView) {
			for (Link link : element.getLinks()) {
				if ((checkboxflag || !checkboxflag && !link.getSource().getSummary().startsWith("Test"))
						&& link.getSource() != null && link.getSource().getSummary().contains(".java")
						&& link.getSource().getDocumentationLocation().getIdentifier().equals("c")
						&& link.getSource().getLinks().size() >= minLinkNumber
						&& link.getSource().getLinks().size() <= maxLinkNumber
						&& ("".equals(query) || " ".equals(query) || link.getSource().getSummary().contains(query))) {
					usedLinks.add(link);
				}
			}
		} else {
			if (!checkboxflag) {
				for (Link link : element.getLinks()) {
					KnowledgeElement targetElement = link.getTarget();
					List<Link> elementLinks = GenericLinkManager.getOutwardLinks(targetElement);
					if (elementLinks != null && elementLinks.size() > 0) {
						usedLinks.add(link);
					}
				}
			} else {
				usedLinks = new HashSet<>(element.getLinks());
			}
		}
		this.setNodeStructure(this.createNodeStructure(element, usedLinks, 1, isIssueView));
		this.setHyperlinked(false);
	}

	public TreantNode createNodeStructure(KnowledgeElement element, Link link, int currentDepth) {
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

	public TreantNode createNodeStructure(KnowledgeElement element, Set<Link> links, int currentDepth,
										  boolean isIssueView) {
		if (element == null || element.getProject() == null || links == null) {
			return new TreantNode();
		}
		// boolean isCollapsed = isNodeCollapsed(linksToTraverse, currentDepth);
		TreantNode node = createTreantNode(element, null, false);
		if (currentDepth == depth + 1) {
			return node;
		}
		List<TreantNode> nodes = new ArrayList<TreantNode>();
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

	private boolean isNodeCollapsed(Set<Link> linksToTraverse, int currentDepth) {
		boolean isCollapsed = false;
		if (currentDepth == depth && !traversedLinks.containsAll(linksToTraverse)) {
			isCollapsed = true;
		}
		return isCollapsed;
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