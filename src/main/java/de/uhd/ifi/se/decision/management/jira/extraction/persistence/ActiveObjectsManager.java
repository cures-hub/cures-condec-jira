package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLinkImpl;
import net.java.ao.Query;

public class ActiveObjectsManager {

	private static ActiveObjects ao;

	public static void init() {
		if (ao == null) {
			ao = ComponentGetter.getActiveObjects();
		}
	}

	public static long addNewSentenceintoAo(Comment comment, long issueId, int index) {
		return addNewSentenceintoAo(comment.getJiraCommentId(), comment.getEndSubstringCount().get(index),
				comment.getStartSubstringCount().get(index), comment.getAuthorId(), issueId, comment.getProjectKey());
	}

	public static long addNewSentenceintoAo(long commentId, int endSubStringCount, int startSubstringCount, long userId,
			long issueId, String projectKey) {
		init();
		if (checkElementExistingInAO(commentId, endSubStringCount, startSubstringCount, userId, projectKey)) {
			return getElementFromAO(commentId, endSubStringCount, startSubstringCount, userId, projectKey).getId();
		}
		DecisionKnowledgeInCommentEntity newElement = ao
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						DecisionKnowledgeInCommentEntity todo = ao.create(DecisionKnowledgeInCommentEntity.class); // (2)
						todo.setCommentId(commentId);
						todo.setEndSubstringCount(endSubStringCount);
						todo.setStartSubstringCount(startSubstringCount);
						todo.setUserId(userId);
						todo.setTagged(false);
						todo.setTaggedFineGrained(false);
						todo.setTaggedManually(false);
						todo.setProjectKey(projectKey);
						todo.setIssueId(issueId);
						todo.save();
						return todo;
					}
				});
		addNewLinkSentenceIssue(issueId, newElement.getId());
		return newElement.getId();
	}

	private static void addNewLinkSentenceIssue(long issueId, long sentenceAoId) {
		ao.executeInTransaction(new TransactionCallback<LinkBetweenDifferentEntitiesEntity>() {
			@Override
			public LinkBetweenDifferentEntitiesEntity doInTransaction() {
				LinkBetweenDifferentEntitiesEntity newGenericLink = ao.create(LinkBetweenDifferentEntitiesEntity.class); // (2)
				newGenericLink.setIdOfDestinationElement("s" + sentenceAoId);
				newGenericLink.setIdOfSourceElement("i" + issueId);
				newGenericLink.setType("contain");
				newGenericLink.save();
				return newGenericLink;
			}
		});
	}

	public static boolean checkElementExistingInAO(long commentId, int endSubtringCount, int startSubstringCount,
			long userId, String projectKey) {
		init();
		DecisionKnowledgeInCommentEntity databaseEntry = getElementFromAO(commentId, endSubtringCount,
				startSubstringCount, userId, projectKey);
		if (databaseEntry != null) {
			return true;
		} else {
			return false;
		}
	}

	public static void updateSentenceElement(Sentence sentence) {
		init();
		ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class,
						Query.select().where("PROJECT_KEY = ?", sentence.getProjectKey()))) {
					if (databaseEntry.getId() == sentence.getId()) {
						databaseEntry.setArgument(sentence.getArgument());
						databaseEntry.setEndSubstringCount(sentence.getEndSubstringCount());
						databaseEntry.setRelevant(sentence.isRelevant());
						databaseEntry.setTagged(sentence.isTagged());
						databaseEntry.setTaggedFineGrained(sentence.isTaggedFineGrained());
						databaseEntry.setTaggedManually(sentence.isTaggedManually());
						databaseEntry.setKnowledgeTypeString(sentence.getKnowledgeTypeString());
						databaseEntry.setStartSubstringCount(sentence.getStartSubstringCount());
						databaseEntry.save();
					}
				}
				return null;
			}
		});
	}

	public static DecisionKnowledgeInCommentEntity getElementFromAO(long commentId, int endSubtringCount,
			int startSubstringCount, long userId, String projectKey) {
		init();
		DecisionKnowledgeInCommentEntity element = ao
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(
								DecisionKnowledgeInCommentEntity.class,
								Query.select().where("PROJECT_KEY = ?", projectKey))) {
							if (equalsDatabase(databaseEntry, commentId, endSubtringCount, startSubstringCount,
									userId)) {
								return databaseEntry;
							}
						}
						return null;
					}
				});
		return element;

	}

	public static DecisionKnowledgeInCommentEntity getElementFromAO(long aoId) {
		init();
		return ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getId() == aoId) {
						return databaseEntry;
					}
				}
				return new SentenceImpl();
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

	public static void setSentenceKnowledgeType(Sentence sentence) {
		init();
		ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getId() == sentence.getId()) {
						databaseEntry.setKnowledgeTypeString(sentence.getKnowledgeTypeString());
						databaseEntry.setTaggedFineGrained(true);
						databaseEntry.setArgument(sentence.getArgument());
						databaseEntry.save();
						return databaseEntry;
					}
				}
				return null;
			}
		});

	}

	public static Boolean updateKnowledgeTypeOfSentence(long id, KnowledgeType knowledgeType) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ao
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (sentenceEntity.getId() == id) {
						sentenceEntity.setKnowledgeTypeString(knowledgeType.toString());
						if (knowledgeType != KnowledgeType.OTHER) {
							sentenceEntity.setRelevant(true);
							sentenceEntity.setTaggedManually(true);
							sentenceEntity.setTaggedFineGrained(true);
						}
						if (!sentenceEntity.getKnowledgeTypeString().equals("Pro")
								&& !sentenceEntity.getKnowledgeTypeString().equals("Con")) {
							sentenceEntity.setArgument("");
						}
						sentenceEntity.save();
						return true;
					}
				}
				return false;
			}
		});

	}

	public static boolean setIsRelevantIntoAo(long activeObjectId, boolean isRelevant) {
		init();
		DecisionKnowledgeInCommentEntity databaseEntry = ao
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ao
								.find(DecisionKnowledgeInCommentEntity.class)) {
							if (databaseEntry.getId() == activeObjectId) {
								databaseEntry.setRelevant(isRelevant);
								// If relevant is true or false, it's tagged, so set it on true
								databaseEntry.setTagged(true);
								databaseEntry.save();
								return databaseEntry;
							}
						}
						return null;
					}
				});
		if (databaseEntry == null) {
			return false;
		}
		return true;
	}

	public static void checkIfCommentBodyHasChangedOutsideOfPlugin(Comment comment) {
		init();
		final List<Integer> starts = comment.getStartSubstringCount();
		final List<Integer> ends = comment.getEndSubstringCount();

		ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				boolean deleteFlag = false;
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class,
						Query.select().where("COMMENT_ID = ?", comment.getJiraCommentId()))) {
					if (databaseEntry.getProjectKey().equals(comment.getProjectKey())
							&& (!starts.contains(databaseEntry.getStartSubstringCount())
									|| !ends.contains(databaseEntry.getEndSubstringCount()))) {
						deleteFlag = true;
					}
				}
				// delete all here
				if (deleteFlag) {
					for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(
							DecisionKnowledgeInCommentEntity.class,
							Query.select().where("COMMENT_ID = ?", comment.getJiraCommentId()))) {
						try {
							if (databaseEntry.getProjectKey().equals(comment.getProjectKey())) {
								databaseEntry.getEntityManager().delete(databaseEntry);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						for (LinkBetweenDifferentEntitiesEntity link : ao
								.find(LinkBetweenDifferentEntitiesEntity.class)) {
							if (link.getIdOfDestinationElement().equals("i" + comment.getIssueId())
									|| link.getIdOfSourceElement().equals("i" + comment.getIssueId())) {
								try {
									link.getEntityManager().delete(link);
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				return null;
			}
		});
	}

	public static boolean deleteGenericLink(GenericLink link) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (LinkBetweenDifferentEntitiesEntity linkEntity : ao
						.find(LinkBetweenDifferentEntitiesEntity.class)) {
					if (link.getIdOfDestinationElement().equals(linkEntity.getIdOfDestinationElement())
							&& link.getIdOfSourceElement().equals(linkEntity.getIdOfSourceElement())) {
						try {
							linkEntity.getEntityManager().delete(linkEntity);
							return true;
						} catch (SQLException e) {
							return false;
						}
					}
				}
				return false;
			}
		});
	}

	public static long insertGenericLink(GenericLink link, ApplicationUser user) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				for (LinkBetweenDifferentEntitiesEntity linkEntity : ao
						.find(LinkBetweenDifferentEntitiesEntity.class)) {
					if (linkEntity.getIdOfSourceElement() == link.getIdOfSourceElement()
							&& linkEntity.getIdOfDestinationElement() == link.getIdOfDestinationElement()
							|| linkEntity.getIdOfDestinationElement() == link.getIdOfSourceElement()// Check inverse
																									// link
									&& linkEntity.getIdOfSourceElement() == link.getIdOfDestinationElement()) {
						return linkEntity.getId();
					}
				}

				final LinkBetweenDifferentEntitiesEntity genericLink = ao
						.create(LinkBetweenDifferentEntitiesEntity.class);
				genericLink.setIdOfSourceElement(link.getIdOfSourceElement());
				genericLink.setIdOfDestinationElement(link.getIdOfDestinationElement());
				genericLink.setType(link.getType());
				genericLink.save();

				return genericLink.getId();
			}
		});
	}

	public static boolean setSentenceIrrelevant(long id, boolean isTaggedManually) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ao
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (sentenceEntity.getId() == id) {
						sentenceEntity.setRelevant(false);
						sentenceEntity.setTaggedManually(isTaggedManually);
						sentenceEntity.setKnowledgeTypeString(KnowledgeType.OTHER.toString());
						sentenceEntity.setArgument("");
						sentenceEntity.save();
						return true;
					}
				}
				return false;
			}
		});
	}

	public static boolean updateSentenceBodyWhenCommentChanged(long commentId, long aoId, String description) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				int lengthDifference = 0; // TODO: Add project ID
				int oldStart = 0;
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ao.find(DecisionKnowledgeInCommentEntity.class,
						"ID = ?", aoId)) {
					int oldLength = sentenceEntity.getEndSubstringCount() - sentenceEntity.getStartSubstringCount();
					lengthDifference = (oldLength - description.length()) * -1;
					sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + lengthDifference);
					sentenceEntity.save();
					oldStart = sentenceEntity.getStartSubstringCount();
				}
				for (DecisionKnowledgeInCommentEntity otherSentenceInComment : ao
						.find(DecisionKnowledgeInCommentEntity.class, "COMMENT_ID = ?", commentId)) {
					if (otherSentenceInComment.getStartSubstringCount() > oldStart
							&& otherSentenceInComment.getId() != aoId
							&& otherSentenceInComment.getCommentId() == commentId) {
						otherSentenceInComment.setStartSubstringCount(
								otherSentenceInComment.getStartSubstringCount() + lengthDifference);
						otherSentenceInComment
								.setEndSubstringCount(otherSentenceInComment.getEndSubstringCount() + lengthDifference);
						otherSentenceInComment.save();
					}
				}
				return true;
			}
		});
	}

	public static List<GenericLink> getGenericLinksForElement(String targetId, boolean getOnlyOutwardLink) {
		init();
		List<GenericLink> links = new ArrayList<GenericLink>();
		ao.executeInTransaction(new TransactionCallback<LinkBetweenDifferentEntitiesEntity>() {
			@Override
			public LinkBetweenDifferentEntitiesEntity doInTransaction() {
				LinkBetweenDifferentEntitiesEntity[] linkElements = ao.find(LinkBetweenDifferentEntitiesEntity.class);
				for (LinkBetweenDifferentEntitiesEntity linkElement : linkElements) {
					if (linkElement.getIdOfDestinationElement().equals(targetId)) {
						links.add(new GenericLinkImpl(targetId, linkElement.getIdOfSourceElement()));
					}
					if (!getOnlyOutwardLink && linkElement.getIdOfSourceElement().equals(targetId)) {
						links.add(new GenericLinkImpl(targetId, linkElement.getIdOfDestinationElement()));
					}
				}
				return null;
			}
		});
		return links;
	}

	public static void clearSentenceDatabaseForProject(String projectKey) {
		init();
		ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class,
						Query.select().where("PROJECT_KEY = ?", projectKey))) {
					deleteLinksIfExisting("s" + databaseEntry.getId());
					try {
						databaseEntry.getEntityManager().delete(databaseEntry);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		});

	}

	private static void deleteLinksIfExisting(String string) {
		init();
		ao.executeInTransaction(new TransactionCallback<LinkBetweenDifferentEntitiesEntity>() {
			@Override
			public LinkBetweenDifferentEntitiesEntity doInTransaction() {
				LinkBetweenDifferentEntitiesEntity[] linkElements = ao.find(LinkBetweenDifferentEntitiesEntity.class);
				for (LinkBetweenDifferentEntitiesEntity linkElement : linkElements) {
					if (linkElement.getIdOfDestinationElement().equals(string)
							|| linkElement.getIdOfSourceElement().equals(string)) {
						try {
							linkElement.getEntityManager().delete(linkElement);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}

				}
				return null;
			}
		});

	}

}
