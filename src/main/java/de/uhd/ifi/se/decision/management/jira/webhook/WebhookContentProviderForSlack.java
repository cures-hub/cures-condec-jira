package de.uhd.ifi.se.decision.management.jira.webhook;

import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Creates the content submitted via the webhook to a Slack channel. The content
 * consists of a key value pair. The key is an issue id. The value is the Treant
 * JSON String.
 * 
 * @see WebhookType
 */
public class WebhookContentProviderForSlack extends AbstractWebookContentProvider {

	private KnowledgeElement knowledgeElement;

	protected static final Logger LOGGER = LoggerFactory.getLogger(WebhookContentProviderForSlack.class);

	public WebhookContentProviderForSlack(String projectKey, KnowledgeElement knowledgeElement, WebhookType type) {
		this.projectKey = projectKey;
		this.knowledgeElement = knowledgeElement;
		this.type = type;
	}

	@Override
	public PostMethod createPostMethod() {
		return createPostMethod("new");
	}

	public PostMethod createPostMethod(String eventType) {
		PostMethod postMethod = new PostMethod();
		if (knowledgeElement == null || projectKey == null || type == null) {
			return new PostMethod();
		}

		String webhookData = createWebhookDataForSlack(eventType);

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

	@Override
	public PostMethod createTestPostMethod() {
		PostMethod postMethod = createPostMethod("test");
		return postMethod;
	}

	public String createWebhookDataForSlack(String event) {
		if (this.knowledgeElement == null || this.knowledgeElement.getSummary() == null
				|| this.knowledgeElement.getType() == null || this.knowledgeElement.getUrl() == null) {
			return "";
		}

		String intro = getIntro(event);

		String url = "";
		if ("test".equals(event)) {
			url = getTestUrl();
		} else {
			url = this.knowledgeElement.getUrl();
		}

		return getData(knowledgeElement, intro, url);
	}

	private String getTestUrl() {
		ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
		return applicationProperties.getString(APKeys.JIRA_BASEURL) + "/browse/" + this.projectKey;
	}

	private String getData(KnowledgeElement element, String intro, String url) {
		String summary = cleanSummary(element.getSummary());

		String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'"
				+ element.getProject().getProjectKey() + " : " + intro + "'}},"
				+ "{'type':'section','text':{'type':'mrkdwn','text':'*Type:* :" + element.getType() + ":  "
				+ element.getType() + " \\n *Title*: " + summary + "\\n'}";
		data += ",'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '" + url + "'}";
		data += "}]}";

		return data;
	}

	// TODO Add enum for EventType
	private String getIntro(String event) {
		String intro = "";

		if ("new".equals(event)) {
			intro = "The following decision knowledge element was documented:";
		}
		if ("changed".equals(event)) {
			intro = "The following decision knowledge element was changed:";
		}
		if ("test".equals(event)) {
			intro = "This is a test post. Changed decision knowledge will be shown like this:";
		}
		return intro;
	}

	/**
	 * @param toCut
	 *            String with "{anything}"-parts.
	 *
	 * @return String without "{anything}"-parts
	 */
	public String cleanSummary(String toCut) {
		if (!toCut.contains("{")) {
			return toCut;
		}
		String cut = toCut.replaceAll("\\x7B(\\S*)\\x7D", "");
		return cut;
	}

}
