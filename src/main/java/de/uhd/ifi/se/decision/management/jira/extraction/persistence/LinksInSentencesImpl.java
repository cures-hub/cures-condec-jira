package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import java.beans.PropertyChangeListener;

import javax.xml.bind.annotation.XmlElement;

import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class LinksInSentencesImpl implements LinksInSentencesEntity {
	
	private long id;
	
	private long idOfDestinationElement;
	
	private long idOfSourceElement;
	
	private String type;

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
	public void save() {
		// TODO Auto-generated method stub
		
	}

	@Override
	@XmlElement(name = "id")
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;		
	}

	@Override
	@XmlElement(name = "idOfSourceElement")
	public long getIdOfSourceElement() {
		return idOfSourceElement;
	}

	@Override
	public void setIdOfSourceElement(long id) {
		this.idOfSourceElement = id;
		
	}

	@Override
	@XmlElement(name = "idOfDestinationElement")
	public long getIdOfDestinationElement() {
		return idOfDestinationElement;
	}

	@Override
	public void setIdOfDestinationElement(long id) {
		this.idOfDestinationElement = id;
	}

	@Override
	@XmlElement(name = "type")
	public String getType() {
		return this.type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}
	
	

}
