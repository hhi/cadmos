package edu.tum.cs.cadmos.analysis.architecture.model

import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.util.HashMap

class DeploymentModel {
	@Property DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG
	@Property DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG
	@Property HashMap<Vertex, Vertex> wcet
	
	new () {
		_softwareComponentDFG = null
		_processingComponentDFG = null 
		_wcet = null
	}
	
	new(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
			DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG,
			HashMap<Vertex, Vertex> wcet) {
		_softwareComponentDFG = softwareComponentDFG
		_processingComponentDFG = processingComponentDFG
		_wcet = wcet
	}
	
	def isReadyToSchedule() {
		softwareComponentDFG != null && processingComponentDFG != null && wcet != null
	}
	
	
}