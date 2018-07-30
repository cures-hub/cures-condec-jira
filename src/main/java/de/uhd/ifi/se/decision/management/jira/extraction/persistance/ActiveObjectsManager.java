package de.uhd.ifi.se.decision.management.jira.extraction.persistance;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;

public class ActiveObjectsManager {

	private static ActiveObjects ao;

	public static void init() {
		if (ao == null) {
			ao = ComponentGetter.getActiveObjects();
		}
	}

	public static long addElement(long commentId, boolean isRelevant, int endSubStringCount, int startSubstringCount,
			long userId) {
		init();
		if (!checkElementExistingInAO(commentId, endSubStringCount, startSubstringCount, userId)) {
			return ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
				@Override
				public DecisionKnowledgeInCommentEntity doInTransaction() {
					DecisionKnowledgeInCommentEntity todo = ao.create(DecisionKnowledgeInCommentEntity.class); // (2)
					todo.setCommentId(commentId);
					todo.setEndSubstringCount(endSubStringCount);
					todo.setStartSubstringCount(startSubstringCount);
					todo.setUserId(userId);
					todo.save();
					return todo;
				}
			}).getId();
		} else {
			return getElementFromAO(commentId, endSubStringCount, startSubstringCount, userId).getId();
		}
	}

	public static boolean checkElementExistingInAO(long commentId, int endSubtringCount, int startSubstringCount,
			long userId) {
		init();
		DecisionKnowledgeInCommentEntity databaseEntry = getElementFromAO(commentId, endSubtringCount,
				startSubstringCount, userId);
		if (databaseEntry != null) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkCommentExistingInAO(long commentId,boolean getIsTagged) {
		init();
		DecisionKnowledgeInCommentEntity dbEntry =  ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getId() == commentId) {
						return databaseEntry;
					}
				}
				return null;
			}
		});
		if(getIsTagged && dbEntry != null) {
			return dbEntry.getIsRelevant();
		}
		return (dbEntry != null);
	}

	public static DecisionKnowledgeInCommentEntity getElementFromAO(long commentId, int endSubtringCount,
			int startSubstringCount, long userId) {
		init();
		return ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class)) {
					if (equalsDatabase(databaseEntry, commentId, endSubtringCount, startSubstringCount, userId)) {
						return databaseEntry;
					}
				}
				return null;
			}
		});

	}

	private static boolean equalsDatabase(DecisionKnowledgeInCommentEntity databaseEntry, long commentId,
			int endSubtringCount, int startSubstringCount, long userId) {
		if (databaseEntry.getCommentId() == commentId && databaseEntry.getEndSubstringCount() == endSubtringCount
				&& databaseEntry.getStartSubstringCount() == startSubstringCount
				&& databaseEntry.getUserId() == userId) {
			return true;
		}
		return false;
	}

}
