package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectpersistencemanager;

import static org.mockito.Mockito.mock;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.option.OptionSetManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.velocity.VelocityManager;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.mocks.MockCommentManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueLinkTypeManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockIssueTypeManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockPluginSettingsFactory;
import de.uhd.ifi.se.decision.management.jira.mocks.MockProjectRoleManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockVelocityManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockVelocityParamFactory;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.LinkInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

public class ActiveObjectPersistenceManagerTestSetUp {

	protected EntityManager entityManager;
	protected ApplicationUser user;
	protected ActiveObjectPersistenceManager aoStrategy;
	private IssueManager issueManager;
	private IssueTypeManager issueTypeManager = new MockIssueTypeManager();

	public void initialisation() {

		new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
				.addMock(IssueLinkManager.class, new MockIssueLinkManager())
				.addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager())
				.addMock(ProjectRoleManager.class, new MockProjectRoleManager())
				.addMock(VelocityManager.class, new MockVelocityManager())
				.addMock(VelocityParamFactory.class, new MockVelocityParamFactory())
				.addMock(IssueTypeSchemeManager.class, mock(IssueTypeSchemeManager.class))
				.addMock(PluginSettingsFactory.class, new MockPluginSettingsFactory())
				.addMock(IssueTypeManager.class, issueTypeManager)
				.addMock(FieldConfigScheme.class, mock(FieldConfigScheme.class))
				.addMock(OptionSetManager.class, mock(OptionSetManager.class))
				.addMock(CommentManager.class, new MockCommentManager())
				.addMock(SearchService.class, mock(SearchService.class));

		ActiveObjects activeObjects = new TestActiveObjects(entityManager);
		TestComponentGetter.init(activeObjects, new MockTransactionTemplate(), new MockUserManager());
		aoStrategy = new ActiveObjectPersistenceManager("TEST");
		user = new MockApplicationUser("NoFails");
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(DecisionKnowledgeElementInDatabase.class);
			entityManager.migrate(PartOfJiraIssueTextInDatabase.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}
}
