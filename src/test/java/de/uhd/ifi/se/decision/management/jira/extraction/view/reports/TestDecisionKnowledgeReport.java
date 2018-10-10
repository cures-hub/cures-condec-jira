package de.uhd.ifi.se.decision.management.jira.extraction.view.reports;


import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.web.action.ProjectActionSupport;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
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
	public void TestCreation() {
		DecisionKnowledgeReport dkr = new DecisionKnowledgeReport(null, projectManager, null);
		ProjectActionSupport pas = new MockProjectActionSupport();
		Map<String, String> params = new HashMap<String,String>();
		params.put("selectedProjectId", "1");
		params.put("rootType", "ISSUE");
		dkr.validate(pas, params);
		
		//Need to do further mocking for JQL questioning
		assertNotNull(dkr);
		
	}
	
	
	private class MockProjectActionSupport extends ProjectActionSupport{
		
		@Override
		public ApplicationUser getLoggedInUser() {
			return new MockApplicationUser("NoFails");
		}
	}

}
