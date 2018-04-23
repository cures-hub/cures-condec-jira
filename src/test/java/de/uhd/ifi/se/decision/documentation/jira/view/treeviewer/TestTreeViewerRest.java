package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

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

import de.uhd.ifi.se.decision.documentation.jira.TestSetUp;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.documentation.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.documentation.jira.util.ComponentGetter;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestTreeViewerRest extends TestSetUp {
	private EntityManager entityManager;
	private TreeViewerRest treeview;

	@Before
	public void setUp() {
		treeview= new TreeViewerRest();
		initialization();
		new ComponentGetter().init(new TestActiveObjects(entityManager), new MockTransactionTemplate(), new MockDefaultUserManager());
	}

	@Test
	public void testProjectKeyNull() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Query parameter 'projectKey' is not provided, please add a valid projectKey")).build().getEntity(), treeview.getMessage(null).getEntity());
	}

	@Test
	public void testProjectKeyDontExist() throws GenericEntityException {
		assertEquals(Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'")).build().getEntity(), treeview.getMessage("NotTEST").getEntity());
	}


	@Test
	public void testProjectKeyExists() throws GenericEntityException {
		assertEquals(200, treeview.getMessage("TEST").getStatus());
	}

	@Test
	public void testProjectKeyExistsNoObjects() throws GenericEntityException {
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = new MockProject(2,"TESTNO");
		((MockProject)project).setKey("TESTNO");
		((MockProjectManager) projectManager).addProject(project);
		assertEquals(200, treeview.getMessage("TESTNO").getStatus());
	}
}
