package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.atlassian.jira.exception.PermissionException;

import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;

/**
 * REST resource for view
 */
public interface ViewRest {

	Response getAllFeatureBranchesTree(String projectKey);

	// FIXME: Unit test
	Response getFeatureBranchTree(HttpServletRequest request, String issueKey) throws PermissionException;

	Response getTreeViewer(String projectKey, String rootElementType);

	Response getTreeViewer2(String issueKey, String showRelevant);

	Response getEvolutionData(HttpServletRequest request, FilterSettings filterSettings);

	Response getTreant(String elementKey, String depthOfTree, String searchTerm, HttpServletRequest request);

	Response getVis(HttpServletRequest request, FilterSettings filterSettings, String elementKey);

	Response getCompareVis(HttpServletRequest request, FilterSettings filterSettings);

	Response getFilterSettings(HttpServletRequest request, String searchTerm, String elementKey);

	Response getDecisionMatrix(HttpServletRequest request, String projectKey);

	Response getDecisionGraph(HttpServletRequest request, FilterSettings filterSettings, String projectKey);
}