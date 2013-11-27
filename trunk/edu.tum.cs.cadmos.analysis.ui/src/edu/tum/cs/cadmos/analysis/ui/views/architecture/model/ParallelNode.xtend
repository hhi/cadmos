package edu.tum.cs.cadmos.analysis.ui.views.architecture.model

import org.eclipse.swt.graphics.Point
import java.util.ArrayList
import edu.tum.cs.cadmos.language.cadmos.Component
import java.util.Set

class ParallelNode extends Node{
	
	
	new(Iterable<Node> neighbors) {
		subnodes.addAll(neighbors)
	}
	
	override getSize() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override drawChildren(Point origin) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
}