package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ofbiz.core.entity.GenericEntityException;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGetTreeViewer extends TestSetUp {
	private EntityManager entityManager;
	private ViewRest viewRest;

	private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	public void testProjectKeyNullKnowledgeTypeNull() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getTreeViewer(null, null).getEntity());
	}

	@Test
	public void testProjectKeyNonExistentKnowledgeTypeNull() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", INVALID_PROJECTKEY))
				.build().getEntity(), viewRest.getTreeViewer("NotTEST", null).getEntity());
	}

	@Test
	public void testProjectKeyExistentKnowledgeTypeNull() throws GenericEntityException {
		assertEquals(200, viewRest.getTreeViewer("TEST", null).getStatus());
	}

	@Test
	public void testProjectKeyExistentKnowledgeTypeEmpty() throws GenericEntityException {
		assertEquals(200, viewRest.getTreeViewer("TEST", "").getStatus());
	}

	@Test
	public void testProjectKeyExistentKnowledgeTypeFilled() throws GenericEntityException {
		assertEquals(200, viewRest.getTreeViewer("TEST", "Issue").getStatus());
	}

	@Test
	public void testProjectKeyExistentWithNoElements() throws GenericEntityException {
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = new MockProject(2, "TESTNO");
		((MockProject) project).setKey("TESTNO");
		((MockProjectManager) projectManager).addProject(project);
		assertEquals(200, viewRest.getTreeViewer("TESTNO", null).getStatus());
	}
}
