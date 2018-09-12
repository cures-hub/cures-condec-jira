package de.uhd.ifi.se.decision.management.jira.extraction.persistence;

import java.beans.PropertyChangeListener;

import javax.xml.bind.annotation.XmlElement;

import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class LinkBetweenDifferentEntitiesImpl implements LinkBetweenDifferentEntitiesEntity{

	private long id;
	
	private String idOfDestinationElement;
	
	private String idOfSourceElement;
	
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
		return this.id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
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

	@Override
	@XmlElement(name = "idOfSourceElement")
	public String getIdOfSourceElement() {
		return this.idOfSourceElement;
	}

	@Override
	public void setIdOfSourceElement(String id) {
		this.idOfSourceElement = id;
		
	}

	@Override
	@XmlElement(name = "idOfDestinationElement")
	public String getIdOfDestinationElement() {
		return this.idOfDestinationElement;
	}

	@Override
	public void setIdOfDestinationElement(String id) {
		this.idOfDestinationElement = id;
		
	}

}
