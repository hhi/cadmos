package edu.tum.cs.cadmos.analysis.ui.views.architecture.model

import org.eclipse.swt.graphics.Point
import edu.tum.cs.cadmos.language.cadmos.Component

class SequentialNode extends Node{
	
	
	
	
	
	new(Node node, Node successor) {
		subnodes.add(node)
		subnodes.add(successor)
	}
	
	override getSize() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override drawChildren(Point origin) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
}