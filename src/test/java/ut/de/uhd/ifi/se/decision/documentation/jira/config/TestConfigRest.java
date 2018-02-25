package ut.de.uhd.ifi.se.decision.documentation.jira.rest.config;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.config.ConfigRest;
import de.uhd.ifi.se.decision.documentation.jira.config.ConfigRestLogic;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import ut.de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import ut.de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;

/**
 * @author Tim Kuchenbuch
 */
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestConfigRest extends TestSetUp {
	private EntityManager entityManager;
	private HttpServletRequest req;
	private ConfigRest confRest;
	
	@Before
	public void setUp() {
		UserManager userManager = new MockDefaultUserManager();
		confRest = new ConfigRest(userManager);
		
		initialisation();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
		
		req = new MockHttpServletRequest();
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", true);
	}
	
	// Testing get function
	@Test
	public void testDoRequestNullKeyNull() {
		assertEquals( Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(), confRest.get(null, null).getEntity());
	}
	
	@Test
	public void testDoRequestNullKeyFilledOk() {
		assertEquals( Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(), confRest.get(null,"TEST").getEntity());
	}
		
	@Test
	public void testDoRequestFilledKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(), confRest.get(req, null).getEntity());
	}
	
	@Test
	public void testDoRequestFilledKeyFilledOk() {
		assertEquals( new ConfigRestLogic().getResponse().getClass(), confRest.get(req,"TEST").getClass());
	}
	
	@Test
	public void testDoUserNoAdmin() {
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals(Response.status(Status.UNAUTHORIZED).build().getEntity(), confRest.get(req,"TEST").getEntity());
	}
	
	@Test
	public void testDoUserNull() {
		HttpServletRequest req2 = new MockHttpServletRequest();
		req2.setAttribute("WithFails", false);
		req2.setAttribute("NoFails", false);
		assertEquals(Response.status(Status.UNAUTHORIZED).build().getEntity(), confRest.get(req2,"TEST").getEntity());
	}
	// Testing doPost
	
	@Test
	public void testdoPostrequestNullKeyNullIsActivatedNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, null, null).getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyNullIsActivatedTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, null, "true").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyNullIsActivatedFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, null, "false").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyExistsIsActivatedNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, "TEST", null).getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyExistsIsActivatedTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, "TEST", "true").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyExistsIsActivatedFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, "TEST", "false").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyDontExistIsActivatedNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, "NotTEST", null).getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyDontExistIsActivatedTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, "NotTEST", "true").getEntity());
	}
	
	@Test
	public void testdoPostrequestNullKeyDontExistIsActivatedFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPost(null, "NotTEST", "false").getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyNullIsActivatedNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.doPost(req, null, null).getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyNullIsActivatedTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.doPost(req, null, "true").getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyNullIsActivatedFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.doPost(req, null, "false").getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyExistsIsActivatedNull() {
		assertEquals( Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build().getEntity(),confRest.doPost(req, "TEST", null).getEntity());
	}
	
	@Test
	public void testdoPostrequestExKeyExistsIsActivatedTrue() {
		assertEquals(new ConfigRestLogic().getResponse().getClass(),confRest.doPost(req, "TEST", "true").getClass());
	}
	
	@Test
	public void testdoPostrequestExKeyExistsIsActivatedFalse() {
		assertEquals(new ConfigRestLogic().getResponse().getClass(),confRest.doPost(req, "TEST", "false").getClass());
	}
	
	@Test
	public void testdoPostUserUnauthorized() {
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),confRest.doPost(req, "NotTEST", "false").getStatus());
	}
	
	@Test
	public void testdoPostUserNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),confRest.doPost(req, "NotTEST", "false").getStatus());
	}
	
	//Testing doPut
	
	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategydNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, null, null).getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategyTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, null, "true").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyNullIsIssueStategyFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, null, "false").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, "TEST", null).getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, "TEST", "true").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyExistsIsIssueStategyFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, "TEST", "false").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, "NotTEST", null).getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, "NotTEST", "true").getEntity());
	}
	
	@Test
	public void testdoPutrequestNullKeyDontExistIsIssueStategyFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(),confRest.doPut(null, "NotTEST", "false").getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.doPut(req, null, null).getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyTrue() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.doPut(req, null, "true").getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyNullIsIssueStategyFalse() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(),confRest.doPut(req, null, "false").getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isIssueStrategy = null")).build().getEntity(),confRest.doPut(req, "TEST", null).getEntity());
	}
	
	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyTrue() {
		assertEquals(new ConfigRestLogic().getResponse().getClass(),confRest.doPut(req, "TEST", "true").getClass());
	}
	
	@Test
	public void testdoPutrequestExKeyExistsIsIssueStategyFalse() {
		assertEquals(new ConfigRestLogic().getResponse().getClass(),confRest.doPut(req, "TEST", "false").getClass());
	}
	
	@Test
	public void testdoPutUserUnauthorized() {
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),confRest.doPut(req, "NotTEST", "false").getStatus());
	}
	
	@Test
	public void testdoPutUserNull() {
		req.setAttribute("WithFails", false);
		req.setAttribute("NoFails", false);
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),confRest.doPut(req, "NotTEST", "false").getStatus());
	}
}
