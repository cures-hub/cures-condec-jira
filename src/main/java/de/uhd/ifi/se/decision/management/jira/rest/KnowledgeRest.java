package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

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

	Response getElements(HttpServletRequest request, boolean allTrees, String projectKey, String query);

	Response createIssueFromSentence(HttpServletRequest request, DecisionKnowledgeElement decisionKnowledgeElement);

	Response setSentenceIrrelevant(HttpServletRequest request, DecisionKnowledgeElement decisionKnowledgeElement);

	Response getSummarizedCode(long id, String projectKey, String documentationLocation, int probability);

	// TODO Remove and replace with updateElement method
	Response setStatus(HttpServletRequest request, String stringStatus,
			DecisionKnowledgeElement decisionKnowledgeElement);
}