package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.PersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AbstractPersistenceManagerForSingleLocation;

public class VisTimeLine {

	private List<DecisionKnowledgeElement> elementList;

	@XmlElement
	private HashSet<VisTimeLineNode> dataSet;

	@XmlElement
	private HashSet<VisTimeLineGroup> groupSet;

	public VisTimeLine(String projectKey) {
		if (projectKey != null) {
			elementList = PersistenceManager.getOrCreate(projectKey).getDecisionKnowledgeElements();
		}
		createDataSet();
	}

	public VisTimeLine(List<DecisionKnowledgeElement> elements) {
		if (elements != null) {
			elementList = elements;
			createDataSet();
		}
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

	public HashSet<VisTimeLineGroup> getGroupSet() {
		return groupSet;
	}

	public void setGroupSet(HashSet<VisTimeLineGroup> groupSet) {
		this.groupSet = groupSet;
	}

	private void createDataSet() {
		dataSet = new HashSet<>();
		groupSet = new HashSet<>();
		if (elementList != null) {
			Set<Long> usedApplicationUser = new HashSet<Long>();
			for (DecisionKnowledgeElement element : elementList) {
				AbstractPersistenceManagerForSingleLocation manager = PersistenceManager
						.getPersistenceManagerForDocumentationLocation(element.getProject().getProjectKey(),
								element.getDocumentationLocation());
				ApplicationUser user = manager.getCreator(element);
				if (user == null) {
					continue;
				}
				if (!usedApplicationUser.contains(user.getId())) {
					usedApplicationUser.add(user.getId());
					groupSet.add(new VisTimeLineGroup(user));
				}
				VisTimeLineNode node = new VisTimeLineNode(element);
				node.setGroup(user.getId());
				dataSet.add(node);
			}
		}
	}
}
