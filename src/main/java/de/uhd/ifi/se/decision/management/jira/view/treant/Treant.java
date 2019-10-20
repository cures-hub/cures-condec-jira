package de.uhd.ifi.se.decision.management.jira.view.treant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterExtractorImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.PersistenceInterface;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManager;

/**
 * Creates Treant content
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

	public Treant() {
	}

	public Treant(String projectKey, String elementKey, int depth, boolean isHyperlinked) {
		this(projectKey, elementKey, depth, null, null, isHyperlinked);
	}

	public Treant(String projectKey, String elementKey, int depth) {
		this(projectKey, elementKey, depth, false);
	}

	public Treant(String projectKey, String elementKey, int depth, String query, ApplicationUser user,
			boolean isHyperlinked) {
		FilterExtractor filterExtractor = new FilterExtractorImpl(projectKey, user, query);
		filterExtractor.getAllElementsMatchingQuery();
		this.graph = KnowledgeGraph.getOrCreate(projectKey);

		AbstractPersistenceManager persistenceManager;
		if (elementKey.contains(":")) {
			persistenceManager = PersistenceInterface.getPersistenceManager(projectKey,
					DocumentationLocation.JIRAISSUETEXT.getIdentifier());
		} else {
			persistenceManager = PersistenceInterface.getDefaultPersistenceManager(projectKey);
		}
		DecisionKnowledgeElement rootElement = persistenceManager.getDecisionKnowledgeElement(elementKey);
		this.setChart(new Chart());
		this.setNodeStructure(this.createNodeStructure(rootElement, null, depth, 1));
		this.setHyperlinked(isHyperlinked);
	}

	public Treant(String projectKey, String elementKey, int depth, String query, ApplicationUser user) {
		this(projectKey, elementKey, depth, query, user, false);
	}

	public TreantNode createNodeStructure(DecisionKnowledgeElement element, Link link, int depth, int currentDepth) {
		if (element == null || element.getProject() == null) {
			return new TreantNode();
		}

		if (graph == null) {
			graph = KnowledgeGraph.getOrCreate(element.getProject().getProjectKey());
		}
		Map<DecisionKnowledgeElement, Link> childrenAndLinks = graph.getAdjacentElementsAndLinks(element);
		boolean isCollapsed = false;
		if (currentDepth == depth && childrenAndLinks.size() != 0) {
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
		for (Map.Entry<DecisionKnowledgeElement, Link> childAndLink : childrenAndLinks.entrySet()) {
			TreantNode newChildNode = createNodeStructure(childAndLink.getKey(), childAndLink.getValue(), depth,
					currentDepth + 1);
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