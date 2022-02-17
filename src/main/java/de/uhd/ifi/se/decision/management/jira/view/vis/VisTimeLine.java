package de.uhd.ifi.se.decision.management.jira.view.vis;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Creates the content for the chronology view. The chronology view is rendered
 * with the vis timeline library.
 */
public class VisTimeLine {

	private Set<Long> userIds;

	@XmlElement(name = "groupSet")
	private Set<VisTimeLineGroup> groups;

	@XmlElement(name = "dataSet")
	private Set<VisTimeLineNode> nodes;

	private boolean isPlacedAtCreationDate;
	private boolean isPlacedAtUpdatingDate;
	private FilterSettings filterSettings;

	private static final Logger LOGGER = LoggerFactory.getLogger(VisTimeLine.class);

	public VisTimeLine() {
		this.nodes = new HashSet<>();
		this.groups = new HashSet<>();
		this.userIds = new HashSet<>();
		this.filterSettings = new FilterSettings();
	}

	public VisTimeLine(Set<KnowledgeElement> elements) {
		this();
		addElements(elements);
	}

	public VisTimeLine(ApplicationUser user, FilterSettings filterSettings, boolean isPlacedAtCreationDate,
			boolean isPlacedAtUpdatingDate) {
		this();
		if (user == null || filterSettings == null) {
			return;
		}
		LOGGER.info(filterSettings.toString());
		this.filterSettings = filterSettings;
		this.isPlacedAtCreationDate = isPlacedAtCreationDate;
		this.isPlacedAtUpdatingDate = isPlacedAtUpdatingDate;
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> elements = filteringManager.getElementsMatchingFilterSettings();
		addElements(elements);
	}

	public void addElements(Set<KnowledgeElement> elements) {
		if (elements == null) {
			return;
		}
		for (KnowledgeElement element : elements) {
			addElement(element);
		}
	}

	public boolean addElement(KnowledgeElement element) {
		String userName = element.getCreatorName();
		if (userName == null) {
			return false;
		}
		long userId = userName.hashCode();
		if (!userIds.contains(userId)) {
			userIds.add(userId);
			groups.add(new VisTimeLineGroup(userName));
		}
		VisTimeLineNode node = new VisTimeLineNode(element, userId, isPlacedAtCreationDate, isPlacedAtUpdatingDate,
				filterSettings);
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
