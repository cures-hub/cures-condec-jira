package de.uhd.ifi.se.decision.management.jira;

import static org.mockito.Mockito.mock;

import de.uhd.ifi.se.decision.management.jira.classification.ClassificationTrainerARFF;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.PreprocessorImpl;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.mocks.MockComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDatabase;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssueTypes;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import java.io.File;

/**
 * Mocks a JIRA server with JIRA's {@link ComponentAccessor}, the custom
 * {@link ComponentGetter} of the ConDec plug-in, and test data (e.g.,
 * {@link JiraUsers}, {@link JiraProjects}, {@link JiraIssueTypes}, and
 * {@link JiraIssues}).
 * <p>
 * The following annotations are used to mock the active objects databases
 * before every test execution. Test classes need to extend this TestSetUp class
 * if they use the mocked active objects databases.
 */
@RunWith(ActiveObjectsJUnitRunner.class)
@Data(MockDatabase.class)
public abstract class TestSetUp {

    private static EntityManager entityManager;

    /**
     * Inits JIRA's {@link ComponentAccessor} including test data (e.g.,
     * {@link JiraUsers}, {@link JiraProjects}, {@link JiraIssueTypes}, and
     * {@link JiraIssues}) and the custom {@link ComponentGetter}.
     */
    public static void init() {
        initComponentAccessor();
        initComponentGetter();
    }

    /**
     * The ComponentAccessor is a class provided by JIRA. It provides methods to
     * access JIRA's internal classes such as the ProjectManager or a UserManager.
     *
     * @see ComponentAccessor
     * @see MockComponentAccessor
     */
    public static void initComponentAccessor() {
        new MockComponentAccessor();
    }

    /**
     * The ComponentGetter is a class provided by the ConDec plugin. It enables to
     * access the active objects databases for object relational mapping. Further,
     * it contains a different user manager than that provided by the
     * ComponentAccessor to handle users in HTTP requests.
     *
     * @see ComponentGetter
     */
    public static void initComponentGetter() {
        ActiveObjects activeObjects = mock(ActiveObjects.class);
        if (entityManager != null) {
            activeObjects = new TestActiveObjects(entityManager);
        }
        new ComponentGetter(new de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager(), activeObjects);
    }


    public static void initClassifierPaths() {
        PreprocessorImpl.DEFAULT_DIR = "src/main/resources/classifier/";
        ClassificationTrainerARFF.DEFAULT_TRAINING_DATA = new File("src/main/resources/classifier/defaultTrainingData.arff");
    }
}