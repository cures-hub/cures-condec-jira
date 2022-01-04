package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.CommentSummary;
import com.atlassian.jira.issue.comments.MockComment;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.json.JSONObject;

public class MockCommentManager implements CommentManager {

	private List<Comment> comments;

	public MockCommentManager() {
		comments = new ArrayList<>();
	}

	@Override
	public List<Comment> getCommentsForUser(Issue issue, ApplicationUser applicationUser) {
		return null;
	}

	@Override
	public Stream<Comment> streamComments(@Nullable ApplicationUser applicationUser, @Nonnull Issue issue) {
		return null;
	}

	@Override
	public CommentSummary getCommentSummary(@Nullable ApplicationUser applicationUser, @Nonnull Issue issue,
			@Nonnull Optional<Long> optional) {
		return null;
	}

	@Override
	public Comment getLastComment(Issue issue) {
		return comments.get(0);
	}

	@Nonnull
	@Override
	public List<Comment> getCommentsForUserSince(@Nonnull Issue issue, @Nullable ApplicationUser applicationUser,
			@Nonnull Date date) {
		return null;
	}

	@Override
	public List<Comment> getComments(Issue issue) {
		return comments.stream().filter(comment -> comment.getIssue().equals(issue)).collect(Collectors.toList());
	}

	@Override
	public Comment create(Issue issue, ApplicationUser applicationUser, String string, boolean dispatchEvent) {
		Comment comment = new MockComment((long) 1337, applicationUser.getName(), string, null, null, new Date(),
				issue);
		comments.add(comment);
		return comment;
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
	public Comment create(Issue issue, ApplicationUser applicationUser, String s, String s1, Long aLong, Date date,
			boolean b) {
		return null;
	}

	@Override
	public Comment create(Issue issue, ApplicationUser applicationUser, String s, String s1, Long aLong, Date date,
			Map<String, JSONObject> map, boolean b) {
		return null;
	}

	@Override
	public Comment create(Issue issue, String s, String s1, String s2, Long aLong, Date date, boolean b) {
		return null;
	}

	@Override
	public Comment create(Issue issue, ApplicationUser applicationUser, ApplicationUser applicationUser1, String s,
			String s1, Long aLong, Date date, Date date1, boolean b) {
		return null;
	}

	@Override
	public Comment create(Issue issue, String s, String s1, String s2, String s3, Long aLong, Date date, Date date1,
			boolean b) {
		return null;
	}

	@Override
	public Comment create(Issue issue, ApplicationUser applicationUser, ApplicationUser applicationUser1, String s,
			String s1, Long aLong, Date date, Date date1, boolean b, boolean b1) {
		return null;
	}

	@Override
	public Comment create(Issue issue, ApplicationUser applicationUser, ApplicationUser applicationUser1, String s,
			String s1, Long aLong, Date date, Date date1, Map<String, JSONObject> map, boolean b, boolean b1) {
		return null;
	}

	@Override
	public Comment create(Issue issue, String s, String s1, String s2, String s3, Long aLong, Date date, Date date1,
			boolean b, boolean b1) {
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
	public Comment getCommentById(Long id) {
		for (Comment comment : comments) {
			if (comment.getId() == id) {
				return comment;
			}
		}
		if (comments.size() > 0) {
			return comments.get(0);
		}
		return null;
	}

	@Override
	public MutableComment getMutableComment(Long aLong) {
		if (comments.size() > 0) {
			return (MutableComment) comments.get(0);
		}
		return null;
	}

	@Override
	public void update(Comment comment, boolean b) {
		int index = 0;
		for (int i = 0; i < comments.size(); i++) {
			if (comments.get(i).getId() == comment.getId()) {
				index = i;
			}
		}
		comments.remove(index);
		comments.add(comment);
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
		comments.remove(comment);
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
		comments.removeAll(getComments(issue));
	}
}
