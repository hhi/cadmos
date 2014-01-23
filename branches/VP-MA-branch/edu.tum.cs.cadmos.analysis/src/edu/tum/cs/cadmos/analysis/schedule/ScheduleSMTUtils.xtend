package edu.tum.cs.cadmos.analysis.schedule

import edu.tum.cs.cadmos.analysis.architecture.model.Edge
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.Random

class ScheduleSMTUtils {
	
	def static componentName(DirectedSparseMultigraph<Vertex, Edge> componentDFG) {
		val child = componentDFG.vertices.toList.head.data
		val component = child.eContainer as Component
		
		component.name
	}
	
	def static componentsString(DirectedSparseMultigraph<Vertex, Edge> componentDFG) {
		val childComponents = componentDFG.vertices.toList
		
		'''«FOR vertex : childComponents SEPARATOR " "»«vertex.id»«ENDFOR»'''
	}
	
	def static components(DirectedSparseMultigraph<Vertex, Edge> componentDFG) {
		componentDFG.vertices.toList
	}
	
	def static executionTime(Vertex sc, Vertex pc) {
		val rand = new Random
		Integer::toString(rand.nextInt(3))
	}
}