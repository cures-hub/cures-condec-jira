package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
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

    public GitCodeClassExtractor(String projectKey) {
	this.projectKey = projectKey;
	this.codeClassListFull = getCodeClassFiles();
    }

    public List<File> getCodeClassFiles() {
	List<File> codeClassListFull = new ArrayList<File>();
	GitClient gitClient = new GitClientImpl(projectKey);
	for (String repoUri : gitClient.getRemoteUris()) {
	    List<File> codeClassList = new ArrayList<File>();
	    Repository repository = gitClient.getRepository(repoUri);
	    if (repository != null) {
		TreeWalk treeWalk = new TreeWalk(repository);
		try {
		    treeWalk.addTree(getHeadCommit(gitClient.getGit(repoUri)).getTree());
		    treeWalk.setRecursive(false);
		    while (treeWalk.next()) {
			if (treeWalk.isSubtree()) {
			    treeWalk.enterSubtree();
			} else {
			    File file = new File(treeWalk.getPathString());
			    ChangedFile chfile = new ChangedFileImpl(file);
			    if (chfile.isJavaClass()) {
				codeClassList.add(file);
			    } /*
			       * //Only needed if ChangedFile.isJavaClass() doesnt work if
			       * (file.getName().endsWith(".java")) { codeClassList.add(file); }
			       */
			}
		    }
		} catch (IOException e) {
		    System.out.println("Error while walking Git-Tree");
		    e.printStackTrace();
		}
		treeWalk.close();
	    }
	    codeClassListFull.addAll(codeClassList);
	}
	return codeClassListFull;
    }

    public Integer getNumberOfCodeClasses() {
	return codeClassListFull.size();
    }

    private static RevCommit getHeadCommit(Git git) {
	try {
	    Iterable<RevCommit> history = git.log().setMaxCount(1).call();
	    return history.iterator().next();
	} catch (Exception e) {
	    System.out.println("Could not get Head Commit");
	    e.printStackTrace();
	}
	return null;

    }
}
