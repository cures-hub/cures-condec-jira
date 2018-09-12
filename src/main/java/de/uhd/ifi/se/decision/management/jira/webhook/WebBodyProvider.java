package de.uhd.ifi.se.decision.management.jira.webhook;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.view.treant.Chart;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Creates the Body for the Webhook.
 */
public class WebBodyProvider {

    private PostMethod postMethod;
    private Treant treant;
    private DecisionKnowledgeProject project;
    private String secret;
    private String identifierKey;

    public WebBodyProvider(String projectKey, String identifierKey) {
        postMethod = new PostMethod();
        if (projectKey == null || identifierKey == null) {
            return;
        }
        this.identifierKey = identifierKey;
        this.secret = ConfigPersistence.getWebhookSecret(projectKey);
        project = new DecisionKnowledgeProjectImpl(projectKey);

    }

    public PostMethod getPostMethodForIssueKey() {
        if (project == null || identifierKey == null) {
            return new PostMethod();
        }
        treant = new Treant(project.getProjectKey(), identifierKey, 4);
        project = new DecisionKnowledgeProjectImpl(project.getProjectKey());
        createJsonStringForIssueKey();
        return postMethod;
    }

    public PostMethod getPostMethodForGitHash() {
        if (project == null || identifierKey == null) {
            return new PostMethod();
        }
        treant = new Treant(project.getProjectKey(), identifierKey, 4);
        project = new DecisionKnowledgeProjectImpl(project.getProjectKey());
        creatJsonStringForGitHash();
        return postMethod;
    }

    /**
     * {
     * "issueKey": "string",
     * " ConDecTree": { ..TreantJS JSON Config.. }
     * }
     */
    private void createJsonStringForIssueKey()  {
        JSONObject treantJSON = createTreantJsonString();
        StringRequestEntity requestEntity = null;
        try {
            requestEntity = new StringRequestEntity(treantJSON.toString(), "application/json", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        postMethod.setRequestEntity(requestEntity);
        Header header = new Header();
        header.setName("X-Hub-Signature");
        header.setValue("sha256=" + createHashedPayload(treantJSON.toString(), project.getWebhookSecret()));
        postMethod.setRequestHeader(header);
    }

    /**
     * {
     * "commit": {
     * "hash": "string"
     * },
     * "ConDecTree": {TreantJS JSON Config/Data}
     * }
     */
    private void creatJsonStringForGitHash() {
        String gitHashString;
        try {
            gitHashString = new JSONObject().put("hash", identifierKey).toString();
            JSONObject treantJSON = createTreantJsonString();
            NameValuePair commitPair = new NameValuePair("commit", gitHashString);
            NameValuePair conDeTreePair = new NameValuePair("ConDecTree", treantJSON.toString());
            NameValuePair[] bodySet = new NameValuePair[2];
            bodySet[0] = commitPair;
            bodySet[1] = conDeTreePair;
            postMethod.setRequestBody(bodySet);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject createTreantJsonString() {
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
            treantJSON.put("chart", chartJSON);
            treantJSON.put("nodeStructure", nodeStructure);
            String payload = "{\"issueKey\": \"" + this.identifierKey + "\", \"ConDecTree\": " + treantJSON.toString() + "}";
            JSONObject JSONPayload = new JSONObject(payload);
            return JSONPayload;
            //return treantJSON;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return chartJSON;
    }

    public static String createHashedPayload(String data, String key) {
        final String algo = "HMACSHA256";
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algo);
        Mac mac = null;
        try {
            mac = Mac.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            mac.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return toHexString(mac.doFinal(data.getBytes()));
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
