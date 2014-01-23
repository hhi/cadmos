package edu.tum.cs.cadmos.analysis.schedule;

import java.io.File;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

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
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace()
				.getRoot();
		final IPath location = workspaceRoot.getLocation();
		File outputDirectory = new File(location.toFile(), "SMT Scripts");
		if (!outputDirectory.exists()) {
			outputDirectory.mkdir();
		}

		String generatedFileName = "";
		if (outputDirectory.isDirectory()) {
			generatedFileName = ScheduleSMTGenerator.doGenerate(
					outputDirectory, softwareComponentDFG,
					processingComponentDFG);
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
