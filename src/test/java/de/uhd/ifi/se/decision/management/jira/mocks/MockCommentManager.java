package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.*;
import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.json.JSONObject;
import org.ofbiz.core.entity.GenericValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MockCommentManager implements CommentManager {
	
	private List<Comment> comments;
	
    @Override
    public List<Comment> getCommentsForUser(Issue issue, ApplicationUser applicationUser) {
        return null;
    }

    @Override
    public Stream<Comment> streamComments(@Nullable ApplicationUser applicationUser, @Nonnull Issue issue) {
        return null;
    }

    @Override
    public CommentSummary getCommentSummary(@Nullable ApplicationUser applicationUser, @Nonnull Issue issue, @Nonnull Optional<Long> optional) {
        return null;
    }

    @Override
    public Comment getLastComment(Issue issue) {
        return this.comments.get(0);
    }

    @Nonnull
    @Override
    public List<Comment> getCommentsForUserSince(@Nonnull Issue issue, @Nullable ApplicationUser applicationUser, @Nonnull Date date) {
        return null;
    }

    @Override
    public List<Comment> getComments(Issue issue) {
        return this.comments;
    }

    @Override
    public Comment create(Issue issue, ApplicationUser applicationUser, String s, boolean b) {
    	if(this.comments == null) {
    		this.comments = new ArrayList<Comment>();
    	}
    	Comment c = new MockComment(issue,applicationUser,s);
    	this.comments.add(c);
        return c;
    }

    @Override
    public Comment create(Issue issue, String s, String s1, boolean b) {
        return null;
    }

    @Override
    public Comment create(Issue issue, ApplicationUser applicationUser, String s, String s1, Long aLong, boolean b) {
        return null;
    }

    @Override
    public Comment create(Issue issue, String s, String s1, String s2, Long aLong, boolean b) {
        return null;
    }

    @Override
    public Comment create(Issue issue, ApplicationUser applicationUser, String s, String s1, Long aLong, Date date, boolean b) {
        return null;
    }

    @Override
    public Comment create(Issue issue, ApplicationUser applicationUser, String s, String s1, Long aLong, Date date, Map<String, JSONObject> map, boolean b) {
        return null;
    }

    @Override
    public Comment create(Issue issue, String s, String s1, String s2, Long aLong, Date date, boolean b) {
        return null;
    }

    @Override
    public Comment create(Issue issue, ApplicationUser applicationUser, ApplicationUser applicationUser1, String s, String s1, Long aLong, Date date, Date date1, boolean b) {
        return null;
    }

    @Override
    public Comment create(Issue issue, String s, String s1, String s2, String s3, Long aLong, Date date, Date date1, boolean b) {
        return null;
    }

    @Override
    public Comment create(Issue issue, ApplicationUser applicationUser, ApplicationUser applicationUser1, String s, String s1, Long aLong, Date date, Date date1, boolean b, boolean b1) {
        return null;
    }

    @Override
    public Comment create(Issue issue, ApplicationUser applicationUser, ApplicationUser applicationUser1, String s, String s1, Long aLong, Date date, Date date1, Map<String, JSONObject> map, boolean b, boolean b1) {
        return null;
    }

    @Override
    public Comment create(Issue issue, String s, String s1, String s2, String s3, Long aLong, Date date, Date date1, boolean b, boolean b1) {
        return null;
    }

    @Override
    public ProjectRole getProjectRole(Long aLong) {
        return null;
    }

    @Override
    public Comment convertToComment(GenericValue genericValue) {
        return null;
    }

    @Override
    public Comment getCommentById(Long aLong) {
        return this.comments.get(0);
    }

    @Override
    public MutableComment getMutableComment(Long aLong) {
        return null;
    }

    @Override
    public void update(Comment comment, boolean b) {

    }

    @Override
    public void update(Comment comment, Map<String, JSONObject> map, boolean b) {

    }

    @Override
    public int swapCommentGroupRestriction(String s, String s1) {
        return 0;
    }

    @Override
    public long getCountForCommentsRestrictedByGroup(String s) {
        return 0;
    }

    @Override
    public long getCountForCommentsRestrictedByRole(Long aLong) {
        return 0;
    }

    @Override
    public ChangeItemBean delete(Comment comment) {
        return null;
    }

    @Override
    public void delete(Comment comment, boolean b, ApplicationUser applicationUser) {

    }

    @Override
    public boolean isUserCommentAuthor(ApplicationUser applicationUser, Comment comment) {
        return false;
    }

    @Override
    public int swapCommentRoleRestriction(Long aLong, Long aLong1) {
        return 0;
    }

    @Override
    public void deleteCommentsForIssue(Issue issue) {

    }
}
