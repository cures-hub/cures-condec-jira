package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.view.treant.Chart;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Creates the Body for the Webhook.
 */
public class WebBodyProvider {

    private PostMethod postMethod;
    private Treant treant;
    private DecisionKnowledgeProject project;
    private String identifierKey;

    public WebBodyProvider(String projectKey, String identifierKey) {
        postMethod = new PostMethod();
        if(projectKey == null || identifierKey == null){
            return;
        }
        this.identifierKey = identifierKey;
        project = new DecisionKnowledgeProjectImpl(projectKey);

    }

    public PostMethod getPostMethodForIssueKey() {
        if(project == null || identifierKey == null){
            return new PostMethod();
        }
        treant = new Treant(project.getProjectKey(), identifierKey, 4);
        project = new DecisionKnowledgeProjectImpl(project.getProjectKey());
        createJsonStringForIssueKey();
        return postMethod;
    }

    public PostMethod getPostMethodForGitHash() {
        if(project == null || identifierKey == null){
            return new PostMethod();
        }
        treant = new Treant(project.getProjectKey(), identifierKey, 4);
        project = new DecisionKnowledgeProjectImpl(project.getProjectKey());
        creatJsonStringForGitHash();
        return postMethod;
    }

    /**
     * {
     *  "issueKey": "string",
     *  " ConDeTree": { ..TreantJS JSON Config.. }
     * }
     */
    private void createJsonStringForIssueKey(){
        NameValuePair issuePair = new NameValuePair("issueKey",identifierKey);
        JSONObject treantJSON = createTreantJsonString();
        NameValuePair conDeTreePair = new NameValuePair("ConDeTree",treantJSON.toString());
        NameValuePair[] bodySet = new NameValuePair[2];
        bodySet[0] = issuePair;
        bodySet[1] = conDeTreePair;
        postMethod.setRequestBody(bodySet);
    }

    /**
     * {
     *  "commit": {
     *     "hash": "string"
     * },
     *  "ConDeTree": {TreantJS JSON Config/Data}
     *  }
     */
    private void creatJsonStringForGitHash(){
        String gitHashString;
		try {
			gitHashString = new JSONObject().put("hash", identifierKey).toString();
	        JSONObject treantJSON = createTreantJsonString();
	        NameValuePair commitPair = new NameValuePair("commit", gitHashString);
	        NameValuePair conDeTreePair = new NameValuePair("ConDeTree",treantJSON.toString());
	        NameValuePair[] bodySet = new NameValuePair[2];
	        bodySet[0] = commitPair;
	        bodySet[1] = conDeTreePair;
	        postMethod.setRequestBody(bodySet);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    private JSONObject createTreantJsonString(){
        Chart chart = this.treant.getChart();
        JSONObject chartJSON = new JSONObject();
        try {
			chartJSON.put("container", chart.getContainer());
	        chartJSON.put("connectors", new JSONObject().put("type", "straight"));
	        chartJSON.put("rootOrientation", chart.getRootOrientation());
	        chartJSON.put("levelSeparation", chart.getLevelSeparation());
	        chartJSON.put("siblingSeparation", chart.getSiblingSeparation());
	        chartJSON.put("subTreeSeparation", chart.getSubTreeSeparation());
	        chartJSON.put("node", new JSONObject().put("collapsable", "true"));

	        JSONObject nodeStructure = new JSONObject();
	        nodeStructure.put("text", new JSONObject(treant.getNodeStructure().getNodeContent()));
	        nodeStructure.put("children", treant.getNodeStructure().getChildren());

	        JSONObject treantJSON = new JSONObject();
	        treantJSON.put("chart",chartJSON);
	        treantJSON.put("nodeStructure", nodeStructure);
	        return treantJSON;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chartJSON;
    }
}
