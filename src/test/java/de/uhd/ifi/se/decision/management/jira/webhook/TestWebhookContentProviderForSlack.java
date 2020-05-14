package de.uhd.ifi.se.decision.management.jira.webhook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.test.jdbc.NonTransactional;

public class TestWebhookContentProviderForSlack extends TestSetUp {

	private KnowledgeElement knowledgeElement;

	@Before
	public void setUp() {
		init();
		knowledgeElement = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement.setSummary("Summary");
		knowledgeElement.setDescription("Description");
		knowledgeElement.setType(KnowledgeType.ISSUE);
	}

	@Test
	public void testcreatePostMethodForSlackForMissingProjectKeyAndMissingElementKeyAndMissingReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack(null, null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testcreatePostMethodForSlackForMissingProjectKeyAndProvidedElementKeyAndAndMissingReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack(null, knowledgeElement, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testcreatePostMethodForProvidedProjectKeyAndMissingElementKeyAndMissingReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForMissingProjectKeyAndMissingElementKeyAndSlackReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack(null, null, WebhookType.SLACK);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndMissingElementKeyAndSlackReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", null, WebhookType.SLACK);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndProvidedElementKeyAndSlackReceiver() {
		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement,
				WebhookType.SLACK);
		assertNotNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndProvidedElementAndSlackReceiver() {

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement,
				WebhookType.SLACK);
		assertNotNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndProvidedKnowledgeElementAndSlackReceiverCutSummary() {
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement1.setSummary("{issue}Summary");
		knowledgeElement1.setDescription("Description");
		knowledgeElement1.setType(KnowledgeType.ISSUE);

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement1,
				WebhookType.SLACK);
		assertNotNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testcreatePostMethodForSlackForProvidedProjectKeyAndProvidedKnowledgeElementAndSlackReceiver() {
		KnowledgeElement knowledgeElement1 = new KnowledgeElement(1, "TEST", "i");
		knowledgeElement1.setSummary("Summary");
		knowledgeElement1.setDescription("Description");
		knowledgeElement1.setType(KnowledgeType.ISSUE);

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement1,
				WebhookType.SLACK);
		assertNotNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testCreateWebhookDataForSlackNewElement() {

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement,
				WebhookType.SLACK);

		String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'"
				+ "TEST : The following decision knowledge element was documented:" + "'}},"
				+ "{'type':'section','text':{'type':'mrkdwn','text':'*Type:* :Issue:  Issue"
				+ " \\n *Title*: Summary\\n'},"
				+ "'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '"
				+ knowledgeElement.getUrl() + "'}}]}";

		assertEquals(data, provider.createWebhookDataForSlack("new"));
	}

	@Test
	@NonTransactional
	public void testCreateWebhookDataForSlackChangedElement() {

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", knowledgeElement,
				WebhookType.SLACK);

		String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'"
				+ "TEST : The following decision knowledge element was changed:" + "'}},"
				+ "{'type':'section','text':{'type':'mrkdwn','text':'*Type:* :Issue:  Issue"
				+ " \\n *Title*: Summary\\n'},"
				+ "'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '"
				+ knowledgeElement.getUrl() + "'}}]}";

		assertEquals(data, provider.createWebhookDataForSlack("changed"));
	}

	@Test
	@NonTransactional
	public void testCreateWebhookDataForSlackTestElement() {
		KnowledgeElement testElement = new KnowledgeElement(1, "TEST", "i");
		testElement.setType(KnowledgeType.ISSUE);
		testElement.setSummary("Test Summary");

		WebhookContentProviderForSlack provider = new WebhookContentProviderForSlack("TEST", testElement,
				WebhookType.SLACK);

		String data = "{'blocks':[{'type':'section','text':{'type':'mrkdwn','text':'"
				+ "TEST : This is a test post. Changed decision knowledge will be shown like this:" + "'}},"
				+ "{'type':'section','text':{'type':'mrkdwn','text':'*Type:* :" + testElement.getType() + ":  "
				+ testElement.getType() + " \\n *Title*: " + testElement.getSummary() + "\\n'}"
				+ ",'accessory':{'type':'button','text':{'type':'plain_text','text':'Go to Jira'},'url' : '"
				+ "null/browse/TEST" + "'}}]}";

		assertEquals(data, provider.createWebhookDataForSlack("test"));
	}

}
