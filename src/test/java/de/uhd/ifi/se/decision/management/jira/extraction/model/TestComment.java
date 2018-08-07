package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
public class TestComment extends TestSetUp{



	@Test
	public void testConstructor() {
		assertNotNull(new Comment());
	}

	@Test
	public void testSentencesAreNotNull() {
		assertNotNull(new Comment().getSentences());
	}

	@Ignore
	@Test
	public void testCommentIsCreated() {
		assertNotNull(new Comment("this is a test Sentence. With two sentences"));
	}
}
