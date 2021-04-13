package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

import de.uhd.ifi.se.decision.management.jira.filtering.JiraQueryHandler;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.DecisionTable;

/**
 * Models a pro- or con-argument that supports or attacks a
 * {@link SolutionOption}.
 */
public class Argument extends KnowledgeElement {

	private String image;

	/**
	 * @issue How to create an object of a superclass from an object of its
	 *        subclass?
	 */
	public Argument(KnowledgeElement argument) {
		this.project = argument.getProject();
		this.id = argument.getId();
		this.setSummary(argument.getSummary());
		this.documentationLocation = argument.getDocumentationLocation();
		this.type = argument.getType();
		this.image = KnowledgeType.getIconUrl(argument);
	}

	public Argument(KnowledgeElement argument, Link link) {
		this(argument);
		this.image = KnowledgeType.getIconUrl(argument, link.getTypeAsString());
	}

	public List<KnowledgeElement> getCriteria() {
		List<KnowledgeElement> criteria = new ArrayList<>();
		Set<String> criteriaTypes = getCriteriaTypes(project.getProjectKey());
		for (Link currentLink : getLinks()) {
			KnowledgeElement element = currentLink.getOppositeElement(this);
			String type = element.getTypeAsString();
			if (criteriaTypes.stream().anyMatch(criterion -> criterion.equalsIgnoreCase(type))) {
				criteria.add(element);
			}
		}
		return criteria;
	}

	public static Set<String> getCriteriaTypes(String projectKey) {
		String query = DecisionTable.getCriteriaQuery(projectKey);
		return JiraQueryHandler.getNamesOfJiraIssueTypesInQuery(query);
	}

	@XmlElement
	public KnowledgeElement getCriterion() {
		List<KnowledgeElement> criteria = getCriteria();
		return criteria.isEmpty() ? null : criteria.get(0);
	}

	@XmlElement
	public String getImage() {
		return image;
	}
}
