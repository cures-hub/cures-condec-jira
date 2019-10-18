package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class CommitMessageToCommentTranscriber {
    private String commitMessage;

    public CommitMessageToCommentTranscriber(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String generateCommentString() {
        String comment = this.commitMessage;
        for (String tag : KnowledgeType.toList()) {
            String replaceString = "{" + tag.toLowerCase() + "}";
            comment = comment.replaceAll(GitDecXtract.generateRegexToFindAllTags(tag), replaceString);
        }
        return comment;
    }


}
