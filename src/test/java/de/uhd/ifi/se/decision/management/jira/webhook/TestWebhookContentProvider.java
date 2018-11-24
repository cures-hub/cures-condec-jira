package de.uhd.ifi.se.decision.management.jira.webhook;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestWebhookContentProvider extends TestSetUpWithIssues {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		initialization();
	}

	@Test
	public void testCreatePostMethodForMissingProjectKeyAndMissingElementKeyAndMissingSecret() {
		WebhookContentProvider provider = new WebhookContentProvider(null, null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testCreatePostMethodForMissingProjectKeyAndMissingElementKeyAndProvidedSecret() {
		WebhookContentProvider provider = new WebhookContentProvider(null, null, "1234IamASecretKey");
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testCreatePostMethodForMissingProjectKeyAndProvidedElementKeyAndMissingSecret() {
		WebhookContentProvider provider = new WebhookContentProvider(null, "TEST-14", null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	public void testCreatePostMethodForProvidedProjectKeyAndMissingElementKeyAndMissingSecret() {
		WebhookContentProvider provider = new WebhookContentProvider("TEST", null, null);
		assertNull(provider.createPostMethod().getRequestEntity());
	}

	@Test
	@NonTransactional
	public void testCreatePostMethodForProvidedProjectKeyAndProvidedElementKeyAndProvidedSecret() {
		WebhookContentProvider provider = new WebhookContentProvider("TEST", "TEST-14", "1234IamASecretKey");
		assertNotNull(provider.createPostMethod().getRequestEntity());
	}

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

	@Test
	public void testCreateHashedPayloadWithUmlaut() {
		assertEquals(WebhookContentProvider.createHashedPayload(
				"{\"issueKey\":\"TEST-29\",\"ConDecTree\":{\"nodeStructure\":{\"children\":[{\"connectors\":{\"style\":{\"stroke\":\"#000000\"}},\"children\":[{\"connectors\":{\"style\":{\"stroke\":\"#000000\"}},\"children\":[{\"connectors\":{\"style\":{\"stroke\":\"#000000\"}},\"children\":[],\"htmlClass\":\"solution\",\"link\":{\"href\":\"http://localhost:2990/jira/browse/TEST-31\",\"target\":\"_blank\"},\"nodeContent\":{\"name\":\"Alternative\",\"title\":\"gfx\",\"desc\":\"TEST-31\"},\"htmlId\":10700}],\"htmlClass\":\"solution\",\"link\":{\"href\":\"http://localhost:2990/jira/browse/TEST-18\",\"title\":\"äundefinedfgreeygyrsehbnhzrdregsycgfanh\",\"target\":\"_blank\"},\"nodeContent\":{\"name\":\"Alternative\",\"title\":\"zweites22\",\"desc\":\"TEST-18\"},\"htmlId\":10601}],\"htmlClass\":\"decision\",\"link\":{\"href\":\"http://localhost:2990/jira/browse/TEST-17\",\"title\":\"undefined\",\"target\":\"_blank\"},\"nodeContent\":{\"name\":\"Decision\",\"title\":\"new decision12\",\"desc\":\"TEST-17\"},\"htmlId\":10600}],\"text\":{\"name\":\"Task\",\"title\":\"a\",\"desc\":\"TEST-29\"}},\"chart\":{\"container\":\"#treant-container\",\"node\":{\"collapsable\":\"true\"},\"connectors\":{\"type\":\"straight\"},\"rootOrientation\":\"NORTH\",\"siblingSeparation\":30,\"levelSeparation\":30,\"subTreeSeparation\":30}}}",
				"03f90207-73bc-44d9-9848-d3f1f8c8254e"),
				"dfb0bca5dbc85e926dd7bf519a1e4a1636bc534a1d13f623108919585aa26262");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateHashedPayloadEmptyKey() {
		WebhookContentProvider.createHashedPayload("{\"issueKey\": \"CONDEC-1234\", \"ConDecTree\": "
				+ "{\"nodeStructure\":{\"children\":[],\"text\":{\"title\":\"Test Send\","
				+ "\"desc\":\"CONDEC-1234\"}},\"chart\":{\"container\":\"#treant-container\","
				+ "\"node\":{\"collapsable\":\"true\"},\"connectors\":{\"type\":\"straight\"},"
				+ "\"rootOrientation\":\"NORTH\",\"siblingSeparation\":30,\"levelSeparation\":30,"
				+ "\"subTreeSeparation\":30}}}", "");
	}
}
