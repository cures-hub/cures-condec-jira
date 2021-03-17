package de.uhd.ifi.se.decision.management.jira.testdata;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;

public class KnowledgeElements {

	public static List<KnowledgeElement> elements = new ArrayList<>();

	public static List<KnowledgeElement> getTestKnowledgeElements() {
		if (elements == null || elements.isEmpty()) {
			elements = createKnowledgeElements();
		}
		return elements;
	}

	public static KnowledgeElement getTestKnowledgeElement() {
		return getTestKnowledgeElements().get(0);
	}

	private static List<KnowledgeElement> createKnowledgeElements() {
		List<KnowledgeElement> elements = new ArrayList<>();
		List<Issue> jiraIssues = JiraIssues.getTestJiraIssues();
		for (Issue jiraIssue : jiraIssues) {
			elements.add(new KnowledgeElement(jiraIssue));
		}

		String stringThatIsNotDone = "public class ClassThatIsNotDone {\n" + 
		"    // This file must be larger than 50 lines,\n" + 
		"    // must not be named \"test\" at the beginning,\n" + 
		"    // and must not be linked to a decision knowledge element.\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"}";
		ChangedFile fileThatIsNotDone = new ChangedFile(stringThatIsNotDone);
		fileThatIsNotDone.setTreeWalkPath("ClassThatIsNotDone.java");
		fileThatIsNotDone.setId(100);
		fileThatIsNotDone.setProject("TEST");
		elements.add(fileThatIsNotDone);

		String smallStringThatIsDone = "public class SmallClassThatIsDone {\n" + 
		"    // This file is smaller than 50 lines,\n" + 
		"    // must not be named \"test\" at the beginning,\n" + 
		"    // and must not be linked to a decision knowledge element.\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"}";
		ChangedFile smallFileThatIsDone = new ChangedFile(smallStringThatIsDone);
		smallFileThatIsDone.setTreeWalkPath("SmallClassThatIsDone.java");
		smallFileThatIsDone.setId(101);
		smallFileThatIsDone.setProject("TEST");
		elements.add(smallFileThatIsDone);

		String testStringThatIsDone = "public class TestClassThatIsDone {\n" + 
		"    // This file must be larger than 50 lines,\n" + 
		"    // is named \"test\" at the beginning,\n" + 
		"    // and must not be linked to a decision knowledge element.\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"}";
		ChangedFile testFileThatIsDone = new ChangedFile(testStringThatIsDone);
		testFileThatIsDone.setTreeWalkPath("TestClassThatIsDone.java");
		testFileThatIsDone.setId(102);
		testFileThatIsDone.setProject("TEST");
		elements.add(testFileThatIsDone);

		String linkedStringThatIsDone = "public class LinkedClassThatIsDone {\n" + 
		"    // This file must be larger than 50 lines,\n" + 
		"    // must not be named \"test\" at the beginning,\n" + 
		"    // and is linked to a decision knowledge element.\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n" + 
		"}";
		ChangedFile linkedFileThatIsDone = new ChangedFile(linkedStringThatIsDone);
		linkedFileThatIsDone.setTreeWalkPath("LinkedClassThatIsDone.java");
		linkedFileThatIsDone.setId(103);
		linkedFileThatIsDone.setProject("TEST");
		linkedFileThatIsDone.setProject("TEST");

		KnowledgeGraph graph = KnowledgeGraph.getInstance("TEST");
		KnowledgeElement issueToBeLinked = new KnowledgeElement(jiraIssues.get(4));
		KnowledgeElement decisionToBeLinked = new KnowledgeElement(jiraIssues.get(10));
		graph.addVertexNotBeingInDatabase(issueToBeLinked);
		graph.addVertexNotBeingInDatabase(decisionToBeLinked);

		Link link = new Link(linkedFileThatIsDone, issueToBeLinked);
		graph.addEdgeNotBeingInDatabase(link);
		link = new Link(issueToBeLinked, decisionToBeLinked);
		graph.addEdgeNotBeingInDatabase(link);
		assertEquals(1, linkedFileThatIsDone.getLinks().size());
		elements.add(linkedFileThatIsDone);

		return elements;
	}
}