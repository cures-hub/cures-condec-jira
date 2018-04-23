package de.uhd.ifi.se.decision.documentation.jira.model;

/**
 * @description Interface for decision knowledge elements
 */
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

	public String getProjectKey();

	public void setProjectKey(String projectKey);

	/**
	 * The key resembles "<<projectKey>>-<<project internal id>>"
	 */
	public String getKey();

	public void setKey(String key);
}