package edu.tum.cs.cadmos.analysis.schedule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import edu.tum.cs.cadmos.analysis.architecture.model.DeploymentModel;
import edu.tum.cs.cadmos.analysis.architecture.model.utils.CostmodelUtils;
import edu.tum.cs.cadmos.analysis.architecture.model.utils.DFGTranslator;
import edu.tum.cs.cadmos.analysis.architecture.model.utils.RequirementsUtils;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Costmodel;
import edu.tum.cs.cadmos.language.cadmos.Requirements;

public class ScheduleManager {

	private DeploymentModel deploymentModel = null;
	private static final ScheduleManager instance = new ScheduleManager();

	public static ScheduleManager getInstance() {
		return instance;
	}

	private ScheduleManager() {
		deploymentModel = new DeploymentModel();
	}

	public boolean readyToSchedule() {
		return deploymentModel.isReadyToSchedule();
	}

	public void schedule() {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace()
				.getRoot();
		final IPath location = workspaceRoot.getLocation();
		File outputDirectory = new File(location.toFile(), "SMTScripts");
		if (!outputDirectory.exists()) {
			outputDirectory.mkdir();
		}

		String generatedFileName = "";
		if (outputDirectory.isDirectory()) {
			generatedFileName = ScheduleSMTGenerator.doGenerate(
					outputDirectory, deploymentModel);
			ProcessBuilder cProcess = null;
			if (System.getProperty("os.name").equals("Mac OS X")) {
				// FIXME CD: will fix this
				// cProcess = new ProcessBuilder("/Applications/Z3/z3",
				// generatedFileName);
				cProcess = new ProcessBuilder("./z3  " + generatedFileName);
				cProcess.directory(outputDirectory);
			} else if (System.getProperty("os.name").startsWith("Windows")) {
				cProcess = new ProcessBuilder("cmd", "/C", "z3 "
						+ generatedFileName);
				cProcess.directory(outputDirectory);
			}

			Process process;
			try {
				process = cProcess.start();
				InputStream inputStream = process.getInputStream();
				int b = 0;
				String output = "";
				while ((b = inputStream.read()) >= 0) {
					output += (char) b;
				}
				System.out.println(output);
				final HashMap<EObject, Pair<String, Integer>> schedule = ScheduleSMTParser
						.parse(output,
								deploymentModel.getSoftwareComponentDFG());
				System.out.println(schedule);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void addSoftwareComponent(Component softwareComponent) {
		final DFGTranslator dfgTranslator = new DFGTranslator(softwareComponent);
		deploymentModel.setSoftwareComponentDFG(dfgTranslator
				.translateFlatGraphToDFG());
		deploymentModel.setAtomicSoftwareComponents(dfgTranslator
				.atomicSoftwareComponentsFlat());
	}

	public void deleteSoftwareComponent() {
		deploymentModel.setSoftwareComponentDFG(null);
	}

	public void addProcessingComponent(Component processingComponent) {
		deploymentModel.setProcessingComponentDFG(new DFGTranslator(
				processingComponent).translateFlatGraphToDFG());
	}

	public void deleteProcessingComponent() {
		deploymentModel.setProcessingComponentDFG(null);
	}

	public void addCostmodel(Costmodel costmodel) {
		deploymentModel.setWcet(CostmodelUtils.translateWCET(costmodel));
		deploymentModel.setTransmissionLatency(CostmodelUtils
				.translateTransmissionLatency(costmodel));
	}

	public void deleteCostmodel() {
		deploymentModel.setWcet(null);
		deploymentModel.setTransmissionLatency(null);
	}

	public void addPeriodFromRequirement(Requirements requirements) {
		deploymentModel.setPeriod(RequirementsUtils
				.translateComponentPeriodicity(requirements));
		deploymentModel.setRobustness(RequirementsUtils
				.translateComponentRobustness(requirements));
		deploymentModel.setLatency(RequirementsUtils
				.translateComponentLatency(requirements));
	}

	public void deletePeriod() {
		deploymentModel.setPeriod(null);
		deploymentModel.setRobustness(null);
		deploymentModel.setLatency(null);
	}

}
