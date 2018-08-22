package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
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
        //TODO Create TreantJS JSON Config/Data
        JSONObject treantJSON = new JSONObject();
        treantJSON.put("Data", "{TreantJS JSON Config/Data}");
        jsonObject.put("ConDeTree",treantJSON);

        this.jsonString = jsonObject.toString();
    }

    public String getJsonString() {
        return jsonString;
    }
}
