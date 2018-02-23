package ut.rest.treeviewer.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

<<<<<<< Updated upstream:src/test/java/ut/rest/treeviewer/model/TestData.java
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.Data;
import com.atlassian.DecisionDocumentation.rest.treeviewer.model.NodeInfo;
=======
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.Data;
import de.uhd.ifi.se.decision.documentation.jira.view.treeviewer.NodeInfo;
>>>>>>> Stashed changes:src/test/java/ut/de/uhd/ifi/se/decision/documentation/jira/rest/treeviewer/model/TestData.java

public class TestData {
	private String id;
	private String text;
	private List<Data> children;
	private NodeInfo nodeInfo;
	
	private Data data;
	
	
	@Before
	public void setUp() {
		id = "Test";
		text= "Test";
		children= new ArrayList<>();
		nodeInfo = new NodeInfo();
		data = new Data();
		data.setChildren(children);
		data.setId(id);
		data.setNodeInfo(nodeInfo);
		data.setText(text);
	}
	
	@Test
	public void testGetId() {
		assertEquals(data.getId(), id);
	}
	
	@Test 
	public void testGetText() {
		assertEquals(data.getText(), text);
	}
	
	@Test
	public void testGetChildren() {
		assertEquals(data.getChildren(), children);
	}
	
	@Test
	public void testGetNodeInfo() {
		assertEquals(data.getNodeInfo(), nodeInfo);
	}
	
	@Test
	public void testSetId() {
		data.setId(id+"New");
		assertEquals(data.getId(), id+"New");
	}
	
	@Test
	public void testSetText() {
		data.setText(text+"New");
		assertEquals(data.getText(), text+"New");
	}
	
	@Test
	public void testSetChildren() {
		List<Data> newChilden  = new ArrayList<>();
		data.setChildren(newChilden);
		assertEquals(newChilden, data.getChildren());
	}
	
	@Test
	public void testSetNodeInfo() {
		NodeInfo newInfo = new NodeInfo();
		data.setNodeInfo(newInfo);
		assertEquals(newInfo, data.getNodeInfo());
	}

}
