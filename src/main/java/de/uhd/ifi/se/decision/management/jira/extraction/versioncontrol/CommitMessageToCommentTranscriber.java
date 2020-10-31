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
	private Issue jiraIssue;

	private static String COMMIT_COMMENTATOR_USER_NAME = "GIT-COMMIT-COMMENTATOR";

	public CommitMessageToCommentTranscriber(Issue jiraIssue) {
		this.jiraIssue = jiraIssue;
		this.gitClient = GitClient.getOrCreate(jiraIssue.getProjectObject().getKey());
	}

	public List<Comment> postCommitsIntoJiraIssueComments() {
		if (jiraIssue == null || gitClient == null) {
			return new ArrayList<>();
		}
		List<Comment> newComments = new ArrayList<>();
		String projectKey = jiraIssue.getProjectObject().getKey();
		if (ConfigPersistenceManager.isPostFeatureBranchCommitsActivated(projectKey)) {
			newComments.addAll(postFeatureBranchCommits());
		}
		if (ConfigPersistenceManager.isPostSquashedCommitsActivated(projectKey)) {
			newComments.addAll(postDefaultBranchCommits());
		}
		return newComments;
	}

	public List<Comment> postFeatureBranchCommits() {
		List<RevCommit> featureBranchCommits = new ArrayList<>();
		Ref branch = gitClient.getBranches(jiraIssue.getKey()).get(0);
		Optional.ofNullable(gitClient.getFeatureBranchCommits(branch)).ifPresent(featureBranchCommits::addAll);
		return postCommitsIntoJiraIssueComments(featureBranchCommits, branch);
	}

	public List<Comment> postDefaultBranchCommits() {
		List<RevCommit> defaultBranchCommits = new ArrayList<>();
		Ref branch = gitClient.getGitClientsForSingleRepos().get(0).getDefaultBranch();
		Optional.ofNullable(gitClient.getDefaultBranchCommits(jiraIssue)).ifPresent(defaultBranchCommits::addAll);
		return postCommitsIntoJiraIssueComments(defaultBranchCommits, branch);
	}

	private List<Comment> postCommitsIntoJiraIssueComments(List<RevCommit> commits, Ref branch) {
		List<Comment> newComments = new ArrayList<>();
		for (RevCommit commit : commits) {
			Comment comment = postCommitIntoJiraIssueComment(commit, branch);
			if (comment != null) {
				newComments.add(comment);
			}
		}
		return newComments;
	}

	/**
	 * @return new comment or null if no comment was created because the commit was
	 *         already posted.
	 */
	private Comment postCommitIntoJiraIssueComment(RevCommit commit, Ref branch) {
		String commentText = generateCommentString(commit, branch);
		if (commentText == null || commentText.isBlank()) {
			return null;
		}
		for (Comment alreadyWrittenComment : ComponentAccessor.getCommentManager().getComments(jiraIssue)) {
			// if the hash of a commit is present in a comment, do not post it again
			if (alreadyWrittenComment.getBody().contains(commit.getName())) {
				return null;
			}
		}
		ApplicationUser user = getUser();
		return ComponentAccessor.getCommentManager().create(jiraIssue, user, commentText, true);
	}

	/**
	 * @issue Who should be the author of the new Jira issue comment that a commit
	 *        messages was posted into?
	 * @alternative The user "GIT-COMMIT-COMMENTATOR" creates the Jira issue comment
	 *              that a commit messages was posted into!
	 * @pro It is clear that the comment origined from a commit messages.
	 * @alternative The user that opens the Jira issue could be the creator of the
	 *              Jira issue comment that a commit messages was posted into.
	 * @con It would be confusing to users if they see that they posted something
	 *      that they did no write.
	 */
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

	public String generateCommentString(RevCommit commit, Ref branch) {
		if (commit == null || commit.getFullMessage().isBlank() || branch == null) {
			return "";
		}
		String comment = replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(commit.getFullMessage());
		StringBuilder builder = new StringBuilder(comment);
		builder.append("\r\n\r\n");
		builder.append("Author: " + commit.getAuthorIdent().getName() + "\r\n");
		builder.append(
				"Repository and Branch: " + gitClient.getRepoUriFromBranch(branch) + " " + branch.getName() + "\r\n");
		builder.append("Commit Hash: " + commit.getName());
		return builder.toString();
	}

	public static String replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(String comment) {
		for (String tag : KnowledgeType.toStringList()) {
			String replaceString = "{" + tag.toLowerCase() + "}";
			comment = comment.replaceAll(GitDecXtract.generateRegexToFindAllTags(tag), replaceString);
		}
		return comment;
	}
}
