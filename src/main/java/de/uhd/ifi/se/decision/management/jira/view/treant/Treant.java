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
import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;
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
					.getManagerForSingleLocation(DocumentationLocation.JIRAISSUETEXT);
		} else {
			persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey).getJiraIssueManager();
		}
		KnowledgeElement rootElement = persistenceManager.getDecisionKnowledgeElement(elementKey);
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, (Link) null, 1));
		this.setHyperlinked(isHyperlinked);
	}

	public Treant(String projectKey, String elementKey, int depth, String query, ApplicationUser user,
				  List<String> elementKeys) {
		this.traversedLinks = new HashSet<Link>();
		this.depth = depth;
		this.graph = KnowledgeGraph.getOrCreate(projectKey);
		AbstractPersistenceManagerForSingleLocation persistenceManager = KnowledgePersistenceManager
				.getOrCreate(projectKey).getJiraIssueManager();
		KnowledgeElement classElement = new KnowledgeElementImpl();
		String nodeNameWithGit = elementKey.replaceFirst(projectKey + "-", "");
		String nodeName = nodeNameWithGit.split(";;")[0].split(".java")[0];
		String gitName = nodeNameWithGit.split(";;")[1];
		if (nodeName.length() > 25) {
			classElement.setSummary(nodeName.substring(0, 24) + "...");
		} else {
			classElement.setSummary(nodeName);
		}
		classElement.setProject(projectKey);
		classElement.setDocumentationLocation(DocumentationLocation.getDocumentationLocationFromIdentifier("c"));
		classElement.setStatus(KnowledgeStatus.getKnowledgeStatus(null));
		String elekey = projectKey + "-" + gitName;
		if (elekey.length() > 24) {
			elekey = elekey.substring(0, 20) + "...";
		}
		classElement.setKey(elekey);
		classElement.setType(KnowledgeType.OTHER);
		classElement.setId(0);
		classElement.setDescription(elementKeys.toString());
		List<Link> links = new ArrayList<Link>();
		for (String key : elementKeys) {
			if (key.contains(projectKey)) {
				KnowledgeElement rootElement = persistenceManager.getDecisionKnowledgeElement(key);
				Link link = new LinkImpl(classElement, rootElement);
				links.add(link);
			}
		}
		Set<Link> linkSet = new HashSet<Link>(links);
		this.setChart(new Chart("treant-container-code"));
		this.setNodeStructure(this.createNodeStructure(classElement, linkSet, 1));
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

	public TreantNode createNodeStructure(KnowledgeElement element, Set<Link> links, int currentDepth) {
		if (element == null || element.getProject() == null) {
			return new TreantNode();
		}

		// Set<Link> linksToTraverse = graph.edgesOf(element);
		// boolean isCollapsed = isNodeCollapsed(linksToTraverse, currentDepth);
		TreantNode node = createTreantNode(element, null, false);
		if (currentDepth == depth + 1) {
			return node;
		}
		List<TreantNode> nodes = new ArrayList<TreantNode>();
		for (Link link : links) {
			TreantNode adult = createTreantNode(link.getTarget(), link, false);
			adult.setChildren(getChildren(link.getTarget(), graph.edgesOf(link.getTarget()), currentDepth));
			nodes.add(adult);
			// nodes.addAll(getChildren(link.getOppositeElement(element),
			// graph.edgesOf(link.getOppositeElement(element)),
			// currentDepth));
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