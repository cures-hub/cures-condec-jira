package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserDetails;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import org.hsqldb.User;

public class CommitMessageToCommentTranscriber {
    private String commitMessage;
    private String comment;
    private static String DEFAULT_COMMIT_COMMENTATOR_USR_NAME = "GIT-COMMIT-COMMENTATOR";
    private static UserDetails DEFAULT_COMMIT_COMMENTATOR_USR_DETAILS = new UserDetails(DEFAULT_COMMIT_COMMENTATOR_USR_NAME, DEFAULT_COMMIT_COMMENTATOR_USR_NAME);

    public CommitMessageToCommentTranscriber(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String generateCommentString() {
        String comment = this.commitMessage;
        for (String tag : KnowledgeType.toList()) {
            String replaceString = "{" + tag.toLowerCase() + "}";
            comment = comment.replaceAll(GitDecXtract.generateRegexToFindAllTags(tag), replaceString);
        }
        this.comment = comment;
        return comment;
    }

    public void postComment(Issue issue, ApplicationUser user, String comment) {
        if (comment != null && !comment.equals("")) {
            /*
             * @Issue: Should we make a user for commenting commit messages under an issue?
             */
            for (Comment alreadyWrittenComment : ComponentAccessor.getCommentManager().getComments(issue)) {
                if (alreadyWrittenComment.getBody().equals(comment)) {
                    return;
                }
            }
            ComponentAccessor.getCommentManager().create(issue, user, comment, true);
        }
    }

    public void postComment(Issue issue) throws PermissionException {
        ApplicationUser defaultUser;
        try {
            defaultUser = ComponentAccessor.getUserManager().createUser(DEFAULT_COMMIT_COMMENTATOR_USR_DETAILS);
        } catch (CreateException e) {
            defaultUser = ComponentAccessor.getUserManager().getUserByName(DEFAULT_COMMIT_COMMENTATOR_USR_NAME);
        }
        this.postComment(issue, defaultUser, this.comment);
    }


}
