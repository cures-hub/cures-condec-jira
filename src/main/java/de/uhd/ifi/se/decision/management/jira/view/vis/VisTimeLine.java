package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

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

	public VisTimeLine(List<KnowledgeElement> elements) {
		this();
		addElements(elements);
	}

	public VisTimeLine(ApplicationUser user, FilterSettings filterSettings) {
		this();
		if (user == null || filterSettings == null) {
			return;
		}
		FilteringManager filteringManager = new FilteringManager(user, filterSettings);
		List<KnowledgeElement> elements = filteringManager.getAllElementsMatchingFilterSettings();
		addElements(elements);
	}

	public void addElements(List<KnowledgeElement> elements) {
		if (elements == null) {
			return;
		}
		for (KnowledgeElement element : elements) {
			addElement(element);
		}
	}

	public boolean addElement(KnowledgeElement element) {
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
