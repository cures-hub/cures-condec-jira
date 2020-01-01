package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.filtering.JiraFilter;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * REST resource: Enables creation, editing, and deletion of decision knowledge
 * elements and their links
 */
public interface KnowledgeRest {

	Response getDecisionKnowledgeElement(long id, String projectKey, String documentationLocation);

	Response getAdjacentElements(long id, String projectKey, String documentationLocation);

	Response getUnlinkedElements(long id, String projectKey, String documentationLocation);

	Response createUnlinkedDecisionKnowledgeElement(HttpServletRequest request, DecisionKnowledgeElement element);

	Response createDecisionKnowledgeElement(HttpServletRequest request, DecisionKnowledgeElement element,
			long idOfExistingElement, String documentationLocationOfExistingElement, String keyOfExistingElement);

	Response updateDecisionKnowledgeElement(HttpServletRequest request, DecisionKnowledgeElement element,
			long idOfParentElement, String documentationLocationOfParentElement);

	Response deleteDecisionKnowledgeElement(HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement);

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

	Response createIssueFromSentence(HttpServletRequest request, DecisionKnowledgeElement decisionKnowledgeElement);

	Response setSentenceIrrelevant(HttpServletRequest request, DecisionKnowledgeElement decisionKnowledgeElement);

	Response getSummarizedCode(long id, String projectKey, String documentationLocation, int probability);
}