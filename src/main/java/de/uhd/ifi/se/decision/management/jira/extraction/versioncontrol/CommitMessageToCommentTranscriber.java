package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class CommitMessageToCommentTranscriber {
    private String commitMessage;
    private String comment;

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
        if (comment != null || comment != "") {
            // AskAnja: Should we make custom git-commit-message user?
            // ComponentAccessor.getUserManager().createUser(new UserDetails("git-commit-message", "git-commit-message"));
            /*
             * @Issue: Should we make a user for commenting commit messages under an issue?
             */
            for(Comment alreadyWrittenComment : ComponentAccessor.getCommentManager().getCommentsForUser(issue, user)){
                if (alreadyWrittenComment.getBody().equals(comment)){
                    return;
                }
            }
            ComponentAccessor.getCommentManager().create(issue, user, comment, true);
        }
    }

    public void postComment(Issue issue, ApplicationUser user) {
        this.postComment(issue, user, this.comment);
    }


}
