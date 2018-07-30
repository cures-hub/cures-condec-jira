package de.uhd.ifi.se.decision.management.jira.extraction.persistance;

import java.beans.PropertyChangeListener;

import javax.xml.bind.annotation.XmlElement;

import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class DecisionKnowledgeInCommentImpl implements DecisionKnowledgeInCommentEntity {

	private long id;

	private boolean isRelevant;

	private boolean isTagged;

	private long commentId;

	private long userId;

	private int startSubstringCount;

	private int endSubstringCount;



	public DecisionKnowledgeInCommentImpl() {

	}



	@Override
	@XmlElement(name = "id")
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id  =id;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X extends RawEntity<Integer>> Class<X> getEntityType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	@XmlElement(name = "isRelevant")
	public boolean getIsRelevant() {
		return isRelevant;
	}

	@Override
	public void setIsRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
		setIsTagged(true);
	}

	@Override
	@XmlElement(name = "commentId")
	public long getCommentId() {
		return commentId;
	}

	@Override
	public void setCommentId(long id) {
		this.commentId = id;
	}

	@Override
	@XmlElement(name = "startSubstringCount")
	public int getStartSubstringCount() {
		return startSubstringCount;
	}

	@Override
	public void setStartSubstringCount(int count) {
		this.startSubstringCount = count;
	}

	@Override
	@XmlElement(name = "endSubstringCount")
	public int getEndSubstringCount() {
		return endSubstringCount;
	}

	@Override
	public void setEndSubstringCount(int count) {
		this.endSubstringCount = count;
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	@XmlElement(name = "userId")
	public long getUserId() {
		return userId;
	}

	@Override
	public void setUserId(long id) {
		 this.userId = id;

	}

	@Override
	@XmlElement(name = "isTagged")
	public boolean getIsTagged() {
		return isTagged;
	}



	@Override
	public void setIsTagged(boolean isTagged) {
		this.isTagged = isTagged;

	}




}
