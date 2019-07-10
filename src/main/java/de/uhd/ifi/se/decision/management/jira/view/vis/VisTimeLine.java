package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.Date;
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

	public VisTimeLine(String projectKey, Date created, Date closed) {
		if (projectKey != null) {
			AbstractPersistenceManager strategy = AbstractPersistenceManager.getDefaultPersistenceStrategy(projectKey);
			elementList = strategy.getDecisionKnowledgeElementsInTimeSpan(created, closed);
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
		for(DecisionKnowledgeElement element: elementList){
			dataSet.add(new VisTimeLineNode(element));
		}
	}
}
