package de.uhd.ifi.se.decision.documentation.jira.decisionknowledge;

/**
 * @description Interface for decision knowledge elements
 */
public interface IDecisionKnowledgeElement {
	
	public long getId();

	public void setId(long id);

	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);

	public String getType();

	public void setType(String type);

	public String getProjectKey();

	public void setProjectKey(String projectKey);
	
	/**
	 * @return projectKey - id
	 */
	public String getKey();

	public String getSummary();

	public void setSummary(String summary);
}