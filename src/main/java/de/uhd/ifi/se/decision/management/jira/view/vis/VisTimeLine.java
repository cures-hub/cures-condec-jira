package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterExtractor;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterExtractorImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;

public class VisTimeLine {

	private List<DecisionKnowledgeElement> elements;
	private Set<Long> applicationUserIds;

	@XmlElement(name = "dataSet")
	private Set<VisTimeLineNode> nodes;

	@XmlElement(name = "groupSet")
	private Set<VisTimeLineGroup> groups;

	public VisTimeLine() {
		this.nodes = new HashSet<VisTimeLineNode>();
		this.groups = new HashSet<VisTimeLineGroup>();
		this.applicationUserIds = new HashSet<Long>();
	}

	public VisTimeLine(List<DecisionKnowledgeElement> elements) {
		this();
		this.elements = elements;
		addElementsToTimeLine(elements);
	}

	public VisTimeLine(String projectKey) {
		this();
		if (projectKey == null) {
			return;
		}
		this.elements = KnowledgePersistenceManager.getOrCreate(projectKey).getDecisionKnowledgeElements();
		addElementsToTimeLine(elements);
	}

	public VisTimeLine(ApplicationUser user, FilterSettings filterSettings) {
		this();
		if (user == null || filterSettings == null) {
			return;
		}
		FilterExtractor filterExtractor = new FilterExtractorImpl(user, filterSettings);
		this.elements = filterExtractor.getAllElementsMatchingCompareFilter();
		addElementsToTimeLine(elements);
	}

	public Set<VisTimeLineNode> getEvolutionData() {
		return nodes;
	}

	public List<DecisionKnowledgeElement> getElements() {
		return elements;
	}

	public void setElementList(List<DecisionKnowledgeElement> elementList) {
		this.elements = elementList;
	}

	public Set<VisTimeLineGroup> getGroupSet() {
		return groups;
	}

	public void setGroupSet(HashSet<VisTimeLineGroup> groupSet) {
		this.groups = groupSet;
	}

	private void addElementsToTimeLine(List<DecisionKnowledgeElement> elements) {
		if (elements == null) {
			return;
		}
		for (DecisionKnowledgeElement element : elements) {
			addElementToTimeLine(element);
		}
	}

	private boolean addElementToTimeLine(DecisionKnowledgeElement element) {
		ApplicationUser user = element.getCreator();
		if (user == null) {
			return false;
		}
		long userId = user.getId();
		if (!applicationUserIds.contains(userId)) {
			applicationUserIds.add(userId);
			groups.add(new VisTimeLineGroup(user));
		}
		VisTimeLineNode node = new VisTimeLineNode(element, userId);
		nodes.add(node);
		return true;
	}
}
