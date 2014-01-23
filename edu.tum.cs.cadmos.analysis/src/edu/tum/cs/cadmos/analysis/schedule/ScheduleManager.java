package edu.tum.cs.cadmos.analysis.schedule;

import edu.tum.cs.cadmos.analysis.architecture.model.Edge;
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class ScheduleManager {

	private DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG;

	private DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG;

	private static final ScheduleManager instance = new ScheduleManager();

	public static ScheduleManager getInstance() {
		return instance;
	}

	private ScheduleManager() {
		softwareComponentDFG = null;
		processingComponentDFG = null;
	}

	public boolean readyToSchedule() {
		return softwareComponentDFG != null && processingComponentDFG != null;
	}

	public void schedule() {
		if (!readyToSchedule()) {
			return;
		}

	}

	public void addSoftwareComponentDFG(
			DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG) {
		this.softwareComponentDFG = softwareComponentDFG;
	}

	public void deleteSoftwareComponentDFG() {
		softwareComponentDFG = null;
	}

	public void addProcessingComponentDFG(
			DirectedSparseMultigraph<Vertex, Edge> processingComponentDFG) {
		this.processingComponentDFG = processingComponentDFG;
	}

	public void deleteProcessingComponentDFG() {
		processingComponentDFG = null;
	}
}
