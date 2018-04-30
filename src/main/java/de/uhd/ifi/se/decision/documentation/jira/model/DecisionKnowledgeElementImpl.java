package de.uhd.ifi.se.decision.documentation.jira.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import org.codehaus.jackson.annotate.JsonProperty;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

/**
 * @description Model class for decision knowledge elements
 */
public class DecisionKnowledgeElementImpl implements DecisionKnowledgeElement {

	private long id;	
	private String summary;
	private String description;
	private KnowledgeType type;
	private String projectKey;
	private String key;
	
	public DecisionKnowledgeElementImpl() {

	}

	public DecisionKnowledgeElementImpl(long id, String summary, String description, KnowledgeType type, String projectKey, String key) {
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.type = type;
		this.projectKey = projectKey;
		this.key = key;
	}

	public DecisionKnowledgeElementImpl(Issue issue) {
		this.id = issue.getId();
		this.summary = issue.getSummary();
		this.description = issue.getDescription();
		this.type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
		this.projectKey = issue.getProjectObject().getKey();
		this.key = issue.getKey();
	}

	@XmlElement(name = "id")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@XmlElement(name = "summary")
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public KnowledgeType getType() {
		return type;
	}

	@XmlElement(name = "type")
	public String getTypeAsString() {
		return type.toString();
	}

	public void setType(KnowledgeType type) {
		this.type = type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = KnowledgeType.getKnowledgeType(type);
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	@XmlElement(name = "key")
	public String getKey() {
		if (this.key == null) {
			return this.projectKey + "-" + this.id;
		}
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public KnowledgeType getSuperType() {
		return this.type.getSuperType();
	}

	public List<DecisionKnowledgeElement> getChildren() {
		StrategyProvider strategyProvider = new StrategyProvider();
		PersistenceStrategy strategy = strategyProvider.getStrategy(this.getProjectKey());
		return strategy.getChildren(this);
	}
}