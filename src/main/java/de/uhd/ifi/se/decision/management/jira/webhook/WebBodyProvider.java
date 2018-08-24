package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.view.treant.Chart;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;
/**
 * Creates the Body for the Webhook.
 *
 * {
 *  "commit": {
 *     "hash": "string"
 * },
 *  "ConDeTree": {TreantJS JSON Config/Data}
 *  }
 *  or
 * {
 *  "issueKey": "string",
 *  " ConDeTree": { ..TreantJS JSON Config.. }
 * }
 */
public class WebBodyProvider {

    private PostMethod postMethod;
    private Treant treant;
    private DecisionKnowledgeProject project;

    public WebBodyProvider(String projectKey, String elementKey) {
        postMethod = new PostMethod();
        if(projectKey == null || elementKey == null){
            return;
        }
        this.treant = new Treant(projectKey, elementKey, 4);
        this.project = new DecisionKnowledgeProjectImpl(projectKey);
        createJsonString(elementKey);
    }

    private void createJsonString(String issueKey){
        String issueKeyString =  new JSONObject().put("issueKey",issueKey).toString();
        NameValuePair issuePair = new NameValuePair("commit",issueKeyString);
        JSONObject treantJSON = createTreantJsonString();
        NameValuePair conDeTreePair = new NameValuePair("ConDeTree",treantJSON.toString());
        NameValuePair[] bodySet = new NameValuePair[2];
        bodySet[0] = issuePair;
        bodySet[1] = conDeTreePair;
        postMethod.setRequestBody(bodySet);
    }

    private JSONObject createTreantJsonString(){
        Chart chart = this.treant.getChart();
        JSONObject chartJSON = new JSONObject();
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
    }

    public PostMethod getPostMethod() {
        return postMethod;
    }
}
