package de.uhd.ifi.se.decision.management.jira;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.user.UserManager;

import de.uhd.ifi.se.decision.management.jira.mocks.MockComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;

/**
 * Test class for the {@link ComponentGetter}. The ComponentGetter is a class
 * provided by the ConDec plugin. It enables to access the active objects
 * databases for object relational mapping. Further, it contains a different
 * user manager than that provided by JIRA's {@link ComponentAccessor} to handle
 * users in HTTP requests.
 */
public class TestComponentGetter {

	private UserManager userManager;
	private ActiveObjects activeObjects;

	@Before
	public void setUp() {
		new MockComponentAccessor();
		userManager = new MockUserManager();
		activeObjects = mock(ActiveObjects.class);
		new ComponentGetter(userManager, activeObjects);
	}

	@Test
	public void testGetUserManager() {
		assertEquals(userManager, ComponentGetter.getUserManager());
	}

	@Test
	public void testGetActiveObjects() {
		assertEquals(activeObjects, ComponentGetter.getActiveObjects());
	}

	@Test
	public void testGetUrlOfImageFolder() {
		assertEquals("null/download/resources/de.uhd.ifi.se.decision.management.jira:stylesheet-and-icon-resources/",
				ComponentGetter.getUrlOfImageFolder());
	}

	@Test
	public void testGetUrlOfClassifierFolder() {
		assertEquals("null/download/resources/de.uhd.ifi.se.decision.management.jira:classifier-resources/",
				ComponentGetter.getUrlOfClassifierFolder());
	}
}
