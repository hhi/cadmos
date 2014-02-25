package edu.tum.cs.cadmos.analysis.architecture.model

import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.Map
import java.util.List

class DeploymentModel {
	@Property DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG
	@Property DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG
	@Property Map<Pair<String, String>, Integer> wcet
	@Property List<List<Pair<String, String>>> atomicSoftwareComponents
	@Property Map<Integer, List<String>> period
	@Property Map<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> robustness
	@Property Map<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> latency
	
	new () {
		_softwareComponentDFG = null
		_processingComponentDFG = null 
		_wcet = null
		_atomicSoftwareComponents = null
		_period = null
		_robustness = null
		_latency = null
	}
	
	new(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
			DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG,
			Map<Pair<String, String>, Integer> wcet,
			List<List<Pair<String, String>>> atomicSoftwareComponents,
			Map<Integer, List<String>> period,
			Map<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> robustness,
			Map<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> latency) {
		_softwareComponentDFG = softwareComponentDFG
		_processingComponentDFG = processingComponentDFG
		_wcet = wcet
		_atomicSoftwareComponents = atomicSoftwareComponents
		_period = period
		_robustness = robustness
		_latency = latency
	}
	
	def isReadyToSchedule() {
		_softwareComponentDFG != null && _processingComponentDFG != null 
			&& _wcet != null && _period != null && _robustness != null && _latency != null
	}
	
	
}