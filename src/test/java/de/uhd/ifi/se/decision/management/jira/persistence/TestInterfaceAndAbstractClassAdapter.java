package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonObject;

public class TestInterfaceAndAbstractClassAdapter {

	InterfaceAndAbstractClassAdapter interfaceAndAbstractClassAdapter;

	@Before
	public void setUp() {
		interfaceAndAbstractClassAdapter = new InterfaceAndAbstractClassAdapter();
	}

	@Test
	public void testGetObjectClass() {
		assertEquals(this.getClass(), interfaceAndAbstractClassAdapter.getObjectClass(this.getClass().getName()));
	}

	@Test(expected = Exception.class)
	public void testDeserializeNotWorking() {
		interfaceAndAbstractClassAdapter.deserialize(new JsonObject(), getClass(), null);
	}

	@Test(expected = Exception.class)
	public void testSerializeNotWorking() {
		interfaceAndAbstractClassAdapter.serialize(new JsonObject(), getClass(), null);
	}

}
