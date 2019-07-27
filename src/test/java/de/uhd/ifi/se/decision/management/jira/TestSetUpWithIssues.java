package de.uhd.ifi.se.decision.management.jira;

import static org.mockito.Mockito.mock;

import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;

import de.uhd.ifi.se.decision.management.jira.mocks.MockComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDatabase;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(MockDatabase.class)
public abstract class TestSetUpWithIssues {

	private static EntityManager entityManager;

	public static void initialization() {
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
}