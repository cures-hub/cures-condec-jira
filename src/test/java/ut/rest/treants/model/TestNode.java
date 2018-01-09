package ut.rest.treants.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.DecisionDocumentation.rest.treants.model.Node;

public class TestNode {
	
	private Map<String, String> nodeContent;
	private Map<String, String> link;
	private String htmlClass;
	private long htmlId;
	private String innerHTML;
	private List<Node> children;
	
	private Node node;
	
	@Before 
	public void setUp() {
		nodeContent = new HashMap<>();
		link = new HashMap<>();
		htmlClass= "Test";
		htmlId=(long)100;
		innerHTML= "Test";
		children = new ArrayList<>();
		
		node = new Node();
		node.setChildren(children);
		node.setHtmlClass(htmlClass);
		node.setHtmlId(htmlId);
		node.setInnerHTML(innerHTML);
		node.setLink(link);
		node.setNodeContent(nodeContent);
	}
	
	@Test
	public void testGetNodeContent() {
		assertEquals(nodeContent, this.node.getNodeContent());
	}
	
	@Test
	public void testGetLink() {
		assertEquals(link, this.node.getLink());
	}
	
	@Test
	public void testGetHtmlClass() {
		assertEquals(htmlClass, this.node.getHtmlClass());
	}
	
	@Test
	public void testGetHtmlId() {
		assertEquals(htmlId, this.node.getHtmlId(),0.0);
	}
	
	@Test
	public void testGetInnerHtml() {
		assertEquals(innerHTML, this.node.getInnerHTML());
	}
	
	@Test
	public void testGetChildren() {
		assertEquals(children, this.node.getChildren());
	}
	
	@Test
	public void testSetNodeContent() {
		Map<String, String>newNodeContent = new HashMap<>();
		this.node.setNodeContent(newNodeContent);
		assertEquals(newNodeContent, this.node.getNodeContent());
	}
	
	@Test
	public void testSetLink() {
		Map<String, String> newLink = new HashMap<>();
		this.node.setLink(newLink);
		assertEquals(newLink, this.node.getLink());
	}
	
	@Test
	public void testSetHtmlClass() {
		String newhtmlClass= htmlClass+"New";
		this.node.setHtmlClass(newhtmlClass);
		assertEquals(newhtmlClass, this.node.getHtmlClass());
	}
	
	@Test
	public void testSetHtmlId() {
		long newHtmlId = htmlId+1;
		this.node.setHtmlId(newHtmlId);
		assertEquals(newHtmlId, this.node.getHtmlId(),0.0);
	}
	
	@Test
	public void testSetInnerHtml() {
		String newInnerHtml = innerHTML+"New";
		this.node.setInnerHTML(newInnerHtml);
		assertEquals(newInnerHtml, this.node.getInnerHTML());
	}
	
	@Test
	public void testSetChildren() {
		List<Node> newchildren= new ArrayList<Node>();
		this.node.setChildren(newchildren);
		assertEquals(newchildren, this.node.getChildren());
	}
}
