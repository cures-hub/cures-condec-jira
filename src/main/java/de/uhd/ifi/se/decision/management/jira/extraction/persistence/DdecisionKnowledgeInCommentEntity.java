package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Ssentence;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("Ssentence")
public interface DdecisionKnowledgeInCommentEntity extends RawEntity<Long>, Ssentence{
	
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);
	
	//Alle methoden aus DKE reinschreiben mit @Ignore und in DKE löschen
	// isTagged und setTagged
	

	@Ignore
	String getSummary();

	@Ignore
	void setSummary(String summary);

	@Ignore
	String getDescription();

	@Ignore
	void setDescription(String description);

	KnowledgeType getType();

	@Ignore
	void setType(KnowledgeType type);

	@Ignore
	void setType(String type);

	@Ignore
	DecisionKnowledgeProject getProject();

	@Ignore
	void setProject(DecisionKnowledgeProject project);

	@Ignore
	void setProject(String projectKey);

	@Ignore
	String getKey();

	@Ignore
	void setKey(String key);

	@Ignore
	List<DecisionKnowledgeElement> getLinkedElements();

	@Ignore
	List<Link> getOutwardLinks();

	@Ignore
	List<Link> getInwardLinks();

}
