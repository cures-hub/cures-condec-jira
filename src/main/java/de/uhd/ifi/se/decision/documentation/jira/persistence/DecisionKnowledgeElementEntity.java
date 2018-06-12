package de.uhd.ifi.se.decision.documentation.jira.persistence;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * @description Interface for decision knowledge elements used in the
 *              active object persistence strategy
 */
@Table("DECISION")
public interface DecisionKnowledgeElementEntity extends RawEntity<Integer> {
	@AutoIncrement
	@PrimaryKey("ID")
	public long getId();
	public void setId(long id);

	public String getSummary();
	public void setSummary(String summary);

	public String getDescription();
	public void setDescription(String description);

	public String getType();
	public void setType(String type);

	public String getProjectKey();
	public void setProjectKey(String projectKey);

	public String getKey();
	public void setKey(String key);
}