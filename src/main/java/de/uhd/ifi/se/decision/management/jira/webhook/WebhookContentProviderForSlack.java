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

	/**
	 *
	 */
	public String createWebhookDataForSlack(String event) {
		if (this.knowledgeElement == null || this.knowledgeElement.getSummary() == null
				|| this.knowledgeElement.getType() == null || this.knowledgeElement.getUrl() == null) {
			return "";
		}
		String summary = this.knowledgeElement.getSummary();
		if (summary.contains("{")) {
			summary = this.cutSummary(summary);
		}
		String intro = "";
		intro = getIntro(event);

		String project = this.knowledgeElement.getProject().getProjectKey();

		String url = "";

		if ("test".equals(event)) {
			ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
			url = applicationProperties.getString(APKeys.JIRA_BASEURL) + "/browse/" + this.projectKey;
		} else {
			url = this.knowledgeElement.getUrl();
		}

		String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'" + project + " : " + intro + "'}},"
				+ "{'type':'section','text':{'type':'mrkdwn','text':'*Typ:* :" + this.knowledgeElement.getType() + ":  "
				+ this.knowledgeElement.getType() + " \\n *Titel*: " + summary + "\\n'}";
		// if(!"test".equals(event)){}
		data += ",'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '" + url + "'}";

		data += "}]}";

		return data;
	}

	private String getIntro(String event) {
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
		return intro;
	}

	/**
	 *
	 */
	@Override
	public PostMethod createPostMethod() {
		if (knowledgeElement == null) {
			return new PostMethod();
		}
		return createPostMethodForSlack("new");
	}

	public PostMethod createPostMethodForSlack(String event) {
		PostMethod postMethod = new PostMethod();
		if (projectKey == null || this.knowledgeElement == null || type == null || event == null) {
			return postMethod;
		}

		String webhookData = createWebhookDataForSlack(event);

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
		PostMethod postMethod = createPostMethodForSlack("test");

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
