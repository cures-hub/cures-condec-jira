package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * @description Interface for decision knowledge elements
 */
@JsonDeserialize(as = DecisionKnowledgeElement.class)
public interface IDecisionKnowledgeElement {

	public long getId();

	public void setId(long id);

	public String getSummary();

	public void setSummary(String summary);

	public String getDescription();

	public void setDescription(String description);

	public KnowledgeType getType();

	public void setType(KnowledgeType type);

	public void setType(String type);

	public KnowledgeType getSuperType();

	public String getProjectKey();

	public void setProjectKey(String projectKey);

	/**
	 * The key resembles "<<projectKey>>-<<project internal id>>"
	 */
	public String getKey();

	public void setKey(String key);

	public List<IDecisionKnowledgeElement> getChildren();
}