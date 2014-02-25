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
	@Property HashMap<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> latency
	
	new () {
		_softwareComponentDFG = null
		_processingComponentDFG = null 
		_wcet = null
		_period = null
		_robustness = null
		_latency = null
	}
	
	new(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
			DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG,
			HashMap<Pair<String, String>, Integer> wcet,
			HashMap<Integer, List<String>> period,
			HashMap<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> robustness,
			HashMap<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> latency) {
		_softwareComponentDFG = softwareComponentDFG
		_processingComponentDFG = processingComponentDFG
		_wcet = wcet
		_period = period
		_robustness = robustness
		_latency = latency
	}
	
	def isReadyToSchedule() {
		_softwareComponentDFG != null && _processingComponentDFG != null 
			&& _wcet != null && _period != null && _robustness != null && _latency != null
	}
	
	
}