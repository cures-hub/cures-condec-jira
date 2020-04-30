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
public class WebhookContentProviderForSlack {

	private String projectKey;
	private String rootElementKey;
	private String secret;
	private KnowledgeElement knowledgeElement;
	private WebhookType type;

	protected static final Logger LOGGER = LoggerFactory.getLogger(WebhookContentProviderForSlack.class);

	public WebhookContentProviderForSlack(String projectKey, String elementKey, String secret, WebhookType type) {
		this.projectKey = projectKey;
		this.rootElementKey = elementKey;
		this.secret = secret;
		this.type = type;
	}

	public WebhookContentProviderForSlack(String projectKey, KnowledgeElement knowledgeElement, String secret,
			WebhookType type) {
		this.projectKey = projectKey;
		this.rootElementKey = knowledgeElement.getKey();
		this.secret = secret;
		this.knowledgeElement = knowledgeElement;
		this.type = type;
	}


 /**
  *
  */
	public String createWebhookDataForSlack(KnowledgeElement changedElement, String event) {
		if (changedElement == null || changedElement.getSummary() == null || changedElement.getType() == null
				|| changedElement.getUrl() == null) {
			return "";
		}
		String summary = changedElement.getSummary();
		if (summary.contains("{")) {
			summary = this.cutSummary(summary);
		}
		String intro = "";
		if ("new".equals(event)) {
			intro = "Neues Entscheidungswissen wurde in Jira dokumentiert:";
		}
		if ("changed".equals(event)) {
			intro = "Dieses dokumentierte Entscheidungswissen wurde geändert:";
		}
		if ("test".equals(event)) {
			intro = "TESTPOST, changed decision knowledge will be shown like this:";
		}

		String project = changedElement.getProject().getProjectKey();

		String url = changedElement.getUrl();

		String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'" + project + " : " + intro + "'}},"
				+ "{'type':'section','text':{'type':'mrkdwn','text':'*Typ:* :" + changedElement.getType() + ":  "
				+ changedElement.getType() + " \\n *Titel*: " + summary + "\\n'},"
				+ "'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '" + url
				+ "'}}]}";
		return data;
	}

	/**
	 *
	 */
	public PostMethod createPostMethodForSlack() {
		if (knowledgeElement == null) {
			return new PostMethod();
		}
		return createPostMethodForSlack(this.knowledgeElement, "new");
	}

	public PostMethod createPostMethodForSlack(KnowledgeElement changedElement, String event) {
		PostMethod postMethod = new PostMethod();
		if (projectKey == null || changedElement == null || type == null || event == null) {
			return postMethod;
		}

		String	webhookData = createWebhookDataForSlack(this.knowledgeElement, event);

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
		postMethod.setRequestHeader(header);

		return postMethod;
	}

	/**
	 * @param toCut
	 *            String with "{anything}"-parts.
	 *
	 * @return String without "{anything}"-parts
	 */
	public String cutSummary(String toCut) {
		String cut = toCut.replaceAll("\\x7B(\\S*)\\x7D", "");
		return cut;
	}

}
