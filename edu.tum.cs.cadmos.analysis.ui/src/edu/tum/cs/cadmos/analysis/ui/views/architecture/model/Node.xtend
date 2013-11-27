package edu.tum.cs.cadmos.analysis.ui.views.architecture.model

import java.util.ArrayList
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.graphics.Rectangle

abstract class Node {
	
		
	var protected  subnodes = new ArrayList<Node>
	
	def abstract Rectangle getSize()
	def abstract void drawChildren(Point origin)
	
	override toString(){
		this.class.simpleName+": \n"+subnodes.map["\t"+it.toString]
	}
}