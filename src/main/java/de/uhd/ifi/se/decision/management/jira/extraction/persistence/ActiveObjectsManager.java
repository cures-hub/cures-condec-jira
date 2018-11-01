package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.LinkInDatabase;
import net.java.ao.Query;

public class ActiveObjectsManager {

	public static ActiveObjects ActiveObjects;

	private static int deleteSentenceCounter = 0;

	public static void init() {
		if (ActiveObjects == null) {
			ActiveObjects = ComponentGetter.getActiveObjects();
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
			Sentence existingElement = getElementFromAO(commentId, endSubStringCount, startSubstringCount, userId,
					projectKey);
			checkIfSentenceHasAValidLink(existingElement.getId(), issueId);
			return existingElement.getId();
		}
		DecisionKnowledgeInCommentEntity newElement = ActiveObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						DecisionKnowledgeInCommentEntity todo = ActiveObjects
								.create(DecisionKnowledgeInCommentEntity.class); // (2)
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
		addNewLinkBetweenSentenceAndIssue(issueId, newElement.getId());
		return newElement.getId();
	}

	private static void checkIfSentenceHasAValidLink(long sentenceId, long issueId) {
		List<Link> links = GenericLinkManager.getLinksForElement("s" + sentenceId);
		if (links == null || links.size() == 0) {
			addNewLinkBetweenSentenceAndIssue(issueId, sentenceId);
		}

	}

	private static void addNewLinkBetweenSentenceAndIssue(long issueId, long sentenceAoId) {
		ActiveObjects.executeInTransaction(new TransactionCallback<LinkInDatabase>() {
			@Override
			public LinkInDatabase doInTransaction() {
				LinkInDatabase newGenericLink = ActiveObjects.create(LinkInDatabase.class); // (2)
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
		return databaseEntry != null;
	}

	public static void updateSentenceElement(Sentence sentence) {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class,
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
		DecisionKnowledgeInCommentEntity element = ActiveObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
								DecisionKnowledgeInCommentEntity.class,
								Query.select().where(
										"PROJECT_KEY = ? AND COMMENT_ID = ? AND END_SUBSTRING_COUNT = ? AND START_SUBSTRING_COUNT = ?",
										projectKey, commentId, endSubtringCount, startSubstringCount))) {
							return databaseEntry;
						}
						return null;
					}
				});
		return element;

	}

	public static DecisionKnowledgeInCommentEntity getElementFromAO(long aoId) {
		init();
		return ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getId() == aoId) {
						return databaseEntry;
					}
				}
				return new SentenceImpl();
			}
		});
	}

	public static void setSentenceKnowledgeType(Sentence sentence) {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class)) {
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

	public static Boolean updateKnowledgeTypeOfSentence(long id, KnowledgeType knowledgeType, String argument) {
		init();
		return ActiveObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (sentenceEntity.getId() == id) {
						if (sentenceEntity.isTaggedManually()) {
							int oldTextLength = sentenceEntity.getEndSubstringCount()
									- sentenceEntity.getStartSubstringCount();
							int newTextLength = updateTagsInComment(sentenceEntity, knowledgeType, argument);
							sentenceEntity
									.setEndSubstringCount(sentenceEntity.getStartSubstringCount() + newTextLength);
							updateSentenceLengthForOtherSentencesInSameComment(sentenceEntity.getCommentId(),
									sentenceEntity.getStartSubstringCount(), newTextLength - oldTextLength,
									sentenceEntity.getId());
							sentenceEntity.save();

						}
						// Knowledgetype is an Argument
						if (knowledgeType.equals(KnowledgeType.OTHER) || knowledgeType.equals(KnowledgeType.ARGUMENT)) {
							sentenceEntity.setKnowledgeTypeString(argument);
							sentenceEntity.setArgument(argument);
						} else {
							sentenceEntity.setKnowledgeTypeString(knowledgeType.toString());
						}
						sentenceEntity.setRelevant(true);
						sentenceEntity.setTaggedManually(true);
						sentenceEntity.setTaggedFineGrained(true);
						if (!sentenceEntity.getKnowledgeTypeString().equals("Pro")
								&& !sentenceEntity.getKnowledgeTypeString().equals("Con")) {
							sentenceEntity.setArgument("");
							if (knowledgeType.equals(KnowledgeType.OTHER)) {
								sentenceEntity.setRelevant(false);
							}
						}
						sentenceEntity.save();
						return true;
					}
				}
				return false;
			}
		});

	}

	private static int updateTagsInComment(DecisionKnowledgeInCommentEntity sentenceEntity, KnowledgeType knowledgeType,
			String argument) {
		CommentManager cm = ComponentAccessor.getCommentManager();
		MutableComment mc = (MutableComment) cm.getMutableComment(sentenceEntity.getCommentId());
		String oldBody = mc.getBody();

		String newBody = oldBody.substring(sentenceEntity.getStartSubstringCount(),
				sentenceEntity.getEndSubstringCount());
		if (knowledgeType.toString().equalsIgnoreCase("other")
				|| knowledgeType.toString().equalsIgnoreCase("argument")) {
			newBody = newBody.replaceAll("(?i)" + sentenceEntity.getKnowledgeTypeString() + "]", argument + "]");
		} else {
			newBody = newBody.replaceAll("(?i)" + sentenceEntity.getKnowledgeTypeString() + "]",
					knowledgeType.toString() + "]");
		}
		// build body with first text and changed text
		int newEndSubstringCount = newBody.length();
		newBody = oldBody.substring(0, sentenceEntity.getStartSubstringCount()) + newBody;
		// If Changed sentence is in the middle of a sentence
		if (oldBody.length() > sentenceEntity.getEndSubstringCount()) {
			newBody = newBody + oldBody.substring(sentenceEntity.getEndSubstringCount());
		}
		mc.setBody(newBody);
		cm.update(mc, true);
		return newEndSubstringCount;
	}

	public static boolean setIsRelevantIntoAo(long activeObjectId, boolean isRelevant) {
		init();
		DecisionKnowledgeInCommentEntity databaseEntry = ActiveObjects
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects
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

		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				boolean deleteFlag = false;
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class,
						Query.select().where("COMMENT_ID = ?", comment.getJiraCommentId()))) {
					if (databaseEntry.getProjectKey().equals(comment.getProjectKey())
							&& (!starts.contains(databaseEntry.getStartSubstringCount())
									|| !ends.contains(databaseEntry.getEndSubstringCount()))) {
						deleteFlag = true;
					}
				}
				// delete all here
				if (deleteFlag) {
					for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
							DecisionKnowledgeInCommentEntity.class,
							Query.select().where("COMMENT_ID = ?", comment.getJiraCommentId()))) {
						try {
							if (databaseEntry.getProjectKey().equals(comment.getProjectKey())) {
								databaseEntry.getEntityManager().delete(databaseEntry);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						for (LinkInDatabase link : ActiveObjects.find(LinkInDatabase.class)) {
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

	public static boolean setSentenceIrrelevant(long id, boolean isTaggedManually) {
		init();
		return ActiveObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ActiveObjects
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
		return ActiveObjects.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				int lengthDifference = 0;
				int oldStart = 0;
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ActiveObjects
						.find(DecisionKnowledgeInCommentEntity.class, "ID = ?", aoId)) {
					int oldLength = sentenceEntity.getEndSubstringCount() - sentenceEntity.getStartSubstringCount();
					lengthDifference = (oldLength - description.length()) * -1;
					sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + lengthDifference);
					sentenceEntity.save();
					oldStart = sentenceEntity.getStartSubstringCount();
				}
				updateSentenceLengthForOtherSentencesInSameComment(commentId, oldStart, lengthDifference, aoId);
				return true;
			}
		});
	}

	private static void updateSentenceLengthForOtherSentencesInSameComment(long commentId, int oldStart,
			int lengthDifference, long aoId) {
		for (DecisionKnowledgeInCommentEntity otherSentenceInComment : ActiveObjects
				.find(DecisionKnowledgeInCommentEntity.class, "COMMENT_ID = ?", commentId)) {
			if (otherSentenceInComment.getStartSubstringCount() > oldStart && otherSentenceInComment.getId() != aoId
					&& otherSentenceInComment.getCommentId() == commentId) {
				otherSentenceInComment
						.setStartSubstringCount(otherSentenceInComment.getStartSubstringCount() + lengthDifference);
				otherSentenceInComment
						.setEndSubstringCount(otherSentenceInComment.getEndSubstringCount() + lengthDifference);
				otherSentenceInComment.save();
			}
		}
	}

	public static List<DecisionKnowledgeElement> getElementsForIssue(long issueId, String projectKey) {
		init();
		List<DecisionKnowledgeElement> elements = new ArrayList<>();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class,
						Query.select().where("PROJECT_KEY = ? AND ISSUE_ID = ?", projectKey, issueId))) {
					elements.add(databaseEntry);
				}
				return new SentenceImpl();
			}
		});
		return elements;
	}

	/**
	 * Deletes all sentences in ao tables for this project and all links to and from
	 * sentences. Currently not used. Useful for developing and system testing.
	 * 
	 * @param projectKey the project to clear
	 */
	@Deprecated
	public static void clearSentenceDatabaseForProject(String projectKey) {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class, Query.select().where("PROJECT_KEY = ?", projectKey))) {
					GenericLinkManager.deleteLinksForElement("s" + databaseEntry.getId());
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

	public static void cleanSentenceDatabaseForProject(String projectKey) {
		init();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class, Query.select().where("PROJECT_KEY = ?", projectKey))) {
					Sentence sentence;
					try {
						sentence = new SentenceImpl(databaseEntry); // Fast method, but may some values are null
					} catch (NullPointerException e) {
						sentence = new SentenceImpl(databaseEntry.getId());
					}
					boolean deleteFlag = false;
					try {// Check if comment is existing
						com.atlassian.jira.issue.comments.Comment c = ComponentAccessor.getCommentManager()
								.getCommentById(sentence.getCommentId());
						if (c.getBody().trim().length() < 1) {
							deleteFlag = true;
						}
					} catch (Exception e) {
						deleteFlag = true;
					}
					if (deleteFlag) {
						try {
							databaseEntry.getEntityManager().delete(databaseEntry);
						} catch (SQLException e1) {
						} // deletion failed.
					}
				}
				return null;
			}
		});
	}

	public static void createLinksForNonLinkedElements(String projectKey) {
		init();

		for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(DecisionKnowledgeInCommentEntity.class,
				Query.select().where("PROJECT_KEY = ?", projectKey))) {
			checkIfSentenceHasAValidLink(databaseEntry.getId(), databaseEntry.getIssueId());

		}
	}

	public static List<DecisionKnowledgeElement> getAllElementsFromAoByType(String projectKey,
			KnowledgeType rootElementType) {
		init();
		List<DecisionKnowledgeInCommentEntity> listOfDbEntries = new ArrayList<>();
		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class, Query.select().where("PROJECT_KEY = ?", projectKey))) {
					if (databaseEntry.getKnowledgeTypeString() != null
							&& (databaseEntry.getKnowledgeTypeString().equals(rootElementType.toString())
									|| (databaseEntry.getKnowledgeTypeString().length() == 3 // its either Pro or con
											&& rootElementType.equals(KnowledgeType.ARGUMENT)))) {
						try {
							listOfDbEntries.add(databaseEntry);
						} catch (NullPointerException e) {
							continue;
						}
					}
				}
				return null;
			}
		});
		List<DecisionKnowledgeElement> listOfDKE = new ArrayList<>();
		for (DecisionKnowledgeInCommentEntity dke : listOfDbEntries) {
			listOfDKE.add(new SentenceImpl(dke));
		}
		return listOfDKE;
	}

	/**
	 * Check sentence AO for duplicates. In some cases it can happen that sentences
	 * are inserted twice into the AO table. This functions checks all entry for one
	 * comment if there are duplicates. If one duplicate is found, its deleted.
	 *
	 * @param comment the comment
	 */
	public static void checkSentenceAOForDuplicates(Comment comment) {
		init();
		List<Integer> sentenceList = new ArrayList<>();
		List<Long> deleteList = new ArrayList<>();

		ActiveObjects.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
						DecisionKnowledgeInCommentEntity.class,
						Query.select().where("PROJECT_KEY = ? AND COMMENT_ID = ?", comment.getProjectKey(),
								comment.getJiraCommentId()))) {
					if (databaseEntry != null) {
						sentenceList.add(databaseEntry.getStartSubstringCount());
					}
				}
				if (sentenceList.size() != comment.getSentences().size()) {
					for (Sentence sentence : comment.getSentences()) {
						int occurrences = Collections.frequency(sentenceList, sentence.getStartSubstringCount());
						if (occurrences > 1) {
							deleteList.add(sentence.getId());
						}
					}
					for (long idToDelete : deleteList) {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ActiveObjects.find(
								DecisionKnowledgeInCommentEntity.class, Query.select().where("ID = ?", idToDelete))) {
							try {
								if (deleteSentenceCounter % 2 == 1) {
									databaseEntry.getEntityManager().delete(databaseEntry);
								}
								deleteSentenceCounter++;
							} catch (SQLException e) {// element not deleted
							}
						}
					}
				}
				return null;
			}
		});
	}

}
