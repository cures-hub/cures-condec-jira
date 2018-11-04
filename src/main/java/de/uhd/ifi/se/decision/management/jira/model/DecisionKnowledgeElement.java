package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Interface for decision knowledge elements
 */
@JsonDeserialize(as = DecisionKnowledgeElementImpl.class)
public interface DecisionKnowledgeElement {

	/**
	 * Get the id of the decision knowledge element. This id is the internal
	 * database id. When using JIRA issues to persist decision knowledge, this id is
	 * different to the project internal id that is part of the key.
	 *
	 * @return id of the decision knowledge element.
	 */
	long getId();

	/**
	 * Set the id of the decision knowledge element. This id is the internal
	 * database id. When using JIRA issues to persist decision knowledge, this id is
	 * different to the project internal id that is part of the key.
	 *
	 * @param id
	 *            of the decision knowledge element.
	 */
	void setId(long id);

	/**
	 * Get the summary of the decision knowledge element. The summary is a short
	 * description of the element.
	 *
	 * @return summary of the decision knowledge element.
	 */
	String getSummary();

	/**
	 * Set the summary of the decision knowledge element. The summary is a short
	 * description of the element.
	 *
	 * @param summary
	 *            of the decision knowledge element.
	 */
	void setSummary(String summary);

	/**
	 * Get the description of the decision knowledge element. The description
	 * provides details about the element. When using JIRA issues to persist
	 * decision knowledge, it can include images and other fancy stuff.
	 *
	 * @return description of the decision knowledge element.
	 */
	String getDescription();

	/**
	 * Set the description of the decision knowledge element. The description
	 * provides details about the element. When using JIRA issues to persist
	 * decision knowledge, it can include images and other fancy stuff.
	 *
	 * @param description
	 *            of the decision knowledge element.
	 */
	void setDescription(String description);

	/**
	 * Get the type of the decision knowledge element. For example, prominent types
	 * are decision, alternative, issue, and argument.
	 *
	 * @see KnowledgeType
	 * @return type of the decision knowledge element.
	 */
	KnowledgeType getType();

	/**
	 * Get the type of the decision knowledge element as a String. For example,
	 * prominent types are decision, alternative, issue, and argument. This methods returns
	 * the type of JIRA issues that are no decision knowledge elements.
	 *
	 * @see KnowledgeType
	 * @return type of the decision knowledge element.
	 */
	String getTypeAsString();

	/**
	 * Set the type of the decision knowledge element. For example, prominent types
	 * are decision, alternative, issue, and argument.
	 *
	 * @see KnowledgeType
	 * @param type
	 *            of the decision knowledge element.
	 */
	void setType(KnowledgeType type);

	/**
	 * Set the type of the decision knowledge element. For example, prominent types
	 * are decision, alternative, issue, and argument.
	 *
	 * @see KnowledgeType
	 * @param type
	 *            of the decision knowledge element.
	 */
	void setType(String type);

	/**
	 * Get the project that the decision knowledge element belongs to. The project
	 * is a JIRA project that is extended with settings for this plug-in, for
	 * example, whether the plug-in is activated for the project.
	 *
	 * @see DecisionKnowledgeProject
	 * @return project.
	 */
	DecisionKnowledgeProject getProject();

	/**
	 * Set the project that the decision knowledge element belongs to. The project
	 * is a JIRA project that is extended with settings for this plug-in, for
	 * example, whether the plug-in is activated for the project.
	 *
	 * @see DecisionKnowledgeProject
	 * @param project
	 *            decision knowledge project.
	 */
	void setProject(DecisionKnowledgeProject project);

	/**
	 * Set the project that the decision knowledge element belongs to via its key.
	 * The project is a JIRA project that is extended with settings for this
	 * plug-in, for example, whether the plug-in is activated for the project.
	 *
	 * @see DecisionKnowledgeProject
	 * @param projectKey
	 *            key of JIRA project.
	 */
	void setProject(String projectKey);

	/**
	 * Get the key of the decision knowledge element.
	 *
	 * @return key of the decision knowledge element. The key is composed of
	 *         projectKey-project internal id.
	 */
	String getKey();

	/**
	 * Set the key of the decision knowledge element.
	 *
	 * @param key
	 *            of the decision knowledge element. The key is composed of
	 *            projectKey-project internal id.
	 */
	void setKey(String key);

	/**
	 * Get all linked elements of the decision knowledge element. It does not matter
	 * whether this decision knowledge element is the source or the destination
	 * element.
	 *
	 * @return list of linked elements.
	 */
	List<DecisionKnowledgeElement> getLinkedElements();

	/**
	 * Get all links where this decision knowledge element is the source element.
	 *
	 * @see Link
	 * @return list of links where this decision knowledge element is the source
	 *         element.
	 */
	List<Link> getOutwardLinks();

	/**
	 * Get all links where this decision knowledge element is the destination
	 * element.
	 *
	 * @see Link
	 * @return list of links where this decision knowledge element is the
	 *         destination element.
	 */
	List<Link> getInwardLinks();

	/**
	 * Get the documentation location of the decision knowledge element. For
	 * example, decision knowledge can be documented in commit messages or in the
	 * comments to a JIRA issue.
	 *
	 * @see DocumentationLocation
	 * @return documentation location of the decision knowledge element.
	 */
	DocumentationLocation getDocumentationLocation();

	/**
	 * Set the documentation location of the decision knowledge element. For
	 * example, decision knowledge can be documented in commit messages or in the
	 * comments to a JIRA issue.
	 *
	 * @see DocumentationLocation
	 * @param documentationLocation
	 *            of the decision knowledge element.
	 */
	void setDocumentationLocation(DocumentationLocation documentationLocation);

	/**
	 * Set the documentation location of the decision knowledge element. For
	 * example, decision knowledge can be documented in commit messages or in the
	 * comments to a JIRA issue.
	 *
	 * @see DocumentationLocation
	 * @param documentationLocation
	 *            of the decision knowledge element.
	 */
	void setDocumentationLocation(String documentationLocation);

	/**
	 * Get the creation date of the decision knowledge element.
	 *
	 * @return creation date.
	 */
	Date getCreated();

	/**
	 * Set the creation date of the decision knowledge element.
	 *
	 * @param date
	 *            of creation.
	 */
	void setCreated(Date date);
}