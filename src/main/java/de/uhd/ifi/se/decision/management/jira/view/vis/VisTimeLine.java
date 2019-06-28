package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceManager;

public class VisTimeLine {

	private List<DecisionKnowledgeElement> elementList;

	@XmlElement
	private HashSet<VisTimeLineNode> dataSet;

	public VisTimeLine(String projectKey) {
		if (projectKey != null) {
			AbstractPersistenceManager strategy = AbstractPersistenceManager.getDefaultPersistenceStrategy(projectKey);
			elementList = strategy.getDecisionKnowledgeElements();
		}
		createDataSet();
	}

	public HashSet<VisTimeLineNode> getEvolutionData() {
		return dataSet;
	}

	public List<DecisionKnowledgeElement> getElementList() {
		return elementList;
	}

	public void setElementList(List<DecisionKnowledgeElement> elementList) {
		this.elementList = elementList;
	}

	private void createDataSet() {
		dataSet = new HashSet<>();
		dataSet.add(new VisTimeLineNode(1, "Test Item 1", "2019-03-1"));
		dataSet.add(new VisTimeLineNode(2, "Test Item 2", "2019-03-4"));
		dataSet.add(new VisTimeLineNode(3, "Test Item 3", "2019-03-20"));
	}
}
