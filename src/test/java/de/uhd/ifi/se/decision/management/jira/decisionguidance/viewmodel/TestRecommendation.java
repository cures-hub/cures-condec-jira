package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
	public void testConstructor() {
		Recommendation recommendation = new Recommendation("TEST", "TEST", "TESTURL");
		assertEquals("TEST", recommendation.getKnowledgeSourceName());
		assertEquals("TEST", recommendation.getRecommendations());
		assertEquals("TESTURL", recommendation.getUrl());
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

		Argument argument2 = new Argument(knowledgeElement1);
		assertTrue(argument.equals(argument));
		assertTrue(argument.equals(argument2));
		Argument nullArgument = null;
		assertFalse(argument.equals(nullArgument));

		assertEquals(Objects.hash("Test Argument", KnowledgeType.ARGUMENT.toString()), argument.hashCode());
	}

}
