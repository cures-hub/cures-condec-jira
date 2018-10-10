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

import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;

/**
 * Creates the content submitted via the webhook. The content consists of a key
 * value pair. The key is an issue id. The value is the Treant JSON String.
 */
public class WebhookContentProvider {

	private String projectKey;
	private String rootElementKey;
	private String secret;

	public WebhookContentProvider(String projectKey, String elementKey, String secret) {
		this.projectKey = projectKey;
		this.rootElementKey = elementKey;
		this.secret = secret;
	}

	/**
	 * Creates post method for a single tree of decision knowledge.
	 * 
	 * @return post method ready to be posted
	 */
	public PostMethod createPostMethod() {
		PostMethod postMethod = new PostMethod();
		if (projectKey == null || rootElementKey == null || secret == null) {
			return postMethod;
		}
		String webhookData = createWebhookData();
		try {
			StringRequestEntity requestEntity = new StringRequestEntity(webhookData, "application/json", "UTF-8");
			postMethod.setRequestEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
	private String createWebhookData() {
		String treantAsJson = createTreantJsonString();
		return "{\"issueKey\": \"" + this.rootElementKey + "\", \"ConDecTree\": " + treantAsJson + "}";
	}

	/**
	 * Creates the Treant JSON String (value transmitted via webhook).
	 * 
	 * @return TreantJS JSON String including config and data
	 */
	private String createTreantJsonString() {
		Treant treant = new Treant(projectKey, rootElementKey, 4);
		ObjectMapper objectMapper = new ObjectMapper();
		String treantAsJson = "";
		try {
			treantAsJson = objectMapper.writeValueAsString(treant);
		} catch (IOException e) {
			e.printStackTrace();
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
		String hexString = "";
		try {
			hexString = toHexString(mac.doFinal(data.getBytes("UTF-8")));
		} catch (IllegalStateException | UnsupportedEncodingException e) {
			e.printStackTrace();
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
