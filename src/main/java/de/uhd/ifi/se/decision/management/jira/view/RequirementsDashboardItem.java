package de.uhd.ifi.se.decision.management.jira.view;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.CommentMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

public class RequirementsDashboardItem  implements ContextProvider {

    @Override
    public void init(final Map<String, String> params) throws PluginParseException {
        /**
         * No special behaviour is foreseen for now.
         */
    }

    @Override
    public Map<String, Object> getContextMap(final Map<String, Object> context) {
        
    	Map<String, Object> newContext = Maps.newHashMap(context);
        Map<String, Object> projectContext = attachProjectsMaps();
        newContext.putAll(projectContext);
        SecureRandom random = new SecureRandom();
        String uid = String.valueOf(random.nextInt(10000));
        String selectId = "condec-dashboard-item-project-selection"+uid;
        newContext.put("selectID", selectId);
        newContext.put("dashboardUID", uid);
        HttpServletRequest req = com.atlassian.jira.web.ExecutingHttpRequest.get();
        if(req.getParameterMap().isEmpty() || req.getParameter("selectPageId")!=null) {
        	String showDiv = "configproject";
        	newContext.put("showDiv",showDiv);
        	return newContext;
        }
        if(req.getParameter("project")!=null && req.getParameter("issuetype")== null) {
        	String showDiv = "configissuetype";
        	newContext.put("showDiv",showDiv);
        	String projectKey = (String) req.getParameter("project");
            newContext.put("projectKey", projectKey);
            Map<String,Object> issueTypeContext = attachIssueTypeMaps(projectKey);
            newContext.putAll(issueTypeContext);
            return newContext;  
        }else {
            String showDiv = "dynamic-content";
            newContext.put("showDiv",showDiv);
            String projectKey = (String) req.getParameter("project");
            String issueTypeId = (String) req.getParameter("issuetype");
            Map<String, Object> values = createValues(projectKey, issueTypeId);
            newContext.putAll(values);
            return newContext;           	
        }    	
    }

    private Map<String,Object> attachIssueTypeMaps(String projectKey){
        Map<String, Object> newContext = new HashMap<>();
        Map<String, String> IssueTypeNameMap = new TreeMap<String, String>(); 
        for(IssueType issuetype : JiraIssueTypeGenerator.getJiraIssueTypes(projectKey)) {
        	String issueTypeId = issuetype.getId();
        	String issueTypeName = issuetype.getName();
        	IssueTypeNameMap.put(issueTypeName,issueTypeId);
        }
        newContext.put("issueTypeNamesMap", IssueTypeNameMap);
        return newContext;    	
    }
    
    private Map<String, Object> attachProjectsMaps() {
        Map<String, Object> newContext = new HashMap<>();
        Map<String, String> projectNameMap = new TreeMap<String, String>();  
        for (Project project : ComponentAccessor.getProjectManager().getProjects()) {     	
                String projectKey = project.getKey();
                String projectName = project.getName();
                projectNameMap.put(projectName,projectKey);        		 
        }
        newContext.put("projectNamesMap", projectNameMap);
        return newContext;
    }
	public Map<String, Object> createValues(String projectKey, String jiraIssueTypeId) {
		Long projectId = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey).getId();
		ApplicationUser loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		
		CommentMetricCalculator calculatorForSentences = new CommentMetricCalculator(projectId, loggedUser);
		CommonMetricCalculator calculator = new CommonMetricCalculator(projectId, loggedUser,
				jiraIssueTypeId);

		Map<String, Object> velocityParams = new HashMap<String, Object>();

		// Push some basic parameters
		String issueTypeName = JiraIssueTypeGenerator.getJiraIssueTypeName(jiraIssueTypeId);
		
		String jiraBaseUrl = ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);

		velocityParams.put("projectName", ComponentAccessor.getProjectManager().getProjectObj(projectId).getName());
		velocityParams.put("jiraBaseUrl", jiraBaseUrl);

		// prepare calculations
		Map<String, String> chartNamesAndPurpose = new HashMap<String, String>();
		Map<String, Object> chartNamesAndData = new HashMap<String, Object>();

		calculateGeneral(calculatorForSentences, calculator, issueTypeName, chartNamesAndPurpose, chartNamesAndData);

		calculateCompleteness(calculator, chartNamesAndPurpose, chartNamesAndData);

		calculateInconsistencies(calculator, chartNamesAndPurpose, chartNamesAndData);

		calculateDistances(calculatorForSentences, calculator, chartNamesAndPurpose, chartNamesAndData);


		// push gathered data to velocity template
		velocityParams.put("chartNamesAndPurpose", chartNamesAndPurpose);
		velocityParams.put("chartNamesAndData", chartNamesAndData);

		return velocityParams;
	}

	private void calculateDistances(CommentMetricCalculator calculatorForSentences, CommonMetricCalculator calculator,
			Map<String, String> chartNamesAndPurpose, Map<String, Object> chartNamesAndData) {
		String chartId = "";
		/* distances */
		chartId = "boxplot-CommentsPerJiraIssue";
		chartNamesAndPurpose.put(chartId, "\\#Comments per JIRA Issue");
		chartNamesAndData.put(chartId, calculatorForSentences.getNumberOfCommentsForJiraIssues());
		chartId = "boxplot-CommitsPerJiraIssue";
		chartNamesAndPurpose.put(chartId, "\\#Commits per JIRA Issue");
		chartNamesAndData.put(chartId, calculator.getNumberOfCommitsForJiraIssues());

		chartId = "boxplot-DecisionsPerJiraIssue";
		chartNamesAndPurpose.put(chartId, "\\#Decisions per JIRA Issue");
		chartNamesAndData.put(chartId,
				calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.DECISION));

		chartId = "boxplot-IssuesPerJiraIssue";
		chartNamesAndPurpose.put(chartId, "\\#Issues per JIRA Issue");
		chartNamesAndData.put(chartId,
				calculator.getNumberOfDecisionKnowledgeElementsForJiraIssues(KnowledgeType.ISSUE));

		chartId = "boxplot-LinkDistanceFromIssue";
		chartNamesAndPurpose.put(chartId, "Link Distance from Issue");
		chartNamesAndData.put(chartId,
				calculator.getLinkDistance(KnowledgeType.ISSUE));

		chartId = "boxplot-LinkDistanceFromAlternative";
		chartNamesAndPurpose.put(chartId, "Link Distance from Alternative");
		chartNamesAndData.put(chartId,
				calculator.getLinkDistance(KnowledgeType.ALTERNATIVE));

		chartId = "boxplot-LinkDistanceFromDecision";
		chartNamesAndPurpose.put(chartId, "Link Distance from Decision");
		chartNamesAndData.put(chartId,
				calculator.getLinkDistance(KnowledgeType.DECISION));
	}

	private void calculateInconsistencies(CommonMetricCalculator calculator, Map<String, String> chartNamesAndPurpose,
			Map<String, Object> chartNamesAndData) {
		String chartId = "";
		/* rationale inconsistencies */
		chartId = "piechartRich-IssuesSolvedByManyDecisions";
		chartNamesAndPurpose.put(chartId, "Issues (=decision problems) solved by more than one decision");
		chartNamesAndData.put(chartId,
				calculator.getDecKnowlElementsOfATypeGroupedByHavingMoreThanOneElementsOfOtherType(KnowledgeType.ISSUE
						, KnowledgeType.DECISION)
		);
	}

	private void calculateCompleteness(CommonMetricCalculator calculator, Map<String, String> chartNamesAndPurpose,
			Map<String, Object> chartNamesAndData) {
		String chartId = "";
		/* towards rationale completeness and inconsistencies */
		chartId = "piechartRich-IssuesSolvedByDecision";
		chartNamesAndPurpose.put(chartId, "How many issues (=decision problems) are solved by a decision?");
		chartNamesAndData.put(chartId,
				calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ISSUE
						, KnowledgeType.DECISION)
		);
		chartId = "piechartRich-DecisionsSolvingIssues";
		chartNamesAndPurpose.put(chartId, "For how many decisions is the issue (=decision problem) documented?");
		chartNamesAndData.put(chartId,
				calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION
						, KnowledgeType.ISSUE)
		);

		chartId = "piechartRich-ProArgumentDocumentedForAlternative";
		chartNamesAndPurpose.put(chartId, "How many alternatives have at least one pro argument documented?");
		chartNamesAndData.put(chartId
				, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ALTERNATIVE
						, KnowledgeType.PRO));

		chartId = "piechartRich-ConArgumentDocumentedForAlternative";
		chartNamesAndPurpose.put(chartId, "How many alternatives have at least one con argument documented?");
		chartNamesAndData.put(chartId
				, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.ALTERNATIVE
						, KnowledgeType.CON));

		chartId = "piechartRich-ProArgumentDocumentedForDecision";
		chartNamesAndPurpose.put(chartId, "How many decisions have at least one pro argument documented?");
		chartNamesAndData.put(chartId
				, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION
						, KnowledgeType.PRO));

		chartId = "piechartRich-ConArgumentDocumentedForDecision";
		chartNamesAndPurpose.put(chartId, "How many decisions have at least one con argument documented?");
		chartNamesAndData.put(chartId
				, calculator.getDecKnowlElementsOfATypeGroupedByHavingElementsOfOtherType(KnowledgeType.DECISION
						, KnowledgeType.CON));
	}

	private void calculateGeneral(CommentMetricCalculator calculatorForSentences, CommonMetricCalculator calculator,
			String issueTypeName, Map<String, String> chartNamesAndPurpose, Map<String, Object> chartNamesAndData) {
		String chartId = "";
		/* general statistics */
		chartId = "piechartInteger-RelevantSentences";
		chartNamesAndPurpose.put(chartId, "Relevance of Sentences in JIRA Issue Comments");
		chartNamesAndData.put(chartId, calculatorForSentences.getNumberOfRelevantSentences());

		chartId = "piechartInteger-KnowledgeTypeDistribution";
		chartNamesAndPurpose.put(chartId, "Distribution of Knowledge Types");
		chartNamesAndData.put(chartId, calculator.getDistributionOfKnowledgeTypes());

		/* selected issue type stats */
		chartId = "piechartRich-DecisionDocumentedForSelectedJiraIssue";
		chartNamesAndPurpose.put(chartId, "For how many JIRA issues of type "+issueTypeName+" is the issue documented?");
		chartNamesAndData.put(chartId, calculator.getLinksToIssueTypeMap(KnowledgeType.ISSUE));

		chartId = "piechartRich-IssueDocumentedForSelectedJiraIssue";
		chartNamesAndPurpose.put(chartId, "For how many JIRA issues of type "+issueTypeName+" is the decision documented?");
		chartNamesAndData.put(chartId, calculator.getLinksToIssueTypeMap(KnowledgeType.DECISION));
	}
	
}