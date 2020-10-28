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
 * 
 * You need to make sure that the user "GIT-COMMIT-COMMENTATOR" has a project
 * role that is allowed to write comments. The user "GIT-COMMIT-COMMENTATOR" is
 * created automatically but needs to be manually associated with the project.
 */
public class CommitMessageToCommentTranscriber {
	private GitClient gitClient;
	private Issue issue;

	private static String COMMIT_COMMENTATOR_USER_NAME = "GIT-COMMIT-COMMENTATOR";

	public CommitMessageToCommentTranscriber(Issue jiraIssue) {
		this(jiraIssue, GitClient.getOrCreate(jiraIssue.getProjectObject().getKey()));
	}

	public CommitMessageToCommentTranscriber(Issue jiraIssue, GitClient gitClient) {
		this.issue = jiraIssue;
		this.gitClient = gitClient;
	}

	public void postComments() {
		ApplicationUser defaultUser = getUser();
		String projectKey = issue.getProjectObject().getKey();
		if (gitClient == null) {
			return;
		}
		if (ConfigPersistenceManager.isPostFeatureBranchCommitsActivated(projectKey)) {
			List<RevCommit> featureBranchCommits = new ArrayList<>();
			Ref branch = gitClient.getBranches(issue.getKey()).get(0);
			Optional.ofNullable(gitClient.getFeatureBranchCommits(issue)).ifPresent(featureBranchCommits::addAll);
			for (RevCommit commit : featureBranchCommits) {
				postComment(defaultUser, commit, branch);
			}
		}
		if (ConfigPersistenceManager.isPostSquashedCommitsActivated(projectKey)) {
			List<RevCommit> defaultBranchCommits = new ArrayList<>();
			Ref branch = gitClient.getGitClientsForSingleRepos().get(0).getDefaultBranch();
			Optional.ofNullable(gitClient.getDefaultBranchCommits(issue)).ifPresent(defaultBranchCommits::addAll);
			for (RevCommit commit : defaultBranchCommits) {
				postComment(defaultUser, commit, branch);
			}
		}
	}

	private ApplicationUser getUser() {
		ApplicationUser defaultUser;
		try {
			UserDetails userDetails = new UserDetails(COMMIT_COMMENTATOR_USER_NAME, COMMIT_COMMENTATOR_USER_NAME);
			defaultUser = ComponentAccessor.getUserManager().createUser(userDetails);
		} catch (CreateException | PermissionException e) {
			defaultUser = ComponentAccessor.getUserManager().getUserByName(COMMIT_COMMENTATOR_USER_NAME);
		}
		return defaultUser;
	}

	/**
	 * @return
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
	public Comment postComment(ApplicationUser user, RevCommit commit, Ref featureBranch) {
		String commentText = generateCommentString(commit, featureBranch);
		if (commentText == null || commentText.isBlank()) {
			return null;
		}
		for (Comment alreadyWrittenComment : ComponentAccessor.getCommentManager().getComments(issue)) {
			// if the hash of a commit is present in a comment, do not post it again
			if (alreadyWrittenComment.getBody().contains(commit.getName())) {
				return null;
			}
		}
		return ComponentAccessor.getCommentManager().create(issue, user, commentText, true);
	}

	public String generateCommentString(RevCommit commit, Ref branch) {
		String comment = commit.getFullMessage();
		if (comment == null || comment.isBlank()) {
			return "";
		}
		comment = replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(comment);
		StringBuilder builder = new StringBuilder(comment);
		builder.append("\r\n\r\n");
		builder.append("Author: " + commit.getAuthorIdent().getName() + "\r\n");
		builder.append(
				"Repository and Branch: " + gitClient.getRepoUriFromBranch(branch) + " " + branch.getName() + "\r\n");
		builder.append("Commit Hash: " + commit.getName());
		return builder.toString();
	}

	private String replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(String comment) {
		for (String tag : KnowledgeType.toStringList()) {
			String replaceString = "{" + tag.toLowerCase() + "}";
			comment = comment.replaceAll(GitDecXtract.generateRegexToFindAllTags(tag), replaceString);
		}
		return comment;
	}
}
