package ut.DecisionDocumentation.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.util.Pair;

import net.sf.hibernate.mapping.Array;

public class TestPair {
	private Pair<String, String> pair;
	
	@Before
	public void setUp() {
		pair = new Pair<String, String>("TestL", "TestR");
	}
	
	@Test
	public void testGetLeft() {
		assertEquals("TestL", pair.getLeft());	
	}
	
	@Test
	public void testGetRight() {
		assertEquals("TestR", pair.getRight());
	}
	
	@Test
	public void testHasCode() {
		String l = "TestL";
		String r = "TestR";
		int hashCode = l.hashCode()^ r.hashCode();
		assertEquals(hashCode, pair.hashCode());
	}
	
	@Test
	public void testEqualsNotPair() {
		Object o = new ArrayList<>();
		assertFalse(pair.equals(o));
	}
	
	@Test
	public void testEqualsObjectPairStringsNotEqual() {
		assertFalse(pair.equals(new Pair<String, String>("left", "right")));
	}
	
	@Test
	public void testEqualsObjectPairStringsEqual() {
		assertTrue(pair.equals(new Pair<String, String>("TestL", "TestR")));
	}
}
