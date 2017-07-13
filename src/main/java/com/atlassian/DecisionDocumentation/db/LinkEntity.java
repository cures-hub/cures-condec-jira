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
@Table("LINK")
public interface LinkEntity extends RawEntity<Integer> {
	@AutoIncrement
	@NotNull
	@PrimaryKey("ID")
	public long getID();
	
	@NotNull
	public String getType();
	public void setType(String type);
	
	@NotNull
	public long getIngoingId();
	public void setIngoingId(long id);
	
	@NotNull
	public long getOutgoingId();
	public void setOutgoingId(long id);
}
