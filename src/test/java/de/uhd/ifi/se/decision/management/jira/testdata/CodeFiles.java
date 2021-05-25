package de.uhd.ifi.se.decision.management.jira.testdata;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;

public class CodeFiles {

	public static List<ChangedFile> codeFiles = new ArrayList<>();

	public static List<ChangedFile> getTestCodeFiles() {
		if (codeFiles == null || codeFiles.isEmpty()) {
			codeFiles = createChangedFiles();
		}
		return codeFiles;
	}

	public static ChangedFile getCodeFileNotDone() {
		return getTestCodeFiles().get(0);
	}

	public static ChangedFile getSmallCodeFileDone() {
		return getTestCodeFiles().get(1);
	}

	public static ChangedFile getTestCodeFileDone() {
		return getTestCodeFiles().get(2);
	}

	public static ChangedFile getAnotherTestCodeFileDone() {
		return getTestCodeFiles().get(3);
	}

	private static List<ChangedFile> createChangedFiles() {
		List<ChangedFile> changedFiles = new ArrayList<>();
		String stringThatIsNotDone = "public class ClassThatIsNotDone {\n"
				+ "    // This file must be larger than 50 lines,\n"
				+ "    // must not be named \"test\" at the beginning,\n"
				+ "    // and must not be linked to a decision knowledge element.\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "}";
		ChangedFile fileThatIsNotDone = new ChangedFile(stringThatIsNotDone);
		fileThatIsNotDone.setTreeWalkPath("ClassThatIsNotDone.java");
		fileThatIsNotDone.setProject("TEST");
		changedFiles.add(fileThatIsNotDone);

		String smallStringThatIsDone = "public class SmallClassThatIsDone {\n"
				+ "    // This file is smaller than 50 lines,\n"
				+ "    // must not be named \"test\" at the beginning,\n"
				+ "    // and must not be linked to a decision knowledge element.\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "}";
		ChangedFile smallFileThatIsDone = new ChangedFile(smallStringThatIsDone);
		smallFileThatIsDone.setTreeWalkPath("SmallClassThatIsDone.java");
		smallFileThatIsDone.setProject("TEST");
		changedFiles.add(smallFileThatIsDone);

		String testStringThatIsDone = "public class TestClassThatIsDone {\n"
				+ "    // This file must be larger than 50 lines,\n" + "    // is named \"test\" at the beginning,\n"
				+ "    // and must not be linked to a decision knowledge element.\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "}";
		ChangedFile testFileThatIsDone = new ChangedFile(testStringThatIsDone);
		testFileThatIsDone.setTreeWalkPath("TestClassThatIsDone.java");
		testFileThatIsDone.setProject("TEST");
		changedFiles.add(testFileThatIsDone);

		String linkedStringThatIsDone = "public class LinkedClassThatIsDone {\n"
				+ "    // This file must be larger than 50 lines,\n"
				+ "    // must not be named \"test\" at the beginning,\n"
				+ "    // and is linked to a decision knowledge element.\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n    // …\n"
				+ "}";
		ChangedFile linkedFileThatIsDone = new ChangedFile(linkedStringThatIsDone);
		linkedFileThatIsDone.setTreeWalkPath("LinkedClassThatIsDone.java");
		linkedFileThatIsDone.setProject("TEST");
		changedFiles.add(linkedFileThatIsDone);
		return changedFiles;
	}

	public static void addCodeFilesToKnowledgeGraph() {
		KnowledgeGraph graph = KnowledgeGraph.getInstance("TEST");
		for (KnowledgeElement element : createChangedFiles()) {
			graph.addVertex(element);
		}

		Link link = new Link(getAnotherTestCodeFileDone(), KnowledgeElements.getSolvedDecisionProblem());
		graph.addEdgeNotBeingInDatabase(link);
	}
}