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

	public Type getType();

	public void setType(Type type);

	public String getProjectKey();

	public void setProjectKey(String projectKey);
	
	/**
	 * The key resembles "<<projectKey>>-<<project internal id>>"
	 */
	public String getKey();
	
	public void setKey(String key);

	public String getSummary();

	public void setSummary(String summary);
}