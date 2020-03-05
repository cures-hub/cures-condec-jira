package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.GenericLinkManager;

/**
 * Interface for knowledge elements, e.g. knowledge elements or requirements.
 * These elements are nodes of the knowledge graph.
 * 
 * @see KnowledgeGraph
 * @see Link
 */
@JsonDeserialize(as = KnowledgeElementImpl.class)
public interface KnowledgeElement {

    /**
     * Get the id of the knowledge element. This id is the internal database id.
     * When using Jira issues to persist knowledge, this id is different to the
     * project internal id that is part of the key.
     *
     * @return id of the knowledge element.
     */
    long getId();

    /**
     * Set the id of the knowledge element. This id is the internal database id.
     * When using Jira issues to persist knowledge, this id is different to the
     * project internal id that is part of the key.
     *
     * @param id of the knowledge element.
     */
    void setId(long id);

    /**
     * Get the summary of the knowledge element. The summary is a short description
     * of the element.
     *
     * @return summary of the knowledge element.
     */
    String getSummary();

    /**
     * Set the summary of the knowledge element. The summary is a short description
     * of the element.
     *
     * @param summary of the knowledge element.
     */
    void setSummary(String summary);

    /**
     * Get the description of the knowledge element. The description provides
     * details about the element. When using Jira issues to persist knowledge, it
     * can include images and other fancy stuff.
     *
     * @return description of the knowledge element.
     */
    String getDescription();

    /**
     * Set the description of the knowledge element. The description provides
     * details about the element. When using Jira issues to persist knowledge, it
     * can include images and other fancy stuff.
     *
     * @param description of the knowledge element.
     */
    void setDescription(String description);

    /**
     * Get the type of the knowledge element. For example, prominent types are
     * decision, alternative, issue, and argument.
     *
     * @see KnowledgeType
     * @return type of the knowledge element.
     */
    KnowledgeType getType();

    /**
     * Get the type of the knowledge element as a String. For example, prominent
     * types are decision, alternative, issue, and argument. This methods returns
     * the type of Jira issues that are no knowledge elements.
     *
     * @see KnowledgeType
     * @return type of the knowledge element.
     */
    String getTypeAsString();

    /**
     * Set the type of the knowledge element. For example, prominent types are
     * decision, alternative, issue, and argument.
     *
     * @see KnowledgeType
     * @param type of the knowledge element.
     */
    void setType(KnowledgeType type);

    /**
     * Set the type of the knowledge element. For example, prominent types are
     * decision, alternative, issue, and argument.
     *
     * @see KnowledgeType
     * @param type of the knowledge element.
     */
    void setType(String type);

    /**
     * Get the project that the knowledge element belongs to. The project is a Jira
     * project that is extended with settings for this plug-in, for example, whether
     * the plug-in is activated for the project.
     *
     * @see DecisionKnowledgeProject
     * @return project.
     */
    DecisionKnowledgeProject getProject();

    /**
     * Set the project that the knowledge element belongs to. The project is a Jira
     * project that is extended with settings for this plug-in, for example, whether
     * the plug-in is activated for the project.
     *
     * @see DecisionKnowledgeProject
     * @param project knowledge project.
     */
    void setProject(DecisionKnowledgeProject project);

    /**
     * Set the project that the knowledge element belongs to via its key. The
     * project is a Jira project that is extended with settings for this plug-in,
     * for example, whether the plug-in is activated for the project.
     *
     * @see DecisionKnowledgeProject
     * @param projectKey key of Jira project.
     */
    void setProject(String projectKey);

    /**
     * Get the key of the knowledge element.
     *
     * @return key of the knowledge element. The key is composed of
     *         projectKey-project internal id.
     */
    String getKey();

    /**
     * Set the key of the knowledge element.
     *
     * @param key of the knowledge element. The key is composed of
     *            projectKey-project internal id.
     */
    void setKey(String key);

    /**
     * Get the documentation location of the knowledge element. For example,
     * knowledge can be documented in commit messages or in the comments to a Jira
     * issue.
     *
     * @see DocumentationLocation
     * @return documentation location of the knowledge element.
     */
    DocumentationLocation getDocumentationLocation();

    /**
     * Get the documentation location of the knowledge element. For example,
     * knowledge can be documented in commit messages or in the comments to a Jira
     * issue.
     *
     * @see DocumentationLocation
     * @return documentation location of the knowledge element as a String.
     */
    String getDocumentationLocationAsString();

    /**
     * Set the documentation location of the knowledge element. For example,
     * knowledge can be documented in commit messages or in the comments to a Jira
     * issue.
     *
     * @see DocumentationLocation
     * @param documentationLocation of the knowledge element.
     */
    void setDocumentationLocation(DocumentationLocation documentationLocation);

    /**
     * Set the documentation location of the knowledge element. For example,
     * knowledge can be documented in commit messages or in the comments to a Jira
     * issue.
     *
     * @see DocumentationLocation
     * @param documentationLocation of the knowledge element.
     */
    void setDocumentationLocation(String documentationLocation);

    /**
     * Get the URL to the knowledge element.
     *
     * @return an URL as String.
     */
    String getUrl();

    /**
     * Get the creation date of the knowledge element.
     *
     * @return creation date.
     */
    Date getCreated();

    /**
     * Set the creation date of the knowledge element.
     *
     * @param date of creation.
     */
    void setCreated(Date date);

    /**
     * Returns the creator of an element as an application user object.
     *
     * @return creator of an element as an {@link ApplicationUser} object.
     */
    ApplicationUser getCreator();

    /**
     * Get the close date fo the knowledge element.
     *
     * @return close date.
     */
    Date getClosed();

    /**
     * Set the close date of the knowledge element.
     *
     * @param date
     */
    void setClosed(Date date);

    /**
     * Check whether the element exists in database.
     * 
     * @return true if the element exists in database.
     */
    boolean existsInDatabase();

    /**
     * Get the Jira issue that the knowledge element or irrelevant text is part of.
     * 
     * @return Jira issue.
     */
    Issue getJiraIssue();

    /**
     * Returns all links (=edges) of this element in the {@link KnowledgeGraph}.
     * 
     * @param element node in the {@link KnowledgeGraph}.
     * @return list of {@link} objects, does contain Jira {@link IssueLink}s and
     *         generic links.
     * 
     * @see GenericLinkManager
     */
    List<Link> getLinks();

    /**
     * Determines whether an element is linked to at least one other decision
     * knowledge element.
     * 
     * @return id of first link that is found.
     */
    long isLinked();

    /**
     * Get the status of the knowledge element. For example, the status for issues
     * can be solved or unsolved.
     *
     * @see KnowledgeStatus
     * @return status of the knowledge element.
     */
    KnowledgeStatus getStatus();

    /**
     * Get the status of the knowledge element. For example, the status for issues
     * can be solved or unsolved.
     *
     * @see KnowledgeStatus
     * @return status of the knowledge element.
     */
    String getStatusAsString();

    /**
     * Set the status of the knowledge element. For example, the status for issues
     * can be solved or unsolved.
     *
     * @see KnowledgeStatus
     * @param status of the knowledge element.
     */
    void setStatus(KnowledgeStatus status);

    /**
     * Set the status of the knowledge element. For example, the status for issues
     * can be solved or unsolved.
     *
     * @see KnowledgeStatus
     * @param status of the knowledge element.
     */
    void setStatus(String status);

    /**
     * Get the list of groups assigned to this decision
     *
     * @return List<String> of groups
     */
    List<String> getDecisionGroup();

    /**
     * Set the list of groups assigned to this decision
     *
     * @param List<String> of groups
     */
    void setDecisionGroup(List<String> decisionGroup);

    /**
     * Add a group to the list of groups
     * 
     * @param The group to add as string
     */
    void addDecisionGroup(String group);

    /**
     * Remove a group from the list of groups
     * 
     * @param The group to remove as string
     */
    void removeDecisionGroup(String group);

}