package edu.tum.cs.cadmos.analysis.ui.views.architecture.model;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KGraphFactory;
import de.cau.cs.kieler.core.kgraph.KLabel;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.kgraph.KPort;

public class Test {
	public static void test(){
	KGraphFactory factory = KGraphFactory.eINSTANCE;
	KNode myGraph = factory.createKNode();
	 
	// Setting the parent reference automatically adds a node to
	// the parent's list of children.
	KNode node1 = factory.createKNode();
	node1.setParent(myGraph);
	KNode node2 = factory.createKNode(); 
	node2.setParent(myGraph);
	 
	// Setting the source and target references automatically adds
	// an edge to the corresponding incoming and outgoing edges lists.
	// In the XML structure edges are contained by their source node.
	KEdge edge = factory.createKEdge();
	edge.setSource(node1);
	edge.setTarget(node2);
	 
	// Setting the parent reference automatically adds a label to
	// the parent's list of labels.
	KLabel label = factory.createKLabel();
	label.setText("Hello, World!");
	label.setParent(edge);
	 
	// Setting the node reference automatically adds a port to the
	// node's list of ports.
	KPort port1 = factory.createKPort();
	port1.setNode(node1);
	KPort port2 = factory.createKPort();
	port2.setNode(node2);
	 
	// The 'edges' and 'sourcePort' / 'targetPort' references are not
	// opposite, so they all have to be set explicitly.
	edge.setSourcePort(port1);
	port1.getEdges().add(edge);
	edge.setTargetPort(port2);
	port2.getEdges().add(edge);
	
	
	}
}
