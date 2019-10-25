package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

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
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

public class CommitMessageToCommentTranscriber {
    private Issue issue;
    private Ref branch;
    private  List<RevCommit> commits;
    //commit message als param
    private static String DEFAULT_COMMIT_COMMENTATOR_USR_NAME = "GIT-COMMIT-COMMENTATOR";
    private static UserDetails DEFAULT_COMMIT_COMMENTATOR_USR_DETAILS = new UserDetails(DEFAULT_COMMIT_COMMENTATOR_USR_NAME, DEFAULT_COMMIT_COMMENTATOR_USR_NAME);

    public CommitMessageToCommentTranscriber(Issue issue, Ref branch) {
        this.issue = issue;
        this.branch = branch;
        this.commits = new ArrayList<>();
        GitClient client =  ComponentGetter.getGitClient(this.issue.getProjectObject().getKey());
        commits.addAll(client.getFeatureBranchCommits(this.branch));
        commits.addAll(client.getCommits(this.issue));
    }

    public String generateCommentStrings(RevCommit commit) {
        String comment = commit.getFullMessage();

        for (String tag : KnowledgeType.toList()) {
            String replaceString = "{" + tag.toLowerCase() + "}";
            comment = comment.replaceAll(GitDecXtract.generateRegexToFindAllTags(tag), replaceString);
        }
        StringBuilder builder = new StringBuilder(comment);
        builder.append("\r\n");
        builder.append("--- Commit meta data --- \r\n");
        builder.append("Author: " + commit.getAuthorIdent().getName() + "\r\n");
        builder.append("Branch: " + this.branch.getName() + "\r\n");
        builder.append("Hash: " + commit.getName());
        return (builder.toString());
    }

    public void postComment(ApplicationUser user, RevCommit commit) {
        String commentString = this.generateCommentStrings(commit);
        if (commentString != null && !commentString.equals("")) {
            /*
             * @Issue: Should we make a user for commenting commit messages under an issue?
             *
             * @Alternative: Yes.
             * @Pro: Its clear what comments were originally commit messages.
             *
             * @Alternative: No.
             * @Con: It would  be confusing to users if they see that they posted something that they did no write.
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

    public void postComments() throws PermissionException {
        ApplicationUser defaultUser;
        try {
            defaultUser = ComponentAccessor.getUserManager().createUser(DEFAULT_COMMIT_COMMENTATOR_USR_DETAILS);
        } catch (CreateException e) {
            defaultUser = ComponentAccessor.getUserManager().getUserByName(DEFAULT_COMMIT_COMMENTATOR_USR_NAME);
        }
        for (RevCommit commit : this.commits) {
            this.postComment(defaultUser, commit);
        }
    }


}
