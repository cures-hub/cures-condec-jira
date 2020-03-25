package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.impl.ChangedFileImpl;

public class GitCodeClassExtractor {

    private String projectKey;
    private List<File> codeClassListFull;
    private Map<String, String> codeClassOriginMap;
    private Map<File, String> treeWalkPath;
    private GitClient gitClient;

    public GitCodeClassExtractor(String projectKey) {
	this.projectKey = projectKey;
	codeClassOriginMap = new HashMap<String, String>();
	treeWalkPath = new HashMap<File, String>();
	this.codeClassListFull = getCodeClassFiles();
    }

    public List<File> getCodeClassFiles() {
	List<File> codeClassListFull = new ArrayList<File>();

	gitClient = new GitClientImpl(projectKey);
	for (String repoUri : gitClient.getRemoteUris()) {
	    Repository repository = gitClient.getRepository(repoUri);
	    if (repository != null) {
		TreeWalk treeWalk = new TreeWalk(repository);
		try {
		    treeWalk.addTree(gitClient.getDefaultBranchCommits(repoUri).get(0).getTree());
		    treeWalk.setRecursive(false);
		    while (treeWalk.next()) {
			if (treeWalk.isSubtree()) {
			    treeWalk.enterSubtree();
			} else {
			    File file = new File(repository.getWorkTree(), treeWalk.getPathString());
			    ChangedFile chfile = new ChangedFileImpl(file);
			    if (chfile.isExistingJavaClass()) {
				codeClassListFull.add(file);
				codeClassOriginMap.put(file.getAbsolutePath(), repoUri);
				treeWalkPath.put(file, treeWalk.getPathString());
			    }
			}
		    }
		} catch (IOException e) {
		    System.out.println("Error while walking Git-Tree");
		    e.printStackTrace();
		}
		treeWalk.close();
	    }
	}
	return codeClassListFull;
    }

    public List<String> getIssuesKeysForFile(File file) {
	BlameResult blameResult = getGitBlameForFile(file);
	List<String> allKeys = new ArrayList<String>();
	if (blameResult == null) {
	    return Collections.emptyList();
	}
	try {
	    int lines = countLines(file);
	    for (int line = 0; line < lines; line++) {
		RevCommit revCommit = blameResult.getSourceCommit(line);
		if (revCommit != null) {
		    Set<String> returnValue = getJiraIssueKeys(revCommit.getFullMessage());
		    for (String val : returnValue) {
			if (!allKeys.contains(val)) {
			    allKeys.add(val);
			}
		    }
		}
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return allKeys;
    }

    private static int countLines(File aFile) throws IOException {
	LineNumberReader reader = null;
	try {
	    reader = new LineNumberReader(new FileReader(aFile));
	    while ((reader.readLine()) != null)
		;
	    return reader.getLineNumber();
	} catch (Exception ex) {
	    return -1;
	} finally {
	    if (reader != null)
		reader.close();
	}
    }

    private BlameResult getGitBlameForFile(File file) {
	BlameResult blameResult = null;
	if (file == null) {
	    return blameResult;
	}
	try {
	    String repoUri = codeClassOriginMap.get(file.getAbsolutePath());
	    Repository repository = gitClient.getGit(repoUri).getRepository();
	    BlameCommand blamer = new BlameCommand(repository);
	    blamer.setFilePath(treeWalkPath.get(file));
	    blamer.setStartCommit(gitClient.getDefaultBranchCommits(repoUri).get(0));
	    blameResult = blamer.call();
	    // blameResult =
	    // gitClient.getGit(repoUri).blame().setFilePath(gitFile.getPath()).call();
	} catch (RevisionSyntaxException | GitAPIException e) {
	    System.err.println("File could not be found.");
	    e.printStackTrace();
	}
	return blameResult;
    }

    private Set<String> getJiraIssueKeys(String message) {
	Set<String> keys = new LinkedHashSet<String>();
	if (projectKey == null) {
	    return keys;
	}
	String baseKey = projectKey.toUpperCase(Locale.ENGLISH);
	String pattern = "(" + baseKey + "-)\\d+";

	String[] words = message.split("[\\s,:]+");
	for (String word : words) {
	    word = word.toUpperCase(Locale.ENGLISH);
	    if (word.matches(pattern)) {
		keys.add(word);
	    }
	}
	return keys;
    }

    public Integer getNumberOfCodeClasses() {
	return codeClassListFull.size();
    }

    public List<File> getCodeClassListFull() {
	return codeClassListFull;
    }

    public void close() {
	gitClient.closeAll();
    }
}
