package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestRecommendation extends TestSetUp {

	private KnowledgeSource knowledgeSource;

	@Before
	public void setUp() {
		init();
		knowledgeSource = new ProjectSource("TEST", "TEST", true);
	}

	@Test
	public void testSetAndGetScore() {
		Recommendation recommendation = new Recommendation();
		assertEquals(0, recommendation.getScore());
	}

	@Test
	public void testRecommendation() {

		Recommendation recommendation = new Recommendation(knowledgeSource, "TEST", "TESTURL");
		recommendation.setUrl("TEST URL");
		recommendation.setScore(123);
		assertEquals("TEST", recommendation.getKnowledgeSourceName());
		assertEquals("TEST", recommendation.getRecommendation());
		assertEquals("TEST URL", recommendation.getUrl());
		assertEquals(123, recommendation.getScore());
		assertEquals(0, recommendation.getArguments().size());
	}

	@Test
	public void testAddArgument() {
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setDescription("Test");
		knowledgeElement.setSummary("Test");
		knowledgeElement.setDocumentationLocation("i");
		knowledgeElement.setType(KnowledgeType.CON);
		List<Argument> arguments = new ArrayList<>();
		Argument argument = new Argument(knowledgeElement);
		arguments.add(argument);

		Recommendation recommendation = new Recommendation();
		recommendation.addArguments(arguments);
		assertEquals(1, recommendation.getArguments().size());

		recommendation.setArguments(arguments);
		assertEquals(1, recommendation.getArguments().size());

		Recommendation emptyArguments = new Recommendation();
		emptyArguments.addArgument(argument);
		assertEquals(1, emptyArguments.getArguments().size());
	}

	@Test
	public void testHashCode() {
		Recommendation recommendation = new Recommendation(knowledgeSource, "TEST", "TESTURL");
		assertEquals(Objects.hash("TEST", "TEST"), recommendation.hashCode());

	}

	@Test
	public void testEquals() {
		KnowledgeSource sourceA = new ProjectSource("TEST", "SourceA", true);
		Recommendation recommendationA = new Recommendation(sourceA, "Recommendation", "TESTURL");

		KnowledgeSource sourceB = new ProjectSource("TEST", "SourceA", true);
		Recommendation recommendationB = new Recommendation(sourceB, "Recommendation", "TESTURL");

		assertEquals(recommendationA, recommendationB);
	}

	@Test
	public void testArgument() {
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setSummary("Test Argument");
		knowledgeElement.setId(123);
		knowledgeElement.setDocumentationLocation("i");
		knowledgeElement.setType(KnowledgeType.ARGUMENT);

		Argument argument = new Argument(knowledgeElement);
		assertEquals("Test Argument", argument.getSummary());

		KnowledgeElement knowledgeElement1 = new KnowledgeElement();
		knowledgeElement1.setSummary("Test Argument");
		knowledgeElement1.setId(123);
		knowledgeElement1.setDocumentationLocation("i");
		knowledgeElement1.setType(KnowledgeType.ARGUMENT);
	}

}
