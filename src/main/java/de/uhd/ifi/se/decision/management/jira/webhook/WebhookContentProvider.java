package de.uhd.ifi.se.decision.management.jira.webhook;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jackson.map.ObjectMapper;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;

/**
 * Creates the content submitted via the webhook. The content consists of a key
 * value pair. The key is an issue id. The value is the Treant JSON String.
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
	 * @return post method ready to be posted
	 */
	public PostMethod createWebhookContentForChangedElement() {
		PostMethod postMethod = new PostMethod();
		if (project == null || elementKey == null) {
			return postMethod;
		}
		String payload = createPayload();
		try {
			StringRequestEntity requestEntity = new StringRequestEntity(payload, "application/json", "UTF-8");
			postMethod.setRequestEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Header header = new Header();
		header.setName("X-Hub-Signature");
		header.setValue("sha256=" + createHashedPayload(payload, project.getWebhookSecret()));
		postMethod.setRequestHeader(header);
		return postMethod;
	}

	/**
	 * Creates the key value JSON String transmitted via webhook
	 * 
	 * @return JSON String with the following pattern: { "issueKey": {String},
	 *         "ConDecTree": {TreantJS JSON config and data} }
	 */
	private String createPayload() {
		String treantAsJson = createTreantJsonString();
		String payload = "{\"issueKey\": \"" + this.elementKey + "\", \"ConDecTree\": " + treantAsJson + "}";
		return payload;
	}

	/**
	 * Creates the Treant JSON String (value transmitted via webhook)
	 * 
	 * @return TreantJS JSON String including config and data
	 */
	private String createTreantJsonString() {
		Treant treant = new Treant(project.getProjectKey(), elementKey, 4);
		ObjectMapper objectMapper = new ObjectMapper();
		String treantAsJson = "";
		try {
			treantAsJson = objectMapper.writeValueAsString(treant);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return treantAsJson;
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
