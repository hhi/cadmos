package edu.tum.cs.cadmos.analysis.architecture.model

import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.HashMap
import java.util.List

class DeploymentModel {
	@Property DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG
	@Property DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG
	@Property HashMap<Pair<String, String>, Integer> wcet
	@Property HashMap<Integer, List<String>> period
	
	new () {
		_softwareComponentDFG = null
		_processingComponentDFG = null 
		_wcet = null
	}
	
	new(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
			DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG,
			HashMap<Pair<String, String>, Integer> wcet,
			HashMap<Integer, List<String>> period) {
		_softwareComponentDFG = softwareComponentDFG
		_processingComponentDFG = processingComponentDFG
		_wcet = wcet
		_period = period
	}
	
	def isReadyToSchedule() {
		softwareComponentDFG != null && processingComponentDFG != null 
			&& wcet != null && period != null
	}
	
	
}