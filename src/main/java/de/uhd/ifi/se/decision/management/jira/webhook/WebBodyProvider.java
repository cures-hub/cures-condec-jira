package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.view.treant.Chart;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
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

    private String jsonString;
    private Treant treant;
    private DecisionKnowledgeProject project;

    public WebBodyProvider(String projectKey, String elementKey) {
        this.treant = new Treant(projectKey, elementKey, 4);
        this.project = new DecisionKnowledgeProjectImpl(projectKey);
        createJsonString();
    }

    private void createJsonString(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("commit", new JSONObject().put("hash",project.getWebhookSecret()));
        JSONObject treantJSON = createTreantJsonString();
        jsonObject.put("ConDeTree",treantJSON);
        this.jsonString = jsonObject.toString();
    }

    private JSONObject createTreantJsonString(){
        JSONObject treantJSON = new JSONObject();

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
        return treantJSON;
    }

    public String getJsonString() {
        return jsonString;
    }
}
