package com.atlassian.DecisionDocumentation.db;

import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

/**
 * 
 * @author Ewald Rode
 * @description
 */
@Table("Decision")
public interface DecisionComponentEntity extends RawEntity<Integer>{
	@AutoIncrement
	@NotNull
	@PrimaryKey("ID")
	public long getID();
	
	@NotNull
	public String getName();
	public void setName(String name);
	
	public String getDescription();
	public void setDescription(String description);
	
	@NotNull
	public String getType();
	public void setType(String type);
	
	@NotNull
	public String getProjectKey();
	public void setProjectKey(String projectKey);
}
