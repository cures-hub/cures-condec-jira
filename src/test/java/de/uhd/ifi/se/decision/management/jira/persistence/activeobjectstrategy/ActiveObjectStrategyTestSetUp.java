package de.uhd.ifi.se.decision.management.jira.persistence.activeobjectstrategy;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.avatar.AvatarManager;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.config.manager.IssueTypeSchemeManager;
import com.atlassian.jira.issue.fields.option.OptionSetManager;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;

import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.velocity.VelocityManager;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.mocks.*;
import de.uhd.ifi.se.decision.management.jira.persistence.ActiveObjectStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionKnowledgeElementInDatabase;
import de.uhd.ifi.se.decision.management.jira.persistence.LinkInDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.DatabaseUpdater;

public class ActiveObjectStrategyTestSetUp {

	protected EntityManager entityManager;
	protected ApplicationUser user;
	protected ActiveObjectStrategy aoStrategy;
    private ProjectManager projectManager;
    private IssueManager issueManager;
    private ConstantsManager constantsManager;
    private IssueTypeManager issueTypeManager = new MockIssueTypeManager();

    public void initialisation() {

        new MockComponentWorker().init().addMock(IssueManager.class, issueManager)
                .addMock(IssueLinkManager.class, new MockIssueLinkManager())
                .addMock(IssueLinkTypeManager.class, new MockIssueLinkTypeManager())
                .addMock(ProjectRoleManager.class, new MockProjectRoleManager())
                .addMock(VelocityManager.class, new MockVelocityManager())
                .addMock(VelocityParamFactory.class, new MockVelocityParamFactory())
                .addMock(IssueTypeSchemeManager.class, new MockIssueTypeSchemeManager())
                .addMock(PluginSettingsFactory.class, new MockPluginSettingsFactory())
				.addMock(IssueTypeManager.class, issueTypeManager)
                .addMock(OptionSetManager.class, new MockOptionSetManager()).addMock(CommentManager.class, new MockCommentManager());

		ActiveObjects activeObjects = new TestActiveObjects(entityManager);
		TestComponentGetter.init(activeObjects, new MockTransactionTemplate(), new MockUserManager());
		aoStrategy = new ActiveObjectStrategy("TEST");
		user = new MockApplicationUser("NoFails");
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(DecisionKnowledgeElementInDatabase.class);
			entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
			entityManager.migrate(LinkInDatabase.class);
		}
	}
}
