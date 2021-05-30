package de.uhd.ifi.se.decision.management.jira.extraction.parser;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestRationaleFromCommitMessageParser {
	private List<String> tags = KnowledgeType.toStringList();
	private RationaleFromCommitMessageParser gitCommitMessageX;

	@Test
	public void emptyMessage() {
		String msg = "";
		gitCommitMessageX = new RationaleFromCommitMessageParser(msg);

		Assert.assertEquals(0, gitCommitMessageX.getElements().size());
		Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
		Assert.assertNull(gitCommitMessageX.getParseError());

		gitCommitMessageX = new RationaleFromCommitMessageParser(null);

		Assert.assertEquals(0, gitCommitMessageX.getElements().size());
		Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
		Assert.assertNull(gitCommitMessageX.getParseError());
	}

	@Test
	public void withoutAnyRationaleTags() {
		String msg = "I am just a simple message without any rationale tags";
		gitCommitMessageX = new RationaleFromCommitMessageParser(msg);

		Assert.assertEquals(0, gitCommitMessageX.getElements().size());
		Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
		Assert.assertNull(gitCommitMessageX.getParseError());
	}

	@Test
	public void simpleRationaleTagTest() {
		String msg = "[Issue]I am just a simple message without any rationale tags[/Issue]";
		gitCommitMessageX = new RationaleFromCommitMessageParser(msg);

		Assert.assertEquals(1, gitCommitMessageX.getElements().size());
		Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
		Assert.assertNull(gitCommitMessageX.getParseError());
	}

	@Test
	public void noEndTags() {
		for (String tag1 : tags) {
			String msg = "[" + tag1 + "]Missing ending tag";
			gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
			Assert.assertEquals(0, gitCommitMessageX.getElements().size());
			Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
			Assert.assertNotNull(gitCommitMessageX.getParseError());

			for (String tag2 : tags) {
				msg = "[" + tag1 + "]Correct[/" + tag1 + "][" + tag2 + "]No end.";
				gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
				Assert.assertEquals(1, gitCommitMessageX.getElements().size());
				Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
				Assert.assertNotNull(gitCommitMessageX.getParseError());
			}

			for (String tag2 : tags) {
				if (tag1.equals(tag2)) {
					continue;
				}
				msg = "[" + tag1 + "]Incorrect. [" + tag2 + "]Previous tag did not end,"
						+ " will be ignored as rationale element[/" + tag2 + "]No end.";
				gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
				Assert.assertEquals(0, gitCommitMessageX.getElements().size());
				Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
				Assert.assertNotNull(gitCommitMessageX.getParseError());
			}
		}
	}

	@Test
	public void noStartTags() {
		for (String tag1 : tags) {
			String msg = "Missing start tag[/" + tag1 + "]";
			gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
			Assert.assertEquals(0, gitCommitMessageX.getElements().size());
			Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
			Assert.assertNull(gitCommitMessageX.getParseError());

			for (String tag2 : tags) {
				msg = "some text without start tag[/" + tag1 + "]" + "[" + tag2 + "]rationale element[/" + tag2 + "]";
				gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
				Assert.assertEquals(1, gitCommitMessageX.getElements().size());
				Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
				Assert.assertNull(gitCommitMessageX.getParseError());
			}
		}
	}

	@Test
	public void nestedTags() {
		for (String tag1 : tags) {
			for (String tag2 : tags) {
				if (tag1.equals(tag2)) {
					continue;
				}

				String msg = "[" + tag1 + "]DecKnowElement[" + tag2 + "]" + "[/" + tag1 + "]Not a DecKnowElement[/"
						+ tag2 + "]";
				gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
				Assert.assertEquals(1, gitCommitMessageX.getElements().size());
				Assert.assertEquals(1, gitCommitMessageX.getParseWarnings().size());
				Assert.assertNull(gitCommitMessageX.getParseError());

				msg = "[" + tag1 + "]DecKnowElement[" + tag2 + "]still same element" + "[/" + tag2
						+ "]still same element[/" + tag1 + "]";
				gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
				Assert.assertEquals(1, gitCommitMessageX.getElements().size());
				Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
				Assert.assertNull(gitCommitMessageX.getParseError());
			}
		}
	}

	@Test
	public void misspelledTags() {
		for (String tag : tags) {
			String msg = "[" + tag + "Z]DecKnowElement[/" + tag + "]";

			gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
			Assert.assertEquals(0, gitCommitMessageX.getElements().size());
			Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
			Assert.assertNull(gitCommitMessageX.getParseError());

			msg = "[" + tag + "]DecKnowElement[/" + tag + "Z]";

			gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
			Assert.assertEquals(0, gitCommitMessageX.getElements().size());
			Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
			Assert.assertNotNull(gitCommitMessageX.getParseError());

			msg = "[A" + tag + "]DecKnowElement[/" + tag + "]";

			gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
			Assert.assertEquals(0, gitCommitMessageX.getElements().size());
			Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
			Assert.assertNull(gitCommitMessageX.getParseError());

			msg = "[" + tag + "]DecKnowElement[/A" + tag + "]";

			gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
			Assert.assertEquals(0, gitCommitMessageX.getElements().size());
			Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
			Assert.assertNotNull(gitCommitMessageX.getParseError());
		}
	}

	@Test
	public void tagCharCases() {
		for (String tag : tags) {
			// change case of each letter in the tag
			for (int pos = 0; pos < tag.length(); pos++) {
				String tagModified = flipLetter(tag, pos);
				String msg = "[" + tagModified + "]DecKnowElement[/" + tagModified + "]";

				gitCommitMessageX = new RationaleFromCommitMessageParser(msg);
				Assert.assertEquals(1, gitCommitMessageX.getElements().size());
				Assert.assertEquals(0, gitCommitMessageX.getParseWarnings().size());
				Assert.assertNull(gitCommitMessageX.getParseError());
			}
		}
	}

	// helpers
	private String flipLetter(String tag, int pos) {
		String letter = tag.substring(pos, pos + 1);

		if (letter == letter.toLowerCase()) {
			letter = letter.toUpperCase();
		} else {
			letter = letter.toUpperCase();
		}

		String tagModified = tag.substring(0, pos) + letter + tag.substring(pos + 1);
		return tagModified;
	}
}