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
            String regexToReplace = "\\[" + tag + "\\]" + "|" + "\\[\\/" + tag + "\\]";
            String replaceString = "{" + tag + "}";
            comment = comment.replaceAll(regexToReplace, replaceString);
        }
        return comment;
    }
}
