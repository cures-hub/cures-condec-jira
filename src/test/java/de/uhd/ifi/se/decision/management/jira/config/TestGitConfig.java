package de.uhd.ifi.se.decision.management.jira.config;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestGitConfig extends TestSetUp {
    private EntityManager entityManager;
    private GitConfig gitConfig;

    @Before
    public void setUp(){
        TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
                new MockDefaultUserManager());
        initialization();
        gitConfig = new GitConfig("TEST");
    }

    @Test
    public void testConstructor(){
        assertEquals("true", gitConfig.getPath());
    }

    @Test
    public void testPathConstructor(){
        GitConfig gitConfigNew = new GitConfig("TEST", "Test-Path");
        assertEquals("Test-Path",gitConfigNew.getPath());
    }

    @Test
    public void testSetGetPath(){
        gitConfig.setPath("test-Path");
        assertEquals("test-Path", gitConfig.getPath());
    }

}
