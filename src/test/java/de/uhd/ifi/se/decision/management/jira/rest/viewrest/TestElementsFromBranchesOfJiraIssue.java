package de.uhd.ifi.se.decision.management.jira.rest.viewrest;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.gitclient.TestSetUpGit;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ofbiz.core.entity.GenericEntityException;

import static org.junit.Assert.assertEquals;

@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestElementsFromBranchesOfJiraIssue extends TestSetUpGit {
	private EntityManager entityManager;
	private ViewRest viewRest;

	private static final String INVALID_PROJECTKEY = "Decision knowledge elements cannot be shown since project key is invalid.";

	@Before
	public void setUp() {
		viewRest = new ViewRest();
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		ConfigPersistenceManager.setGitUri("TEST", getRepoUri());
		/* TODO: uri will not be correctly retrieved
		String uri = ConfigPersistenceManager.getGitUri("TEST");
		*/
	}

	@Test
	public void testEmptyIssueKey() throws GenericEntityException {
		assertEquals(400, viewRest.getFeatureBranchTree("").getStatus());
	}

	@Test
	@Ignore("In order for the test to work, the mock PluginSettings must store and return" +
			"gitUri for the TEST project, which is currently not implemented.")
	public void testUnknownIssueKey() throws GenericEntityException {
		/*
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			TODO: Setup the test, so that the git client can get git uri given the project key only.
		 */
		assertEquals(400, viewRest.getFeatureBranchTree("HOUDINI-1").getStatus());
	}

	@Test
	@Ignore("In order for the test to work, the mock PluginSettings must store and return" +
			"gitUri for the TEST project, which is currently not implemented.")
	public void testExistingIssueKey() throws GenericEntityException {
		assertEquals(200, viewRest.getFeatureBranchTree("TEST-1").getStatus());
		Object receivedEntity = viewRest.getFeatureBranchTree("TEST-1").getEntity();

		Object expectedEntity = new DiffViewer(null);
		assertEquals(expectedEntity.getClass(), receivedEntity.getClass());
	}
}
