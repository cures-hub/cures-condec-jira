package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserDetails;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class CommitMessageToCommentTranscriber {
    private final GitClient gitClient;
    private Issue issue;
    private List<RevCommit> featureBranchCommits;
    private List<RevCommit> squashedCommits;

    // commit message als param
    private static String DEFAULT_COMMIT_COMMENTATOR_USR_NAME = "GIT-COMMIT-COMMENTATOR";
    private static UserDetails DEFAULT_COMMIT_COMMENTATOR_USR_DETAILS = new UserDetails(
	    DEFAULT_COMMIT_COMMENTATOR_USR_NAME, DEFAULT_COMMIT_COMMENTATOR_USR_NAME);

    public CommitMessageToCommentTranscriber(Issue issue) {
	this(issue, ComponentGetter.getGitClient(issue.getProjectObject().getKey()));
    }

    public CommitMessageToCommentTranscriber(Issue issue, GitClient gitClient) {
	this.issue = issue;
	this.featureBranchCommits = new ArrayList<>();
	this.gitClient = gitClient;
	this.squashedCommits = new ArrayList<>();
    }

    public String generateCommentString(RevCommit commit, Ref featureBranch) {
	String comment = commit.getFullMessage();
	if (comment != null && !comment.equals("")) {
	    for (String tag : KnowledgeType.toList()) {
		String replaceString = "{" + tag.toLowerCase() + "}";
		comment = comment.replaceAll(GitDecXtract.generateRegexToFindAllTags(tag), replaceString);
	    }
	    StringBuilder builder = new StringBuilder(comment);
	    builder.append("\r\n");
	    builder.append("> Commit meta data\r\n");
	    builder.append("> Author: " + commit.getAuthorIdent().getName() + "\r\n");
	    builder.append("> Branch: " + featureBranch.getName() + "\r\n");
	    builder.append("> Repository: " + gitClient.getRepoUriFromBranch(featureBranch) + "\r\n");
	    builder.append("> Hash: " + commit.getName());
	    return (builder.toString());
	}
	return "";
    }

    public void postComment(ApplicationUser user, RevCommit commit, Ref featureBranch) {
	String commentString = this.generateCommentString(commit, featureBranch);
	if (commentString != null && !commentString.equals("")) {
	    /*
	     * @Issue: Should we make a user for commenting commit messages under an issue?
	     *
	     * @Alternative: Yes.
	     * 
	     * @Pro: Its clear what comments were originally commit messages.
	     *
	     * @Alternative: No.
	     * 
	     * @Con: It would be confusing to users if they see that they posted something
	     * that they did no write.
	     */

	    for (Comment alreadyWrittenComment : ComponentAccessor.getCommentManager().getComments(this.issue)) {
		// if the hash of a commit is present in a comment, do not post it again.
		if (alreadyWrittenComment.getBody().contains(commit.getName())) {
		    return;
		}
	    }
	    ComponentAccessor.getCommentManager().create(issue, user, commentString, true);
	}
    }

    public void postComments(Ref branch) throws PermissionException {
	ApplicationUser defaultUser = getUser();
	String projectKey = this.issue.getProjectObject().getKey();
	if (gitClient == null) {
	    return;
	}
	String repoUri = gitClient.getRepoUriFromBranch(branch);
	if (branch.getName().contains("/" + gitClient.getDefaultBranchFolderNames().get(repoUri))) {
	    if (Boolean.parseBoolean(ConfigPersistenceManager.getValue(projectKey, "isPostSquashedCommitsActivated"))) {
		Optional.ofNullable(gitClient.getAllRelatedCommits(this.issue)).ifPresent(squashedCommits::addAll);
		for (RevCommit commit : this.squashedCommits) {
		    this.postComment(defaultUser, commit, branch);
		}
	    }
	} else {
	    if (Boolean.parseBoolean(
		    ConfigPersistenceManager.getValue(projectKey, "isPostFeatureBranchCommitsActivated"))) {
		Optional.ofNullable(gitClient.getFeatureBranchCommits(branch)).ifPresent(featureBranchCommits::addAll);
		for (RevCommit commit : this.featureBranchCommits) {
		    this.postComment(defaultUser, commit, branch);
		}
	    }
	}

    }

    private ApplicationUser getUser() throws PermissionException {
	ApplicationUser defaultUser;
	try {
	    defaultUser = ComponentAccessor.getUserManager().createUser(DEFAULT_COMMIT_COMMENTATOR_USR_DETAILS);
	} catch (CreateException e) {
	    defaultUser = ComponentAccessor.getUserManager().getUserByName(DEFAULT_COMMIT_COMMENTATOR_USR_NAME);
	}
	return defaultUser;
    }

}
