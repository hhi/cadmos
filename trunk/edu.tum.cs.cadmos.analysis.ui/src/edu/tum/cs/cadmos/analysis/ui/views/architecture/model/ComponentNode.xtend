package edu.tum.cs.cadmos.analysis.ui.views.architecture.model

import org.eclipse.swt.graphics.Point
import edu.tum.cs.cadmos.language.cadmos.Component

class ComponentNode extends Node{
	
	Component component
	
	new(Component component) {
		this.component = component
	}
	
	def addChildren(Iterable<Node> nodes){
		subnodes.addAll(nodes)
		println(nodes)
	}
	
	override getSize() {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override drawChildren(Point origin) {
		throw new UnsupportedOperationException("TODO: auto-generated method stub")
	}
	
	override toString() {
		"Component: "+component.name+"\n "+super.toString
		
	}
	
}