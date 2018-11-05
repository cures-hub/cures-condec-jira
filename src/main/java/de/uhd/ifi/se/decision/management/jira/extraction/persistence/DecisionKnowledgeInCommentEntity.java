package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import java.util.Date;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import net.java.ao.RawEntity;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Table("CondecInComment")
public interface DecisionKnowledgeInCommentEntity extends RawEntity<Long>, Sentence{
	
	@AutoIncrement
	@PrimaryKey("ID")
	long getId();

	void setId(long id);

	@Ignore
	String getSummary();

	@Ignore
	void setSummary(String summary);

	@Ignore
	String getDescription();

	@Ignore
	void setDescription(String description);

	@Ignore
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
	
	@Ignore
	DocumentationLocation getDocumentationLocation();

	@Ignore
	void setDocumentationLocation(DocumentationLocation documentationLocation);

	@Ignore
	void setDocumentationLocation(String documentationLocation);
	
	@Ignore
	void setRelevant(Double prediction);
	
	@Ignore
	void setKnowledgeType(double[] prediction);

	@Ignore
	boolean isPlainText();

	@Ignore
	void setPlainText(boolean isPlainText);

	@Ignore
	String getBody();

	@Ignore
	void setBody(String body);

	@Ignore
	Date getCreated();

	@Ignore
	void setCreated(Date date);
}
