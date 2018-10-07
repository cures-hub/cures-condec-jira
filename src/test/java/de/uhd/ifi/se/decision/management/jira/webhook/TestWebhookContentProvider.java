package de.uhd.ifi.se.decision.management.jira.webhook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@Data(TestSetUp.AoSentenceTestDatabaseUpdater.class)
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebhookContentProvider extends TestSetUp {
	private EntityManager entityManager;

	@Before
	public void setUp() {
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		initialization();
	}

	@Test
	public void testGetIssueKeyNullNull() {
		WebhookContentProvider provider = new WebhookContentProvider(null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testGetIssueKeyNullFilled() {
		WebhookContentProvider provider = new WebhookContentProvider(null, "TEST-14");
		assertNull(provider.createPostMethod().getRequestEntity());
	}

//	@Test
//	public void testGetIssueKeyFilledNull() throws IOException {
//		WebhookContentProvider provider = new WebhookContentProvider("TEST", null);
//		assertNull(provider.createPostMethod().getRequestEntity());
//	}

//	@Test
//	@NonTransactional
//	public void testGetIssueKeyFilledFilled() throws IOException {
//		WebhookContentProvider provider = new WebhookContentProvider("TEST", "TEST-14");
//		assertTrue(provider.createPostMethod().getRequestEntity().getContentLength() > 0);
//	}

	@Test
	public void testGetGitHashNullNull() throws IOException {
		WebhookContentProvider provider = new WebhookContentProvider(null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testGetGitHashNullFilled() throws IOException {
		WebhookContentProvider provider = new WebhookContentProvider(null, "TEST-14");
		assertNull(provider.createPostMethod().getRequestEntity());
	}

//	@Test
//	public void testGetGitHashFilledNull() throws IOException {
//		WebhookContentProvider provider = new WebhookContentProvider("TEST", null);
//		assertNull(provider.createPostMethod().getRequestEntity());
//	}

//	@Test
//	@NonTransactional
//	public void testGetGitHashFilledFilled() throws IOException {
//		WebhookContentProvider provider = new WebhookContentProvider("TEST", "TEST-14");
//		assertTrue(provider.createPostMethod().getRequestEntity().getContentLength() > 0);
//	}

	@Test
	public void testCreateHashedPayload() {
		assertEquals(
				WebhookContentProvider.createHashedPayload("{\"issueKey\": \"CONDEC-1234\", \"ConDecTree\": "
						+ "{\"nodeStructure\":{\"children\":[],\"text\":{\"title\":\"Test Send\","
						+ "\"desc\":\"CONDEC-1234\"}},\"chart\":{\"container\":\"#treant-container\","
						+ "\"node\":{\"collapsable\":\"true\"},\"connectors\":{\"type\":\"straight\"},"
						+ "\"rootOrientation\":\"NORTH\",\"siblingSeparation\":30,\"levelSeparation\":30,"
						+ "\"subTreeSeparation\":30}}}", "03f90207-73bc-44d9-9848-d3f1f8c8254e"),
				"e7f0bb82f13286d1afea8cb59f07af829177e2bac8a7af4e883a074851152717");
	}
}
