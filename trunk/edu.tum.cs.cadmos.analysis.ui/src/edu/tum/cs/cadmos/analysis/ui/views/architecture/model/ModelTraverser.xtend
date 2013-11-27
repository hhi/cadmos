package edu.tum.cs.cadmos.analysis.ui.views.architecture.model

import edu.tum.cs.cadmos.language.cadmos.Channel
import edu.tum.cs.cadmos.language.cadmos.Component
import java.util.HashSet
import java.util.Set
import edu.tum.cs.cadmos.language.cadmos.Embedding

class ModelTraverser {
	
	
	
	var HashSet<Component> visited = new HashSet 
	var ComponentNode rootNode
	
	def traverseRoot(Component root){
		visited.clear
		val firstRow = root.eContents.filter(Channel).filter[sourceComponent == root].map[sinkComponent].toSet
		rootNode = new ComponentNode(root);
		rootNode.addChildren(firstRow.map[traverse])
		return rootNode
	}
	
	def Node traverse(Component c){
		val successors = c.newSuccessors
		println(successors.size)
		if(successors.size == 0){
			return new ComponentNode(c)	
		}else if(successors.size == 1){
			return new SequentialNode(new ComponentNode(c), traverse(successors.head))
		}
		return new ParallelNode(successors.map[traverse])
	}
	
	def Component getSinkComponent(Channel ch){
		ch.snk.port.eContainer as Component
	}
	
	def Component getSourceComponent(Channel ch){
		ch.src.port.eContainer as Component
	}
	
	def Set<Component> getSuccessors(Component c) {
		//clean up
		println(c.parentComponent)
		var test = c.parentComponent.eContents
		.filter(Channel)
		.filter[sourceComponent == c]
		.map[sinkComponent].toSet
		return test
	}
	
	def Set<Component> getNewSuccessors(Component c){
		c.successors.filter[c2 |!c2.visited].toSet
	}
	
	def visit(Component c){
		visited.add(c)
	}
	
	def boolean visited(Component c){
		visited.contains(c)
	}
	
	def Component getParentComponent(Component c){
		c.eContainer.eContents.filter(Component).filter[eContents.filter(Embedding).filter[component==c]!=0].get(0)
	}
}