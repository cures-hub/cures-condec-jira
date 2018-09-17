package de.uhd.ifi.se.decision.management.jira.webhook;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONException;
import org.json.JSONObject;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.view.treant.Chart;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;

/**
 * Creates the content submitted by the webhook. The content consists of a key
 * value pair. The key is either an issue id or a commit SHA id. The value is
 * the Treant JSON String.
 */
public class WebhookContentProvider {

	private DecisionKnowledgeProject project;
	private String elementKey;

	public WebhookContentProvider(String projectKey, String elementKey) {
		if (projectKey == null || elementKey == null) {
			return;
		}
		this.elementKey = elementKey;
		this.project = new DecisionKnowledgeProjectImpl(projectKey);
	}

	/**
	 * Create post method for webhook
	 * 
	 * @param elementKey
	 *            key of the changed element.
	 * @return post method ready to be posted
	 */
	public PostMethod createWebhookContentForChangedElement() {
		PostMethod postMethod = new PostMethod();
		if (project == null || elementKey == null) {
			return postMethod;
		}
		JSONObject treantJSON = createTreantJsonString();
		try {
			StringRequestEntity requestEntity = new StringRequestEntity(treantJSON.toString(), "application/json", "UTF-8");
			postMethod.setRequestEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Header header = new Header();
		header.setName("X-Hub-Signature");
		header.setValue("sha256=" + createHashedPayload(treantJSON.toString(), project.getWebhookSecret()));
		postMethod.setRequestHeader(header);
		return postMethod;
	}

	/**
	 * Creates the Treant JSON data transmitted via webhook
	 * 
	 * @param elementKey
	 *            key of the changed element.
	 * @return JSON object containing the following String: { "issueKey": "string",
	 *         " ConDecTree": {TreantJS JSON config and data} }
	 */
	private JSONObject createTreantJsonString() {
		Treant treant = new Treant(project.getProjectKey(), elementKey, 4);
		Chart chart = treant.getChart();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("container", chart.getContainer());
			jsonObject.put("connectors", new JSONObject().put("type", "straight"));
			jsonObject.put("rootOrientation", chart.getRootOrientation());
			jsonObject.put("levelSeparation", chart.getLevelSeparation());
			jsonObject.put("siblingSeparation", chart.getSiblingSeparation());
			jsonObject.put("subTreeSeparation", chart.getSubTreeSeparation());
			jsonObject.put("node", new JSONObject().put("collapsable", "true"));

			JSONObject nodeStructure = new JSONObject();
			nodeStructure.put("text", new JSONObject(treant.getNodeStructure().getNodeContent()));
			nodeStructure.put("children", treant.getNodeStructure().getChildren());

			JSONObject treantJSON = new JSONObject();
			treantJSON.put("chart", jsonObject);
			treantJSON.put("nodeStructure", nodeStructure);
			String payload = "{\"issueKey\": \"" + this.elementKey + "\", \"ConDecTree\": " + treantJSON.toString()
					+ "}";
			jsonObject = new JSONObject(payload);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public static String createHashedPayload(String data, String key) {
		final String hashingAlgorithm = "HMACSHA256";
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), hashingAlgorithm);
		Mac mac = null;
		try {
			mac = Mac.getInstance(hashingAlgorithm);
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
		String formattedString = formatter.toString();
		formatter.close();
		return formattedString;
	}
}
