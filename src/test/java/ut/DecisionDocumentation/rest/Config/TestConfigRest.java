package ut.DecisionDocumentation.rest.Config;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.DecisionDocumentation.rest.Config.ConfigRest;
import com.atlassian.DecisionDocumentation.rest.Config.ConfigRestLogic;
import com.atlassian.DecisionDocumentation.util.ComponentGetter;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import ut.mocks.MockDefaultUserManager;
import ut.mocks.MockTransactionTemplate;
import ut.testsetup.TestSetUp;

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
	public void testRequestNullKeyNull() {
		assertEquals( Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(), confRest.get(null, null).getEntity());
	}
	
	@Test
	public void testRequestNullKeyFilledOk() {
		assertEquals( Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build().getEntity(), confRest.get(null,"TEST").getEntity());
	}
		
	@Test
	public void testRequestFilledKeyNull() {
		assertEquals(Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "projectKey = null")).build().getEntity(), confRest.get(req, null).getEntity());
	}
	
	@Test
	public void testRequestFilledKeyFilledOk() {
		assertEquals( new ConfigRestLogic().getResponse().getClass(), confRest.get(req,"TEST").getClass());
	}
	
	@Test
	public void testUserNoAdmin() {
		req.setAttribute("WithFails", true);
		req.setAttribute("NoFails", false);
		assertEquals(Response.status(Status.UNAUTHORIZED).build().getEntity(), confRest.get(req,"TEST").getEntity());
	}
	
	@Test
	public void testUserNull() {
		HttpServletRequest req2 = new MockHttpServletRequest();
		req2.setAttribute("WithFails", false);
		req2.setAttribute("NoFails", false);
		assertEquals(Response.status(Status.UNAUTHORIZED).build().getEntity(), confRest.get(req2,"TEST").getEntity());
	}
}
