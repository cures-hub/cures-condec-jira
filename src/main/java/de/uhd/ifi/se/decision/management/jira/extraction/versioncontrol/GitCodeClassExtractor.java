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

public class GitCodeClassExtractor {

    private String projectKey;
    private List<File> codeClassList;

    public GitCodeClassExtractor(String projectKey) {
	this.projectKey = projectKey;
	this.codeClassList = getCodeClassFiles();
    }

    public List<File> getCodeClassFiles() {
	List<File> codeClassList = new ArrayList<File>();
	GitClient gitClient = new GitClientImpl(projectKey);
	Repository repository = gitClient.getRepository();
	if (repository != null) {
	    TreeWalk treeWalk = new TreeWalk(repository);
	    try {
		treeWalk.addTree(getHeadCommit(gitClient.getGit()).getTree());
		treeWalk.setRecursive(false);
		while (treeWalk.next()) {
		    if (treeWalk.isSubtree()) {
			treeWalk.enterSubtree();
		    } else {
			File file = new File(treeWalk.getPathString());
			if (file.getName().endsWith(".java")) {
			    codeClassList.add(file);
			}
		    }
		}
	    } catch (IOException e) {
		System.out.println("Error while walking Git-Tree");
		e.printStackTrace();
	    }
	    treeWalk.close();
	}
	return codeClassList;
    }

    public Integer getNumberOfCodeClasses() {
	return codeClassList.size();
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
