package de.uhd.ifi.se.decision.documentation.jira.rest.treants.model;

import javax.xml.bind.annotation.*;

/**
 * @author Ewald Rode
 * @description model class for treant
 */
@XmlRootElement(name = "treant")
@XmlAccessorType(XmlAccessType.FIELD)
public class Treant {
    @XmlElement
    private Chart chart;

    @XmlElement(name = "nodeStructure")
    private Node nodeStructure;

    public Treant() {}
    
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