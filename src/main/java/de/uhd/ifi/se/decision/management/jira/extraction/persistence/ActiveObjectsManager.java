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
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import net.java.ao.Query;

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
					todo.setIsTagged(false);
					todo.setIsTaggedFineGrained(false);
					todo.setIsTaggedManually(false);
					todo.save();
					System.out.println("Created:\t" + todo.getId());
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

	public static boolean checkCommentExistingInAO(long sentenceAoId, boolean getIsTagged) {
		init();
		DecisionKnowledgeInCommentEntity dbEntry = ao
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ao
								.find(DecisionKnowledgeInCommentEntity.class)) {
							if (databaseEntry.getId() == sentenceAoId) {
								return databaseEntry;
							}
						}
						return null;
					}
				});
		if (getIsTagged && dbEntry != null) {
			return dbEntry.getIsTagged();
		}
		return (dbEntry != null);
	}

	public static DecisionKnowledgeInCommentEntity getElementFromAO(long commentId, int endSubtringCount,
			int startSubstringCount, long userId) {
		init();
		DecisionKnowledgeInCommentEntity element = ao
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						for (DecisionKnowledgeInCommentEntity databaseEntry : ao
								.find(DecisionKnowledgeInCommentEntity.class)) {
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

	public static void setSentenceKnowledgeType(Sentence sentence) {
		init();
		ao.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
			@Override
			public DecisionKnowledgeInCommentEntity doInTransaction() {
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getId() == sentence.getActiveObjectId()) {
						databaseEntry.setKnowledgeType(sentence.getKnowledgeType().toString());
						databaseEntry.setIsTaggedFineGrained(true);
						databaseEntry.setArgument(sentence.getArgument());
						databaseEntry.save();
						return databaseEntry;
					}
				}
				return null;
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
								databaseEntry.setIsRelevant(isRelevant);
								// If relevant is true or false, it's tagged, so set it on true
								databaseEntry.setIsTagged(true);
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
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getCommentId() == comment.getJiraCommentId()) {
						if (!starts.contains(databaseEntry.getStartSubstringCount())
								|| !ends.contains(databaseEntry.getEndSubstringCount())) {
							deleteFlag = true;
						}
					}
				}
				// delete all here
				for (DecisionKnowledgeInCommentEntity databaseEntry : ao.find(DecisionKnowledgeInCommentEntity.class)) {
					if (databaseEntry.getCommentId() == comment.getJiraCommentId() && deleteFlag) {
						try {
							databaseEntry.getEntityManager().delete(databaseEntry);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				return null;
			}
		});
	}

	public static long insertLink(Link link, ApplicationUser user) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction() {
				for (LinksInSentencesEntity linkEntity : ao.find(LinksInSentencesEntity.class)) {
					if (linkEntity.getIdOfSourceElement() == link.getSourceElement().getId()
							&& linkEntity.getIdOfDestinationElement() == link.getDestinationElement().getId()) {
						return linkEntity.getId();
					}
				}

				DecisionKnowledgeInCommentEntity sourceElement = null;
				DecisionKnowledgeInCommentEntity[] sourceElements = ao.find(DecisionKnowledgeInCommentEntity.class,
						Query.select().where("ID = ?", link.getSourceElement().getId()));
				if (sourceElements.length == 1) {
					sourceElement = sourceElements[0];
				}

				DecisionKnowledgeInCommentEntity destinationElement = null;
				DecisionKnowledgeInCommentEntity[] destinationElements = ao.find(DecisionKnowledgeInCommentEntity.class,
						Query.select().where("ID = ?", link.getDestinationElement().getId()));
				if (destinationElements.length == 1) {
					destinationElement = destinationElements[0];
				}
				if (sourceElement == null || destinationElement == null) {
					return (long) 0;
				}

				// elements exist
				final LinksInSentencesEntity linkEntity = ao.create(LinksInSentencesEntity.class);
				linkEntity.setIdOfSourceElement(link.getSourceElement().getId());
				linkEntity.setIdOfDestinationElement(link.getDestinationElement().getId());
				linkEntity.setType(link.getType());
				linkEntity.save();
				return linkEntity.getId();
			}
		});
	}

	public static List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		init();
		List<Link> inwardLinks = new ArrayList<>();
		LinksInSentencesEntity[] links = ao.find(LinksInSentencesEntity.class,
				Query.select().where("ID_OF_DESTINATION_ELEMENT = ?", element.getId()));
		for (LinksInSentencesEntity link : links) {
			Link inwardLink = new LinkImpl(link);
			inwardLink.setDestinationElement(element);
			inwardLink.setSourceElement(getDecisionKnowledgeElement(link.getIdOfSourceElement()));
			inwardLinks.add(inwardLink);
		}
		return inwardLinks;
	}

	public static List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		init();
		List<Link> outwardLinks = new ArrayList<>();
		LinksInSentencesEntity[] links = ao.find(LinksInSentencesEntity.class,
				Query.select().where("ID_OF_SOURCE_ELEMENT = ?", element.getId()));
		for (LinksInSentencesEntity link : links) {
			Link outwardLink = new LinkImpl(link);
			outwardLink.setSourceElement(element);
			outwardLink.setDestinationElement(getDecisionKnowledgeElement(link.getIdOfDestinationElement()));

			outwardLinks.add(outwardLink);
		}
		return outwardLinks;
	}

	private static DecisionKnowledgeElementImpl getDecisionKnowledgeElement(long id) {
		init();
		DecisionKnowledgeInCommentEntity decisionKnowledgeElement = ao
				.executeInTransaction(new TransactionCallback<DecisionKnowledgeInCommentEntity>() {
					@Override
					public DecisionKnowledgeInCommentEntity doInTransaction() {
						DecisionKnowledgeInCommentEntity[] decisionKnowledgeElement = ao
								.find(DecisionKnowledgeInCommentEntity.class, Query.select().where("ID = ?", id));
						// 0 or 1 decision knowledge elements might be returned by this query
						if (decisionKnowledgeElement.length == 1) {
							return decisionKnowledgeElement[0];
						}
						return null;
					}
				});
		if (decisionKnowledgeElement != null) {
			Sentence sentence = new Sentence(id);
			// sentence.setType(KnowledgeType.OTHER);
			// if (sentence.getKnowledgeTypeEquivalent() != null) {
			// sentence.setType(sentence.getKnowledgeTypeEquivalent());
			// }
			return sentence;
		}
		return null;
	}

	public static boolean deleteLinkBetweenSentences(Link link) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (LinksInSentencesEntity linkEntity : ao.find(LinksInSentencesEntity.class)) {
					if (link.getDestinationElement().getId() == linkEntity.getIdOfDestinationElement()
							&& link.getSourceElement().getId() == linkEntity.getIdOfSourceElement()) {
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

	public static Boolean updateKnowledgeTypeOfSentence(long id, KnowledgeType knowledgeType) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ao
						.find(DecisionKnowledgeInCommentEntity.class)) {
					if (sentenceEntity.getId() == id) {
						sentenceEntity.setKnowledgeType(knowledgeType.toString());
						if (knowledgeType != KnowledgeType.OTHER) {
							sentenceEntity.setIsRelevant(true);
						}
						sentenceEntity.save();
						return true;
					}
				}
				return false;
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
						sentenceEntity.setIsRelevant(false);
						sentenceEntity.setIsTaggedManually(isTaggedManually);
						sentenceEntity.setKnowledgeType(KnowledgeType.OTHER.toString());
						sentenceEntity.save();
						return true;
					}
				}
				return false;
			}
		});
	}

	public static boolean updateSentenceBody(long commentId, long aoId, String description) {
		init();
		return ao.executeInTransaction(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction() {
				int lengthDifference = 0;
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ao.find(DecisionKnowledgeInCommentEntity.class,
						"ID = ?", aoId)) {
					int oldLength = sentenceEntity.getEndSubstringCount() - sentenceEntity.getStartSubstringCount();
					lengthDifference = (oldLength - description.length()) * -1;
					sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + lengthDifference);
					sentenceEntity.save();
				}
				for (DecisionKnowledgeInCommentEntity sentenceEntity : ao.find(DecisionKnowledgeInCommentEntity.class,
						"COMMENT_ID = ?", commentId)) {
					if (sentenceEntity.getStartSubstringCount() > sentenceEntity.getStartSubstringCount() && sentenceEntity.getId() != aoId
							&& sentenceEntity.getCommentId() == commentId) {
						sentenceEntity.setStartSubstringCount(sentenceEntity.getStartSubstringCount() + lengthDifference);
						sentenceEntity.setEndSubstringCount(sentenceEntity.getEndSubstringCount() + lengthDifference);
						sentenceEntity.save();
					}
				}
				return true;
			}
		});
	}

}
