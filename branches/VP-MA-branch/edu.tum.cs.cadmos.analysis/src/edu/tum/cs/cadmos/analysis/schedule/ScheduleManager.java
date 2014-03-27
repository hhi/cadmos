package edu.tum.cs.cadmos.analysis.schedule;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Pair;

import edu.tum.cs.cadmos.analysis.architecture.model.DeploymentModel;
import edu.tum.cs.cadmos.analysis.architecture.model.utils.CostmodelUtils;
import edu.tum.cs.cadmos.analysis.architecture.model.utils.DFGTranslator;
import edu.tum.cs.cadmos.analysis.architecture.model.utils.RequirementsUtils;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Costmodel;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Import;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.Requirements;

public class ScheduleManager {

	private DeploymentModel deploymentModel = null;
	private Process z3_instance;
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

	private String getDefaultZ3Path() {
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			return "C:\\Program Files\\Z3\\z3.exe";
		} else {
			// Mac OS X
			return "/Applications/Z3/z3";
		}
	}

	public void schedule(IPath resourceDirectory, String resourceName) {
		final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace()
				.getRoot();
		final IPath location = workspaceRoot.getLocation();
		File outputDirectory = new File(location.toFile(), "SMTScripts");
		if (!outputDirectory.exists()) {
			outputDirectory.mkdir();
		}

		// getDefaultZ3Path()
		String pathZ3 = Platform.getPreferencesService().getString(
				"edu.tum.cs.cadmos.analysis.ui", "PATH_Z3", getDefaultZ3Path(),
				null);

		String generatedFileName = "";
		if (outputDirectory.isDirectory()) {
			generatedFileName = ScheduleSMTGeneratorWithUnsatCore
					.doGenerateSMTScript(outputDirectory, deploymentModel);
			// generatedFileName = ScheduleSMTGenerator.doGenerateSMTScript(
			// outputDirectory, deploymentModel);
			ProcessBuilder cProcess = new ProcessBuilder(pathZ3,
					generatedFileName);
			cProcess.directory(outputDirectory);

			Process process;
			try {
				IOOutput.print("Starting Z3 and calculating");
				long start = System.currentTimeMillis();
				process = cProcess.start();
				ScheduleManager.instance.z3_instance = process;
				InputStream inputStream = process.getInputStream();
				int b = 0;
				String output = "";
				while ((b = inputStream.read()) >= 0) {
					output += (char) b;
				}
				long stop = System.currentTimeMillis();
				ScheduleManager.instance.z3_instance = null;

				System.out.println(output);
				IOOutput.print(output);
				final HashMap<EObject, Pair<String, Integer>> schedule = ScheduleSMTParser
						.parse(output,
								deploymentModel.getSoftwareComponentDFG());
				System.out.println(printSchedule(schedule));
				IOOutput.print(printSchedule(schedule));
				System.out.println("returned after "
						+ (double) ((stop - start) / 100) / 10 + "s");
				IOOutput.print("returned after "
						+ (double) ((stop - start) / 100) / 10 + "s");

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IPath path = Path.fromOSString(location + "/"
						+ resourceDirectory + "/" + resourceName
						+ "Schedule.cadmos");
				IFile newSchedule = workspace.getRoot()
						.getFileForLocation(path);

				if (!newSchedule.exists()) {
					// empty content
					String content = "";
					InputStream source = new ByteArrayInputStream(
							content.getBytes());
					try {
						newSchedule.create(source, false, null);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}

				if (schedule != null) {
					ScheduleSMTGenerator.doGenerateCadmosSchedule(newSchedule,
							schedule, deploymentModel);
				}

			} catch (IOException e) {
				IOOutput.print(e.getMessage());
			}
		}

	}

	private String printSchedule(HashMap<EObject, Pair<String, Integer>> map) {
		if (map == null) {
			return "";
		}
		StringBuilder s = new StringBuilder();
		for (Entry<EObject, Pair<String, Integer>> e : map.entrySet()) {
			if (e.getKey() instanceof Embedding) {
				Embedding em = (Embedding) e.getKey();
				String core = e.getValue().getKey();
				Integer start = e.getValue().getValue();
				s.append(em.getName() + " -> " + core + " : " + start + "\n");
			} else if (e.getKey() instanceof Port) {
				Port p = (Port) e.getKey();
				String core = e.getValue().getKey();
				Integer start = e.getValue().getValue();
				s.append(p.getName() + " -> " + core + " : " + start + "\n");

			}
		}
		return s.toString();
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
		deploymentModel.setTransmissionDuration(CostmodelUtils
				.translateTransmissionDuration(costmodel));
	}

	public void deleteCostmodel() {
		deploymentModel.setWcet(null);
		deploymentModel.setTransmissionLatency(null);
		deploymentModel.setTransmissionDuration(null);
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

	public void addImports(EList<Import> imports) {
		final List<String> iList = new ArrayList<>();
		for (Import im : imports) {
			String newImport = "import ";
			newImport += im.getImportedNamespace();
			iList.add(newImport);
		}
		deploymentModel.setImports(iList);
	}

	public void deleteImports() {
		deploymentModel.setImports(null);
	}

	public void cancelZ3() {
		z3_instance.destroy();
	}

}
