package de.uhd.ifi.se.decision.management.jira.git;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserDetails;

import de.uhd.ifi.se.decision.management.jira.git.parser.RationaleFromCommitMessageParser;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(CommitMessageToCommentTranscriber.class);

	public CommitMessageToCommentTranscriber(Issue jiraIssue) {
		this.jiraIssue = jiraIssue;
		if (jiraIssue != null) {
			gitClient = GitClient.getInstance(jiraIssue.getProjectObject().getKey());
		}
	}

	/**
	 * @return list of newly created comments. If all commits of the Jira issue were
	 *         already posted, an empty list is returned.
	 */
	public List<Comment> postCommitsIntoJiraIssueComments() {
		if (jiraIssue == null || gitClient == null) {
			LOGGER.error(
					"Commit messages cannot be posted to Jira issue comment because preconditions are not fullfilled.");
			return new ArrayList<>();
		}
		List<Comment> newComments = new ArrayList<>();
		String projectKey = jiraIssue.getProjectObject().getKey();
		if (ConfigPersistenceManager.getGitConfiguration(projectKey).isPostFeatureBranchCommitsActivated()) {
			newComments.addAll(postFeatureBranchCommits());
		}
		if (ConfigPersistenceManager.getGitConfiguration(projectKey).isPostDefaultBranchCommitsActivated()) {
			newComments.addAll(postDefaultBranchCommits());
		}
		return newComments;
	}

	public List<Comment> postFeatureBranchCommits() {
		List<Comment> newComments = new ArrayList<>();
		for (Ref featureBranch : gitClient.getBranches(jiraIssue.getKey())) {
			List<RevCommit> featureBranchCommits = gitClient.getFeatureBranchCommits(featureBranch);
			String uri = gitClient.getRepoUriFromBranch(featureBranch);
			newComments.addAll(postCommitsIntoJiraIssueComments(featureBranchCommits, featureBranch, uri));
		}
		return newComments;
	}

	public List<Comment> postDefaultBranchCommits() {
		List<Comment> newComments = new ArrayList<>();
		for (GitClientForSingleRepository gitClientForSingleRepository : gitClient.getGitClientsForSingleRepos()) {
			Ref branch = gitClientForSingleRepository.getDefaultBranch();
			List<RevCommit> defaultBranchCommits = gitClientForSingleRepository.getCommits(jiraIssue, true);
			String uri = gitClientForSingleRepository.getRemoteUri();
			newComments.addAll(postCommitsIntoJiraIssueComments(defaultBranchCommits, branch, uri));
		}
		return newComments;
	}

	private List<Comment> postCommitsIntoJiraIssueComments(List<RevCommit> commits, Ref branch, String uri) {
		List<Comment> newComments = new ArrayList<>();
		for (RevCommit commit : commits) {
			Comment comment = postCommitIntoJiraIssueComment(commit, branch, uri);
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
	private Comment postCommitIntoJiraIssueComment(RevCommit commit, Ref branch, String uri) {
		String commentText = generateCommentString(commit, branch, uri);
		if (commentText == null || commentText.isBlank()) {
			LOGGER.warn("Commit messages cannot be posted to Jira issue comment because comment text would be blank.");
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
	 * @decision The user "GIT-COMMIT-COMMENTATOR" creates the Jira issue comment
	 *           that a commit messages was posted into!
	 * @pro It is clear that the comment origined from a commit messages.
	 * @alternative The user that opens the Jira issue could be the creator of the
	 *              Jira issue comment that a commit messages was posted into.
	 * @con It would be confusing to users if they see that they posted something
	 *      that they did not write.
	 * @alternative The git user could be the creator of the Jira issue comment that
	 *              a commit messages was posted into.
	 * @con Git user names can be different to Jira user names and it is hard to
	 *      match them.
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

	public String generateCommentString(RevCommit commit, Ref branch, String uri) {
		if (commit == null || commit.getFullMessage().isBlank() || branch == null) {
			return "";
		}
		String comment = replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(commit.getFullMessage());
		comment += "\r\n\r\n";
		comment += "Author: " + commit.getAuthorIdent().getName() + "\r\n";
		comment += "Repository and Branch: " + uri + " " + branch.getName() + "\r\n";
		comment += "Commit Hash: " + commit.getName();
		return comment;
	}

	public static String replaceAnnotationsUsedInCommitsWithAnnotationsUsedInJira(String comment) {
		for (String tag : KnowledgeType.toStringList()) {
			String replaceString = "{" + tag.toLowerCase() + "}";
			comment = comment.replaceAll(generateRegexToFindAllTags(tag), replaceString);
		}
		return comment;
	}

	public static String generateRegexToFindAllTags(String tag) {
		return RationaleFromCommitMessageParser.generateRegexForOpenTag(tag) + "|"
				+ RationaleFromCommitMessageParser.generateRegexForCloseTag(tag);
	}

	public void setGitClient(GitClient gitClient) {
		this.gitClient = gitClient;
	}
}
