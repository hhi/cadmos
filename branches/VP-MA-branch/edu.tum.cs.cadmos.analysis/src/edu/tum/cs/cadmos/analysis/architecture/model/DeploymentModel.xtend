package edu.tum.cs.cadmos.analysis.architecture.model

import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.HashMap
import java.util.List

class DeploymentModel {
	@Property DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG
	@Property DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG
	@Property HashMap<Pair<String, String>, Integer> wcet
	@Property HashMap<Integer, List<String>> period
	@Property HashMap<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> robustness
	
	new () {
		_softwareComponentDFG = null
		_processingComponentDFG = null 
		_wcet = null
		_period = null
		_robustness = null
	}
	
	new(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
			DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG,
			HashMap<Pair<String, String>, Integer> wcet,
			HashMap<Integer, List<String>> period,
			HashMap<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> robustness) {
		_softwareComponentDFG = softwareComponentDFG
		_processingComponentDFG = processingComponentDFG
		_wcet = wcet
		_period = period
		_robustness = robustness
	}
	
	def isReadyToSchedule() {
		_softwareComponentDFG != null && _processingComponentDFG != null 
			&& _wcet != null && _period != null && _robustness != null
	}
	
	
}