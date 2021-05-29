package de.uhd.ifi.se.decision.management.jira.model.git;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class TestFileType {

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEqualsFalse() {
		assertFalse(FileType.java().equals((Object) null));
		assertFalse(FileType.java().equals(CommentStyleType.JAVA_C));
	}

	@Test
	public void testEqualsTrue() {
		FileType javaType = FileType.java();
		assertTrue(javaType.equals(javaType));
		assertTrue(javaType.equals(new FileType("java", CommentStyleType.JAVA_C)));
	}

}
