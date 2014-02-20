package edu.tum.cs.cadmos.analysis.schedule

import edu.tum.cs.cadmos.analysis.architecture.model.Edge
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.HashMap
import edu.tum.cs.cadmos.language.cadmos.Embedding

class ScheduleSMTUtils {
	
	static val INFINITY = (1<<30)
	
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
	
	def static executionTime(Vertex sc, Vertex pc, HashMap<Pair<String, String>, Integer> execMap) {
//		val rand = new Random
//		Integer::toString(rand.nextInt(3))

		if (sc.data instanceof Embedding && pc.data instanceof Embedding) {
			val typeSc = (sc.data as Embedding).component.name
			val typePc = (pc.data as Embedding).component.name
			val execTime = execMap.get(new Pair(typeSc, typePc));
			
			if (execTime == null)
				return INFINITY
			return execTime
		}
		
		// case of Port
		0
	}
}