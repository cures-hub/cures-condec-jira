package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

public class TestRecommendation extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testSetAndGetScore() {
		Recommendation recommendation = new Recommendation();
		assertEquals(0, recommendation.getScore());
	}

	@Test
	public void testRecommendation() {
		Recommendation recommendation = new Recommendation("TEST", "TEST", "TESTURL");
		recommendation.setUrl("TEST URL");
		recommendation.setScore(123);
		assertEquals("TEST", recommendation.getKnowledgeSourceName());
		assertEquals("TEST", recommendation.getRecommendations());
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
		Recommendation recommendation = new Recommendation("TEST", "TEST", "TESTURL");
		assertEquals(Objects.hash("TEST", "TEST"), recommendation.hashCode());

	}

	@Test
	public void testEquals() {
		Recommendation recommendationA = new Recommendation();
		recommendationA.setKnowledgeSourceName("SourceA");
		recommendationA.setRecommendations("Recommendation");

		Recommendation recommendationB = new Recommendation();
		recommendationB.setKnowledgeSourceName("SourceA");
		recommendationB.setRecommendations("Recommendation");

		assertEquals(recommendationA, recommendationB);

		recommendationB.setKnowledgeSourceName("SourceB");

		assertNotEquals(recommendationA, recommendationB);
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
