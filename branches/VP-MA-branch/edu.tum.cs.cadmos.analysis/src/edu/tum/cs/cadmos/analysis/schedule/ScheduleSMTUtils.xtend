package edu.tum.cs.cadmos.analysis.schedule

import edu.tum.cs.cadmos.analysis.architecture.model.Edge
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.tum.cs.cadmos.language.cadmos.Port
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map.Entry

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
	
	def static componentsStringWithPeriodicity(DirectedSparseMultigraph<Vertex, Edge> componentDFG, HashMap<Integer,
												List<String>> periodMap) {
		val childComponents = componentDFG.vertices.toList
		
		'''«FOR vertex : childComponents SEPARATOR " "»«vertex.verticesWithPeriodicity(periodMap)»«ENDFOR»'''
	}
	
	private def static verticesWithPeriodicity(Vertex vertex, 
												HashMap<Integer, List<String>> periodMap) {
		var i = 1
		val lcm = periodMap.keySet.toList.lcm
		var stringVertex = ""
		while (i <= lcm/vertex.periodTime(periodMap)) {
			stringVertex = stringVertex + " " + vertex.id + "_" + i 
			i = i + 1
		}
		stringVertex										
	}
	
	def static components(DirectedSparseMultigraph<Vertex, Edge> componentDFG) {
		componentDFG.vertices.toList
	}
	
	def static componentsWithPeriodicity(DirectedSparseMultigraph<Vertex, Edge> componentDFG,
											 HashMap<Integer,List<String>> periodMap) {
		val allVertices = new ArrayList()
		for (Vertex v : componentDFG.vertices.toList) {
			allVertices.addAll(v.verticesListWithPeriodicity(periodMap))
		}
		allVertices
	}
	
	private def static verticesListWithPeriodicity(Vertex vertex, HashMap<Integer,
												List<String>> periodMap) {
		var i = 1
		val lcm = periodMap.keySet.toList.lcm
		val vlist = new ArrayList()
		while (i <= lcm/vertex.periodTime(periodMap)) {
			vlist.add(new Pair(vertex.id + "_" + i, vertex)) 
			i = i + 1
		}
		vlist
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
	
	def static periodTime (Vertex sc, HashMap<Integer, List<String>> periodMap) {
		if (sc.data instanceof Embedding) {
			val typeSc = (sc.data as Embedding).component.name
			var period = 0
			for (Entry<Integer, List<String>> e : periodMap.entrySet) {
				if (e.value.contains(typeSc)) {
					period = e.key
				}
			}
			
			return period
		}
		if (sc.data instanceof Port) {
			val typeSc = ((sc.data as Port).eContainer as Component).name
			
			var period = 0
			for (Entry<Integer, List<String>> e : periodMap.entrySet) {
				if (e.value.contains(typeSc)) {
					period = e.key
				}
			}
			
			return period
		}
		
		return INFINITY
	}
	
	
	private def static gcd(Integer n1, Integer n2) {
		var a = n1
		var b = n2
	    while (b > 0) {
	        var temp = b
	        b = a % b
	        a = temp
	    }
	    return a;
	}
	
	private def static lcm(Integer a, Integer b) {
	    return a * (b / gcd(a, b))
	}
	
	private def static lcm(List<Integer> input) {
	    var result = input.get(0)
	    var i = 1;
	    while (i < input.length) {
	    	result = lcm(result, input.get(i))
	    	i = i+1
	    } 
	    result
	}
}