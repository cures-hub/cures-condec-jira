package de.uhd.ifi.se.decision.management.jira.extraction.view.reports;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.web.action.ProjectActionSupport;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestDecisionKnowledgeReport.AoSentenceTestDatabaseUpdater.class)
public class TestDecisionKnowledgeReport extends TestSetUp {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}


	
	@Test
	@NonTransactional
	public void testCreation() {
		DecisionKnowledgeReport dkr = new DecisionKnowledgeReport(null);
		ProjectActionSupport pas = new MockProjectActionSupport();
		Map<String, String> params = new HashMap<String,String>();
		params.put("selectedProjectId", "1");
		params.put("rootType", "ISSUE");
		dkr.validate(pas, params);
		
		//Need to do further mocking for JQL questioning
		assertNotNull(dkr);
		
		try {
			assertNotNull(dkr.generateReportHtml(pas, params));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
	
	
	private class MockProjectActionSupport extends ProjectActionSupport{

		private static final long serialVersionUID = -4361508663504224792L;

		@Override
		public ApplicationUser getLoggedInUser() {
			return new MockApplicationUser("NoFails");
		}
	}

}
