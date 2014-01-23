package edu.tum.cs.cadmos.analysis.architecture.model

import edu.tum.cs.cadmos.language.cadmos.Channel
import edu.tum.cs.cadmos.language.cadmos.Component
import java.util.ArrayList
import java.util.List
import org.eclipse.emf.ecore.EObject
import edu.tum.cs.cadmos.language.cadmos.Embedding
import java.util.HashMap

//class DataFlowGraph {
//	
//	def static toDGF(Component c){
//		new DataFlowGraph(c).toDFG
//	}
//	val Component root;
//	
//	new(Component component) {
//		this.root = component
//	}
//	
//
//	def void toDFG(){
//	}
//
//	
//}
//	class Context{
//		val Component root
//		var List<Embedding> path = new ArrayList<Embedding>
//		val HashMap<String, Vertex> visitedNodes = new HashMap
//		var Embedding current
//		new (Component c){
//			this.root = c
//		}
//		
//		def enterScope(Embedding e){
//			path.add(e)
//			visit(e)
//			current = e
//		}
//		
//		def leaveScope(){
//			val tmp = visitedNodes.get(scope)
//			path.remove(current)
//			current = path.last
//			return tmp
//		}
//		
//		def getScope(){
//			path.fold(root.name)[a,b|a+"."+b.name]
//		}
//		
//		def visit(Embedding e){
//			if(!visited){
//				visitedNodes.put(scope, new Vertex(e))
//			}
//		}
//		
//		def boolean visited(){
//			visitedNodes.containsKey(scope)
//		}
//	}

	class Vertex{
		@Property String id
		@Property EObject data
	
		new(String id, EObject data){
			_id = id
			_data = data
		}
	
	}
	
	class Edge {
		@Property String id
		@Property int multiplicity
		new(String id, int multiplicity){
			_id = id
			_multiplicity = multiplicity
		}
	}