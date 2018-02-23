package ut.rest.Decisions;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.DecisionDocumentation.rest.Decisions.DecisionsRest;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.google.common.collect.ImmutableMap;

<<<<<<< Updated upstream:src/test/java/ut/rest/Decisions/TestGetUnlinkedIssues.java
=======
import de.uhd.ifi.se.decision.documentation.jira.rest.DecisionsRest;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
>>>>>>> Stashed changes:src/test/java/ut/de/uhd/ifi/se/decision/documentation/jira/rest/decisions/TestGetUnlinkedIssues.java
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import ut.mocks.MockDefaultUserManager;
import ut.mocks.MockTransactionTemplate;
import ut.testsetup.TestSetUp;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetUnlinkedIssues extends TestSetUp {	
	private EntityManager entityManager;
	private DecisionsRest decRest;
	
	@Before
	public void setUp() {
		decRest= new DecisionsRest();
		initialisation();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
	}
		
	@Test
	public void testIssueIdZeroProjectKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey or issueId = null")).build().getEntity(),decRest.getUnlinkedIssues(0, null).getEntity());
	}
	
	@Test
	public void testIssueIdFilledProjectKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey or issueId = null")).build().getEntity(),decRest.getUnlinkedIssues(7, null).getEntity());
	}
	
	@Test
	public void testIssueIdZeroProjectKeyDontExist() {
		assertEquals(200, decRest.getUnlinkedIssues(0, "NotTEST").getStatus());
	}
	
	@Test
	public void testIssueIdFilledProjectKeyDontExist() {
		assertEquals(200,decRest.getUnlinkedIssues(7, "NotTEST").getStatus());
	}
	
	@Test
	public void testIssueIdZeroProjectKeyExist() {
		assertEquals(200,decRest.getUnlinkedIssues(0, "TEST").getStatus());
	}
	
	@Test
	public void testIssueIdFilledProjectKeyExist() {
		assertEquals(200,decRest.getUnlinkedIssues(7, "TEST").getStatus());
	}

}
