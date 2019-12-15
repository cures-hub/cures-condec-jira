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

	private Set<Long> applicationUserIds;

	@XmlElement(name = "groupSet")
	private Set<VisTimeLineGroup> groups;

	@XmlElement(name = "dataSet")
	private Set<VisTimeLineNode> nodes;

	public VisTimeLine() {
		this.nodes = new HashSet<VisTimeLineNode>();
		this.groups = new HashSet<VisTimeLineGroup>();
		this.applicationUserIds = new HashSet<Long>();
	}

	public VisTimeLine(List<DecisionKnowledgeElement> elements) {
		this();
		addElements(elements);
	}

	public VisTimeLine(String projectKey) {
		this();
		if (projectKey == null) {
			return;
		}
		List<DecisionKnowledgeElement> elements = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getDecisionKnowledgeElements();
		addElements(elements);
	}

	public VisTimeLine(ApplicationUser user, FilterSettings filterSettings) {
		this();
		if (user == null || filterSettings == null) {
			return;
		}
		FilterExtractor filterExtractor = new FilterExtractorImpl(user, filterSettings);
		List<DecisionKnowledgeElement> elements = filterExtractor.getAllElementsMatchingFilterSettings();
		addElements(elements);
	}

	public void addElements(List<DecisionKnowledgeElement> elements) {
		if (elements == null) {
			return;
		}
		for (DecisionKnowledgeElement element : elements) {
			addElement(element);
		}
	}

	public boolean addElement(DecisionKnowledgeElement element) {
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

	public Set<VisTimeLineNode> getTimeLineNodes() {
		return nodes;
	}

	public Set<VisTimeLineGroup> getGroups() {
		return groups;
	}

	public void setGroups(HashSet<VisTimeLineGroup> groups) {
		this.groups = groups;
	}
}
