package de.uhd.ifi.se.decision.documentation.jira.view.treant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.Graph;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;
import de.uhd.ifi.se.decision.documentation.jira.persistence.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

/**
 * @description Model class for Treant
 */
@XmlRootElement(name = "treant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Treant {
	private static final Logger LOGGER = LoggerFactory.getLogger(Treant.class);

	@XmlElement
	private Chart chart;

	@XmlElement(name = "nodeStructure")
	private Node nodeStructure;

	private PersistenceStrategy strategy;

	public Treant() {
	}

	public Treant(String projectKey, String elementKey, int depth) {
		Graph graph = new Graph(projectKey,elementKey,depth);
		this.setChart(new Chart());
		this.setNodeStructure(graph.getNodeStructure());
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	public Node getNodeStructure() {
		return nodeStructure;
	}

	public void setNodeStructure(Node nodeStructure) {
		this.nodeStructure = nodeStructure;
	}
}