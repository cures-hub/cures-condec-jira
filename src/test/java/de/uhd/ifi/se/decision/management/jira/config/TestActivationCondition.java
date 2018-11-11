package de.uhd.ifi.se.decision.management.jira.config;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockJiraHelper;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestActivationCondition extends TestSetUpWithIssues {
    private EntityManager entityManager;
    private ActivationCondition condition;

    @Before
    public void setUp(){
        initialization();
        TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
                new MockUserManager());
        condition = new ActivationCondition();
    }

    @Test
    public void testUserFilledJiraHelperFilled(){
        ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("SysAdmin");
        MockJiraHelper helper = new MockJiraHelper();
        assertTrue(condition.shouldDisplay(user, helper));
    }
}
