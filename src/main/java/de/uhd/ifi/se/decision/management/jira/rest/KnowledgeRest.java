package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.JiraFilter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * REST resource: Enables creation, editing, and deletion of decision knowledge
 * elements and their links
 */
public interface KnowledgeRest {

	Response getDecisionKnowledgeElement(long id, String projectKey, String documentationLocation);

	/**
	 * Returns all unlinked elements of the knowledge element for a project. Sorts
	 * the elements according to their similarity and their likelihood that they
	 * should be linked.
	 * 
	 * TODO Sorting according to the likelihood that they should be linked.
	 * 
	 * @issue How can the sorting be implemented?
	 *
	 * @param id
	 *            of the {@link KnowledgeElement} in database.
	 * @param documentationLocation
	 *            of the {@link KnowledgeElement}, e.g. "i" for Jira issue,
	 *            see {@link DocumentationLocation}.
	 * @param projectKey
	 *            of the Jira project, see {@link DecisionKnowledgeProject}.
	 * @return list of unlinked elements, sorted by the likelihood that they should
	 *         be linked.
	 */
	Response getUnlinkedElements(long id, String projectKey, String documentationLocation);

	/**
	 * Creates a new {@link KnowledgeElement}. The decision knowledge
	 * element can either be documented as a separate Jira issue (documentation
	 * location "i") or in the description/a comment of an existing Jira issue
	 * (documentation location "s").
	 * 
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param element
	 *            {@link KnowledgeElement} object with attributes, such as
	 *            summary, description (optional), {@link DocumentationLocation},
	 *            and {@link KnowledgeType}.
	 * @param idOfExistingElement
	 *            optional parameter. Identifier of a parent element that the new
	 *            element should be linked with. Either the id or the key needs to
	 *            be passed, not both.
	 * @param documentationLocationOfExistingElement
	 *            optional parameter. Documentation location of a parent element
	 *            that the new element should be linked with.
	 * @param keyOfExistingElement
	 *            optional parameter. Key of a parent element that the new element
	 *            should be linked with. Either the id or the key needs to be
	 *            passed, not both.
	 * @return new {@link KnowledgeElement} with its internal database id
	 *         set.
	 */
	Response createDecisionKnowledgeElement(HttpServletRequest request, KnowledgeElement element,
			long idOfExistingElement, String documentationLocationOfExistingElement, String keyOfExistingElement);

	Response updateDecisionKnowledgeElement(HttpServletRequest request, KnowledgeElement element,
			long idOfParentElement, String documentationLocationOfParentElement);

	Response deleteDecisionKnowledgeElement(HttpServletRequest request,
			KnowledgeElement decisionKnowledgeElement);

	Response createLink(HttpServletRequest request, String projectKey, String knowledgeTypeOfChild, long idOfParent,
			String documentationLocationOfParent, long idOfChild, String documentationLocationOfChild,
			String linkTypeName);

	Response deleteLink(HttpServletRequest request, String projectKey, Link link);

	/**
	 * Returns a list of all elements that match the query. The query can either be
	 * in Jira Query Language (JQL) or a predefinded {@link JiraFilter}.
	 * 
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param query
	 *            either in in Jira Query Language (JQL) or a predefinded
	 *            {@link JiraFilter}.
	 * @return list of all elements that match the query.
	 */
	Response getElements(HttpServletRequest request, String projectKey, String query);

	Response createIssueFromSentence(HttpServletRequest request, KnowledgeElement decisionKnowledgeElement);

	Response setSentenceIrrelevant(HttpServletRequest request, KnowledgeElement decisionKnowledgeElement);

	Response getSummarizedCode(long id, String projectKey, String documentationLocation, int probability);
}