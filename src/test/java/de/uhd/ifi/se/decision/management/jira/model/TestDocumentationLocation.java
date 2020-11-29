package de.uhd.ifi.se.decision.management.jira.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestDocumentationLocation {

	@Test
	public void testGetDocumentationLocationFromIdentifier() {
		assertEquals(DocumentationLocation.JIRAISSUETEXT,
				DocumentationLocation.getDocumentationLocationFromIdentifier("s"));
		assertEquals(DocumentationLocation.JIRAISSUETEXT,
				DocumentationLocation.getDocumentationLocationFromIdentifier("s "));
		assertEquals(DocumentationLocation.JIRAISSUE,
				DocumentationLocation.getDocumentationLocationFromIdentifier("i"));
		assertEquals(DocumentationLocation.UNKNOWN, DocumentationLocation.getDocumentationLocationFromIdentifier(""));
		assertEquals(DocumentationLocation.UNKNOWN, DocumentationLocation.getDocumentationLocationFromIdentifier(null));
		assertEquals(DocumentationLocation.UNKNOWN,
				DocumentationLocation.getDocumentationLocationFromIdentifier("XXX"));
	}

	@Test
	public void testGetIdentifier() {
		assertEquals("s", DocumentationLocation.JIRAISSUETEXT.getIdentifier());
		assertEquals("s", DocumentationLocation.getIdentifier(DocumentationLocation.JIRAISSUETEXT));
		assertEquals("", DocumentationLocation.getIdentifier((DocumentationLocation) null));
		assertEquals("i", DocumentationLocation.JIRAISSUE.getIdentifier());
		assertEquals("", DocumentationLocation.UNKNOWN.getIdentifier());
	}

	@Test
	public void testGetIdentifierFromElement() {
		assertEquals("", DocumentationLocation.getIdentifier((KnowledgeElement) null));

		KnowledgeElement element = new KnowledgeElement();
		assertEquals("", DocumentationLocation.getIdentifier(element));

		element.setDocumentationLocation("s");
		assertEquals("s", DocumentationLocation.getIdentifier(element));

		element.setDocumentationLocation("i");
		assertEquals("i", DocumentationLocation.getIdentifier(element));
	}

	@Test
	public void testToString() {
		assertEquals("JiraIssueText", DocumentationLocation.JIRAISSUETEXT.toString());
	}

	@Test
	public void testGetName() {
		assertEquals("Unknown", DocumentationLocation.getName(null));
		assertEquals("JiraIssueText", DocumentationLocation.getName(DocumentationLocation.JIRAISSUETEXT));
	}

	@Test
	public void testGetAllDocumentationLocations() {
		// excludes Unknown documentation location
		assertEquals(3, DocumentationLocation.getAllDocumentationLocations().size());
	}

	@Test
	public void testGetDocumentationLocationFromString() {
		assertEquals(DocumentationLocation.JIRAISSUE,
				DocumentationLocation.getDocumentationLocationFromString("JiraIssue"));
		assertEquals(DocumentationLocation.JIRAISSUE,
				DocumentationLocation.getDocumentationLocationFromString("JiraIssues"));
		assertEquals(DocumentationLocation.JIRAISSUETEXT,
				DocumentationLocation.getDocumentationLocationFromString("JiraIssueText"));

		assertEquals(DocumentationLocation.UNKNOWN, DocumentationLocation.getDocumentationLocationFromString(null));
		assertEquals(DocumentationLocation.UNKNOWN,
				DocumentationLocation.getDocumentationLocationFromString("WikiPage"));
	}
}