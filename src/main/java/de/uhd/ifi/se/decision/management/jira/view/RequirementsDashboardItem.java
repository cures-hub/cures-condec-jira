package de.uhd.ifi.se.decision.management.jira.view;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserProjectHistoryManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.google.common.collect.Maps;

import de.uhd.ifi.se.decision.management.jira.config.JiraIssueTypeGenerator;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.CommentMetricCalculator;
import de.uhd.ifi.se.decision.management.jira.quality.CommonMetricCalculator;
import static com.atlassian.jira.security.Permissions.BROWSE;

import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
//TODO: Center Form
//TODO: Change Style
//TODO: Put Button Next to Select
//TODO: Select Issue Type

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
        if(com.atlassian.jira.web.ExecutingHttpRequest.get().getParameter("project")!=null) {
        	String projectKey = (String) com.atlassian.jira.web.ExecutingHttpRequest.get().getParameter("project");
            Map<String, Object> values = createValues(projectKey);
            newContext.putAll(values);  
            return newContext;  
        }else {
            ApplicationUser loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
            String projectKey = ComponentAccessor.getComponent(UserProjectHistoryManager.class).getCurrentProject(BROWSE, loggedUser).getKey();
            Map<String, Object> values = createValues(projectKey);
            newContext.putAll(values);
            return newContext;           	
        }    	
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
	public Map<String, Object> createValues(String projectKey) {
		Long projectId = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey).getId();
		ApplicationUser loggedUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
		String jiraIssueTypeId = "10200";
		//String jiraIssueTypeId = "10000"; //Hardcoded Issue Type Work Item on Jira Uni server
		
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

		/* rationale inconsistencies */
		chartId = "piechartRich-IssuesSolvedByManyDecisions";
		chartNamesAndPurpose.put(chartId, "Issues (=decision problems) solved by more than one decision");
		chartNamesAndData.put(chartId,
				calculator.getDecKnowlElementsOfATypeGroupedByHavingMoreThanOneElementsOfOtherType(KnowledgeType.ISSUE
						, KnowledgeType.DECISION)
		);

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


		// push gathered data to velocity template
		velocityParams.put("chartNamesAndPurpose", chartNamesAndPurpose);
		velocityParams.put("chartNamesAndData", chartNamesAndData);

		return velocityParams;
	}
	
	/**
	 * Seems to be uncalled, but is automatically called to transfer velocity
	 * variables.
	 */
	/*
	@Override
	@SuppressWarnings("rawtypes")
	public void validate(ProjectActionSupport action, Map params) {
		this.projectId = ParameterUtils.getLongParam(params, "selectedProjectId");
		this.jiraIssueTypeId = ParameterUtils.getStringParam(params, "issueType");
	}
	*/
}