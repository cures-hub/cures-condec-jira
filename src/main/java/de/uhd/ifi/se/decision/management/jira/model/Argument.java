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

		for (Link currentLink : getLinks()) {
			KnowledgeElement element = currentLink.getOppositeElement(this);
			// TODO Make checking criteria type more explicit
			if (element.getType().getSuperType() == KnowledgeType.CONTEXT
					|| getCriteriaTypes().contains(element.getTypeAsString())) {
				criteria.add(element);
			}
		}
		return criteria;
	}

	public Set<String> getCriteriaTypes() {
		String query = DecisionTable.getCriteriaQuery(this.getProject().getProjectKey());
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
