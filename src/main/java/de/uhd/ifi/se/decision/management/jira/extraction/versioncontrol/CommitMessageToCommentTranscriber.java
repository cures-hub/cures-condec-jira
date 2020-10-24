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

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

/**
 * Posts a commit message into a comment of the Jira issue if the Jira issue key
 * is mentioned in the commit message. Consequently, the decision knowledge in
 * the commit message is added to the {@link KnowledgeGraph} and can be changed
 * in Jira.
 */
public class CommitMessageToCommentTranscriber {
	private GitClient gitClient;
	private Issue issue;
	private List<RevCommit> featureBranchCommits;
	private List<RevCommit> defaultBranchCommits;

	private static String DEFAULT_COMMIT_COMMENTATOR_USER_NAME = "GIT-COMMIT-COMMENTATOR";
	private static UserDetails DEFAULT_COMMIT_COMMENTATOR_USR_DETAILS = new UserDetails(
			DEFAULT_COMMIT_COMMENTATOR_USER_NAME, DEFAULT_COMMIT_COMMENTATOR_USER_NAME);

	public CommitMessageToCommentTranscriber(Issue issue) {
		this(issue, GitClient.getOrCreate(issue.getProjectObject().getKey()));
	}

	public CommitMessageToCommentTranscriber(Issue issue, GitClient gitClient) {
		this.issue = issue;
		this.gitClient = gitClient;
		this.featureBranchCommits = new ArrayList<>();
		this.defaultBranchCommits = new ArrayList<>();
	}

	/**
	 * @Issue Who should be the author of the new Jira issue comment that a commit
	 *        messages was posted into?
	 * @Alternative The user "GIT-COMMIT-COMMENTATOR" creates the Jira issue comment
	 *              that a commit messages was posted into!
	 * @Pro It is clear that the comment origined from a commit messages.
	 * @Alternative The user that opens the Jira issue could be the creator of the
	 *              Jira issue comment that a commit messages was posted into.
	 * @Con It would be confusing to users if they see that they posted something
	 *      that they did no write.
	 */
	public void postComment(ApplicationUser user, RevCommit commit, Ref featureBranch) {
		String commentText = generateCommentString(commit, featureBranch);
		if (commentText == null || commentText.isBlank()) {
			return;
		}
		for (Comment alreadyWrittenComment : ComponentAccessor.getCommentManager().getComments(issue)) {
			// if the hash of a commit is present in a comment, do not post it again
			if (alreadyWrittenComment.getBody().contains(commit.getName())) {
				return;
			}
		}
		ComponentAccessor.getCommentManager().create(issue, user, commentText, true);
	}

	public String generateCommentString(RevCommit commit, Ref featureBranch) {
		String comment = commit.getFullMessage();
		if (comment == null || comment.isBlank()) {
			return "";
		}
		comment = replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(comment);
		StringBuilder builder = new StringBuilder(comment);
		builder.append("\r\n");
		builder.append("> Commit meta data\r\n");
		builder.append("> Author: " + commit.getAuthorIdent().getName() + "\r\n");
		builder.append("> Branch: " + featureBranch.getName() + "\r\n");
		builder.append("> Repository: " + gitClient.getRepoUriFromBranch(featureBranch) + "\r\n");
		builder.append("> Hash: " + commit.getName());
		return builder.toString();
	}

	private String replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(String comment) {
		for (String tag : KnowledgeType.toStringList()) {
			String replaceString = "{" + tag.toLowerCase() + "}";
			comment = comment.replaceAll(GitDecXtract.generateRegexToFindAllTags(tag), replaceString);
		}
		return comment;
	}

	public void postComments(Ref branch) {
		ApplicationUser defaultUser = getUser();
		String projectKey = issue.getProjectObject().getKey();
		if (gitClient == null) {
			return;
		}
		String repoUri = gitClient.getRepoUriFromBranch(branch);
		if (branch.getName().contains("/" + gitClient.getGitClientsForSingleRepo(repoUri).getDefaultBranchName())) {
			if (ConfigPersistenceManager.isPostSquashedCommitsActivated(projectKey)) {
				Optional.ofNullable(gitClient.getCommits(issue)).ifPresent(defaultBranchCommits::addAll);
				for (RevCommit commit : defaultBranchCommits) {
					this.postComment(defaultUser, commit, branch);
				}
			}
		} else {
			if (ConfigPersistenceManager.isPostFeatureBranchCommitsActivated(projectKey)) {
				Optional.ofNullable(gitClient.getFeatureBranchCommits(branch)).ifPresent(featureBranchCommits::addAll);
				for (RevCommit commit : this.featureBranchCommits) {
					this.postComment(defaultUser, commit, branch);
				}
			}
		}
	}

	private ApplicationUser getUser() {
		ApplicationUser defaultUser;
		try {
			defaultUser = ComponentAccessor.getUserManager().createUser(DEFAULT_COMMIT_COMMENTATOR_USR_DETAILS);
		} catch (CreateException | PermissionException e) {
			defaultUser = ComponentAccessor.getUserManager().getUserByName(DEFAULT_COMMIT_COMMENTATOR_USER_NAME);
		}
		return defaultUser;
	}

}
