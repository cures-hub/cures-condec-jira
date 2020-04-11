package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.atlassian.jira.exception.PermissionException;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

/**
 * REST resource for view
 */
public interface ViewRest {

	Response getAllFeatureBranchesTree(String projectKey);

	// FIXME: Unit test
	Response getFeatureBranchTree(HttpServletRequest request, String issueKey) throws PermissionException;

	/**
	 * Returns a jstree tree viewer for a list of trees where all root elements have
	 * a specific {@link KnowledgeType}.
	 *
	 * @param projectKey      of a Jira project.
	 * @param rootElementType {@link KnowledgeType} of the root elements.
	 */
	Response getTreeViewer(String projectKey, String rootElementType);

	/**
	 * Returns a jstree tree viewer for a single knowledge element as the root
	 * element. The tree viewer comprises only one tree.
	 */
	Response getTreeViewerForSingleElement(HttpServletRequest request, String jiraIssueKey,
										   FilterSettings filterSettings);

	Response getEvolutionData(HttpServletRequest request, FilterSettings filterSettings);

	Response getTreant(HttpServletRequest request, String elementKey, String depthOfTree, String searchTerm);

	Response getVis(HttpServletRequest request, FilterSettings filterSettings, String elementKey);

	Response getCompareVis(HttpServletRequest request, FilterSettings filterSettings);

	Response getFilterSettings(HttpServletRequest request, String searchTerm, String elementKey);

	Response getDecisionMatrix(HttpServletRequest request, String projectKey);

	Response getDecisionGraph(HttpServletRequest request, FilterSettings filterSettings);

	Response getClassTreant(HttpServletRequest request, String elementKey, String depthOfTree, String searchTerm,
							Boolean checkboxflag, Boolean isIssueView, int minLinkNumber, int maxLinkNumber);

}