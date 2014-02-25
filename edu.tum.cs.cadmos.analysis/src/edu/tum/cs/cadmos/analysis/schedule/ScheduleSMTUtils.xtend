package edu.tum.cs.cadmos.analysis.schedule

import edu.tum.cs.cadmos.analysis.architecture.model.Edge
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.tum.cs.cadmos.language.cadmos.Port
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.ArrayList
import java.util.Map
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
	
	def static componentsStringWithPeriodicity(DirectedSparseMultigraph<Vertex, Edge> componentDFG, Map<Integer,
												List<String>> periodMap) {
		val childComponents = componentDFG.vertices.toList
		
		'''«FOR vertex : childComponents SEPARATOR " "»«vertex.verticesWithPeriodicity(periodMap)»«ENDFOR»'''
	}
	
	private def static verticesWithPeriodicity(Vertex vertex, 
												Map<Integer, List<String>> periodMap) {
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
											 Map<Integer,List<String>> periodMap) {
		val List<Pair<String, Vertex>> allVertices = new ArrayList()
		for (Vertex v : componentDFG.vertices.toList) {
			allVertices.addAll(v.verticesListWithPeriodicity(periodMap))
		}
		
		allVertices
	}
	
	def static componentsWithStringConsecutivePeriodicity(DirectedSparseMultigraph<Vertex, Edge> componentDFG,
											 Map<Integer,List<String>> periodMap) {
		val List<Pair<Pair<String, Vertex>, Pair<String, Vertex>>> allVertices = new ArrayList()
		for (Vertex v : componentDFG.vertices.toList) {
			val vList = v.verticesListWithPeriodicity(periodMap)
			var i = 1
			while (i < vList.size) {
				allVertices.add(new Pair(vList.get(i), vList.get(i-1)))
				i = i + 1
			}
		}
		
		allVertices
	}
	
	private def static verticesListWithPeriodicity(Vertex vertex, 
													Map<Integer, List<String>> periodMap) {
		var i = 1
		val lcm = periodMap.keySet.toList.lcm
		val vlist = new ArrayList()
		while (i <= lcm/vertex.periodTime(periodMap)) {
			vlist.add(new Pair(vertex.id + "_" + i, vertex)) 
			i = i + 1
		}
		vlist
	}
	
	def static precedenceComponents(Vertex src, Vertex dst, Map<Integer, List<String>> periodMap) {
		val srcList = src.verticesListWithPeriodicity(periodMap)
		val dstList = dst.verticesListWithPeriodicity(periodMap)
		
		val List<Pair<String, String>> pairComponentList = new ArrayList()
	
		val minElements = Math::min(srcList.size, dstList.size)
		var i = 0
		while (i < minElements) {
			pairComponentList.add(new Pair(srcList.get(i).key, dstList.get(i).key))
			i = i + 1
		} 
		
		pairComponentList
	}
	
		
	def static executionTime(Vertex sc, Vertex pc, Map<Pair<String, String>, Integer> execMap) {
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
	
	def static periodNrOfExecutions (String sc, Map<Integer, List<String>> periodMap) {
		var period = 0
		for (Entry<Integer, List<String>> e : periodMap.entrySet) {
			if (e.value.contains(sc)) {
				period = e.key
			}
		}
		if (period > 0)
			return periodMap.keySet.toList.lcm / period
		return 1
	}
	
	def static periodTime (String sc, Map<Integer, List<String>> periodMap) {
		var period = 0
		for (Entry<Integer, List<String>> e : periodMap.entrySet) {
			if (e.value.contains(sc)) {
				period = e.key
			}
		}
		
		return period
	}
	
	def static periodTime (Vertex sc, Map<Integer, List<String>> periodMap) {
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