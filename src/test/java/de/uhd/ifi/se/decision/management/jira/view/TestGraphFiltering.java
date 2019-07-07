package de.uhd.ifi.se.decision.management.jira.view;

import static org.junit.Assert.assertNotNull;

import javax.ejb.CreateException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.filtering.GraphFiltering;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.FilterSettingsImpl;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@Data(TestSetUpWithIssues.AoSentenceTestDatabaseUpdater.class)
@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGraphFiltering extends TestSetUpWithIssues {
	private EntityManager entityManager;
	private GraphFiltering graphFiltering;
	private DecisionKnowledgeElement element;
	private ApplicationUser user;

	@Before
	public void setUp() throws CreateException {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
		element = new DecisionKnowledgeElementImpl(ComponentAccessor.getIssueManager().getIssueObject((long) 14));
		element.setProject(new DecisionKnowledgeProjectImpl("Test"));
		user = ComponentAccessor.getUserManager().getUserByName("NoFails");
		FilterSettings filterData = new FilterSettingsImpl(element.getProject().getProjectKey(), "");
		graphFiltering = new GraphFiltering(filterData, user);
	}

	@Test
	public void testConstructor() {
		FilterSettings filterData = new FilterSettingsImpl(element.getProject().getProjectKey(),
				"?jql= Project = " + element.getProject().getProjectKey() + " AND type!=null");
		GraphFiltering filter = new GraphFiltering(filterData, user);
		assertNotNull(filter);
	}
}
