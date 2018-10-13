package de.uhd.ifi.se.decision.management.jira.extraction.view.reports;

import static org.junit.Assert.assertNotNull;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.web.action.ProjectActionSupport;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockSearchService;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
public class TestDecisionKnowledgeReport extends TestSetUpWithIssues {

	private EntityManager entityManager;

	private DecisionKnowledgeReport report;
	
	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		ProjectManager a = ComponentAccessor.getProjectManager();
		this.report = new DecisionKnowledgeReport(a);
		this.report.setSearchService(new MockSearchService());

		ProjectActionSupport pas = new MockProjectActionSupport();
		Map<String, String> params = new HashMap<String, String>();
		params.put("selectedProjectId", "1");
		params.put("rootType", "ISSUE");
		this.report.validate(pas, params);
	}

	@Test
	@NonTransactional
	public void testCreation() {
		assertNotNull(this.report);
		assertNotNull(this.report.createValues(new MockProjectActionSupport()));
	}


	private class MockProjectActionSupport extends ProjectActionSupport {

		private static final long serialVersionUID = -4361508663504224792L;

		@Override
		public ApplicationUser getLoggedInUser() {
			return new MockApplicationUser("NoFails");
		}
	}

}
