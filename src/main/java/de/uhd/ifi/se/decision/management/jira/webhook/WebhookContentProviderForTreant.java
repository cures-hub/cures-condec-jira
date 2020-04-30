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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;

/**
 * Creates the content submitted via the webhook. The content consists of a key
 * value pair. The key is an issue id. The value is the Treant JSON String.
 */
public class WebhookContentProviderForTreant {

	private String projectKey;
	private String rootElementKey;
	private String secret;
	private KnowledgeElement knowledgeElement;
	private WebhookType type;

	protected static final Logger LOGGER = LoggerFactory.getLogger(WebhookContentProviderForTreant.class);

	public WebhookContentProviderForTreant(String projectKey, String elementKey, String secret, WebhookType type) {
		this.projectKey = projectKey;
		this.rootElementKey = elementKey;
		this.secret = secret;
		this.type = type;
	}

	public WebhookContentProviderForTreant(String projectKey, KnowledgeElement knowledgeElement, String secret,
			WebhookType type) {
		this.projectKey = projectKey;
		this.rootElementKey = knowledgeElement.getKey();
		this.secret = secret;
		this.knowledgeElement = knowledgeElement;
		this.type = type;
	}

	/**
	 * Creates post method for a single tree of decision knowledge.
	 *
	 * @return post method ready to be posted
	 */
	public PostMethod createPostMethodForTreant() {
		PostMethod postMethod = new PostMethod();
		if (projectKey == null || rootElementKey == null || secret == null || type == null) {
			// System.out.println("createPostMethod null");
			return postMethod;
		}

		String	webhookData = createWebhookDataForTreant();
		if (webhookData == null || webhookData.isBlank()) {
			return postMethod;
		}

		try {
			StringRequestEntity requestEntity = new StringRequestEntity(webhookData, "application/json", "UTF-8");
			postMethod.setRequestEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Creating the post method failed. Message: " + e.getMessage());
		}
		Header header = new Header();
		header.setName("X-Hub-Signature");
		header.setValue("sha256=" + createHashedPayload(webhookData, secret));
		postMethod.setRequestHeader(header);
		return postMethod;
	}

	/**
	 * Creates the key value JSON String transmitted via webhook.
	 *
	 * @return JSON String with the following pattern: { "issueKey": {String},
	 *         "ConDecTree": {TreantJS JSON config and data} }
	 */
	public String createWebhookDataForTreant() {
		String treantAsJson = createTreantJsonString();
		return "{\"issueKey\": \"" + this.rootElementKey + "\", \"ConDecTree\": " + treantAsJson + "}";
	}



	/**
	 * Creates the key value JSON String transmitted via webhook for Slack.
	 * (Differences: "text", mask \ and ")
	 *
	 * @return JSON String with the following pattern: {"text": " \"issueKey\":
	 *         {String}, \"ConDecTree\": {TreantJS JSON config and data} " }
	 */


	/**
	 * Creates the Treant JSON String (value transmitted via webhook).
	 *
	 * @return TreantJS JSON String including config and data
	 */
	private String createTreantJsonString() {
		Treant treant = new Treant(projectKey, rootElementKey, 4, true);
		ObjectMapper objectMapper = new ObjectMapper();
		String treantAsJson = "";
		try {
			treantAsJson = objectMapper.writeValueAsString(treant);
		} catch (IOException e) {
			LOGGER.error("Failed to create a treant json string for the webhook. Message: " + e.getMessage());
		}
		return treantAsJson;
	}

	/**
	 * Converts the webhook data String to a hexadecimal String using the secret
	 * key.
	 *
	 * @param data
	 *            String to be hashed
	 * @param key
	 *            secret key
	 *
	 * @return hexadecimal String
	 */
	public static String createHashedPayload(String data, String key) {
		final String hashingAlgorithm = "HMACSHA256";
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), hashingAlgorithm);
		String hexString = "";
		try {
			Mac mac = Mac.getInstance(hashingAlgorithm);
			mac.init(secretKeySpec);
			hexString = toHexString(mac.doFinal(data.getBytes("UTF-8")));
		} catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException e) {
			LOGGER.error("Creating a hashed payload failed. Message: " + e.getMessage());
		}
		return hexString;
	}

	/**
	 * Converts an array of bytes to a hexadecimal String.
	 *
	 * @param bytes
	 *            array of bytes
	 *
	 * @return hexadecimal String
	 */
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
