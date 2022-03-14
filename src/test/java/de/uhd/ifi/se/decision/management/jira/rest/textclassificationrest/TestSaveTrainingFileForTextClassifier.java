package de.uhd.ifi.se.decision.management.jira.rest.textclassificationrest;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.jira.mock.servlet.MockHttpServletRequest;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rest.TextClassificationRest;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import net.java.ao.test.jdbc.NonTransactional;

public class TestSaveTrainingFileForTextClassifier extends TestSetUp {

	private HttpServletRequest request;
	private TextClassificationRest classificationRest;

	@Before
	public void setUp() {
		init();
		classificationRest = new TextClassificationRest();
		request = new MockHttpServletRequest();
		request.setAttribute("user", JiraUsers.SYS_ADMIN.getApplicationUser());
	}

	@Test
	public void testRequestNullProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.saveTrainingFileForTextClassifier(null, null).getStatus());
	}

	@Test
	public void testRequestNullProjectKeyExists() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.saveTrainingFileForTextClassifier(null, "TEST").getStatus());
	}

	@Test
	public void testRequestValidProjectKeyNull() {
		assertEquals(Status.BAD_REQUEST.getStatusCode(),
				classificationRest.saveTrainingFileForTextClassifier(request, null).getStatus());
	}

	@Test
	@NonTransactional
	public void testRequestValidProjectKeyExists() {
		KnowledgeElement emptyElement = new KnowledgeElement(42, "TEST", "s");
		emptyElement.setType(KnowledgeType.OTHER);
		emptyElement.setSummary("");
		KnowledgeGraph.getInstance("TEST").addVertex(emptyElement);
		JiraIssues.addElementToDataBase();
		assertEquals(Status.OK.getStatusCode(),
				classificationRest.saveTrainingFileForTextClassifier(request, "TEST").getStatus());
	}
}
