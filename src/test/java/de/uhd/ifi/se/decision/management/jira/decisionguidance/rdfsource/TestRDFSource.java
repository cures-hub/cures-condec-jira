package de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSource;

public class TestRDFSource extends TestSetUp {

	private RDFSource rdfSource;

	@Before
	public void setUp() {
		init();
		rdfSource = new RDFSource();
	}

	@Test
	public void testRDFSourceConstructor() {
		assertNotNull(rdfSource);
		rdfSource.setService(RDFSource.DEFAULT_SERVICE);
		rdfSource.setQuery(RDFSource.DEFAULT_QUERY);
		rdfSource.setTimeout(30000);

		assertEquals(RDFSource.DEFAULT_SERVICE, rdfSource.getService());
		assertEquals(RDFSource.DEFAULT_NAME, rdfSource.getName());
		assertEquals(RDFSource.DEFAULT_QUERY, rdfSource.getQuery());
		assertEquals(30000, rdfSource.getTimeout());
		assertEquals("aui-iconfont-download", rdfSource.getIcon());
	}

	@Test
	public void testRDFSourceActivated() {
		assertEquals(true, rdfSource.isActivated());
	}

	@Test
	public void testRDFSourceDeactivated() {
		rdfSource.setActivated(false);
		assertEquals(false, rdfSource.isActivated());
	}

	@Test
	public void testConstructor() {
		RDFSource rdfSource = new RDFSource();
		assertEquals(RDFSource.DEFAULT_SERVICE, rdfSource.getService());
		assertEquals(RDFSource.DEFAULT_QUERY, rdfSource.getQuery());
		assertEquals(RDFSource.DEFAULT_NAME, rdfSource.getName());
		assertEquals(30000, rdfSource.getTimeout());
	}

	@Test
	public void testHashCode() {
		assertEquals(Objects.hash(RDFSource.DEFAULT_NAME), rdfSource.hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		RDFSource rdfSourceother = null;
		assertFalse(rdfSource.equals(rdfSourceother));

		assertFalse(rdfSource.equals(new ProjectSource("TEST")));

		rdfSourceother = new RDFSource();
		assertTrue(rdfSourceother.equals(rdfSource));
		assertTrue(rdfSourceother.equals(rdfSourceother));
	}

	@Test
	public void testToString() {
		RDFSource source = new RDFSource();
		source.setName("One Two Three");
		assertEquals("One-Two-Three", source.toString());
	}

	@Test
	public void testConstraints() {
		RDFSource source = new RDFSource();
		source.setConstraints("License=dbo:license");
		assertTrue(source.getConstraintMap().containsKey("License"));
		assertTrue(source.getConstraintMap().containsValue("dbo:license"));
	}

}
