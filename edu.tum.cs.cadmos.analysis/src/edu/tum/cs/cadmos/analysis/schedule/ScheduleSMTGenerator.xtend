package edu.tum.cs.cadmos.analysis.schedule

import edu.tum.cs.cadmos.analysis.architecture.model.DeploymentModel
import edu.tum.cs.cadmos.analysis.architecture.model.Edge
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.io.File
import java.io.FileWriter
import java.util.HashSet
import java.util.List
import java.util.Map

import static extension edu.tum.cs.cadmos.analysis.schedule.ScheduleSMTUtils.*
import org.eclipse.emf.ecore.EObject
import edu.tum.cs.cadmos.language.cadmos.Embedding
import org.eclipse.core.resources.IFile
import edu.tum.cs.cadmos.language.cadmos.Component
import java.io.ByteArrayInputStream

class ScheduleSMTGenerator {
	
	var static id = 1;

	/**
	 * Creates the given file and writes the given contents to it.
	 */
	private def static generateFile(File file, CharSequence contents) {
		val writer = new FileWriter(file, false)
		writer.write(contents.toString)
		writer.close
	}
	
	/**
	 * Creates the given file and writes the given contents to it.
	 */
	private def static generateDerivedFile(IFile iFile, CharSequence contents) {
		val source = new ByteArrayInputStream(contents.toString().getBytes())
		iFile.setContents(source, true, true, null)
		iFile.setDerived(true)
	}
	
	def static doGenerateCadmosSchedule(IFile outputFile, Map<EObject, Pair<String, Integer>> schedule, DeploymentModel deploymentModel) {
		generateDerivedFile(outputFile, schedule.generateSchedule(deploymentModel));
		
		outputFile
	}
	
	private def static generateSchedule(Map<EObject, Pair<String, Integer>> schedule, DeploymentModel deploymentModel) {
		'''
			«FOR imp : deploymentModel.imports SEPARATOR "\n"»«imp»«ENDFOR»
		
			schedule «deploymentModel.softwareComponentDFG.rootComponentName»_«deploymentModel.processingComponentDFG.rootComponentName» {
				«FOR period : deploymentModel.period.keySet.toList SEPARATOR "\n\n"
			    »periodic «period» {
					«FOR resource : deploymentModel.processingComponentDFG.components.filter[data instanceof Embedding] SEPARATOR "\n\n"
					»resource «((resource.data as Embedding).eContainer as Component).name».«resource.id» {
						«FOR entry : schedule.entrySet().filter[key instanceof Embedding && value.key.equals(resource.id) 
													&& (key as Embedding).component.name.periodTime(deploymentModel.period) == period] 
													SEPARATOR "\n"
						»task «(entry.key.eContainer as Component).name».«(entry.key as Embedding).name»	«
						IF entry.value.value >= period»«entry.value.value - period»«ELSE»«entry.value.value»«ENDIF»«ENDFOR»
					}«ENDFOR»
				}«ENDFOR»
			}
		'''
	}

	def static doGenerateSMTScript(File outputDirectory, DeploymentModel deploymentModel) {
		val generatedFileName = deploymentModel.softwareComponentDFG.componentName + "-" + deploymentModel.processingComponentDFG.componentName +
			".smt2"
		generateFile(new File(outputDirectory, generatedFileName),
			deploymentModel.generateComponents())
		generatedFileName

	}

	private def static generateComponents(DeploymentModel deploymentModel) {
		'''
			;Declare the period times.
			«deploymentModel.period.keySet.toList.declarePeriods»
			
			;Declare the possible allocation as a function.
			(declare-datatypes () ((components «deploymentModel.softwareComponentDFG.
													componentsStringWithPeriodicity(deploymentModel.period)»)))
			(declare-datatypes () ((platform «deploymentModel.processingComponentDFG.componentsString»)))
			(declare-fun mapping (components) platform)
			
			;Define the duration times of the components on the different processors.
			(define-fun dR ((c components) (x platform)) Int 
										«deploymentModel.generateDuration()»
										
			; Declare the starting and finish times of the components in the schedule.
			(declare-fun start (components) Int)
			(declare-fun finish (components) Int)
			
			; Start lower limit.
			(assert (forall ((x components)) (>= (start x) 0)))
			; Finish upper limit.
			«deploymentModel.softwareComponentDFG.generateFinishAssertions(deploymentModel.period)»
			
			; Multirate schedule constant periodicity.
			«deploymentModel.softwareComponentDFG.generateConstantPeriodicityAssertions(deploymentModel.period)»
			
			; Multirate allocation consistency.
			«deploymentModel.softwareComponentDFG.generateAllocationConsistencyAssertions(deploymentModel.period)»
			
			; Finish computation.
			(assert (forall ((x components)) (= (finish x) (+ (start x) (dR x (mapping x))))))
			
			; Precedence constraints.
			«deploymentModel.softwareComponentDFG.generatePrecedenceConstraints(deploymentModel.period)»
			
			; Transmission latencies costs.
			«deploymentModel.softwareComponentDFG.generateTransmissionLatenciesConstraints(deploymentModel.transmissionLatency, deploymentModel.period)»
			
			; Transmission duration constraints.
			«deploymentModel.softwareComponentDFG.generateTransmissionDurationConstraints(deploymentModel.transmissionDuration, deploymentModel.period)»
			
			; Atomic software components run on same core assumption.
			«deploymentModel.atomicSoftwareComponents.generateAtomicSoftwareComponentsAssumption(deploymentModel.period)»
			
			; Robustness requirements.
			«deploymentModel.robustness.generateLatencyRequirements(deploymentModel.period)»
			
			; Latency requirements.
			«deploymentModel.latency.generateLatencyRequirements(deploymentModel.period)»
			
			; Overlap constraints.
			(assert (forall ((x components) (y components)) 
				(=> (and (distinct x y) (= (mapping x) (mapping y)))
					(or (<= (finish x) (start y)) 
						(<= (finish y) (start x))))))
				
			; Simplify the start expressions.
			«simplifyStartTimes(deploymentModel.softwareComponentDFG.componentsWithPeriodicity(deploymentModel.period))»
			
			; Simplify the mapping expressions.
			«simplifyMapping(deploymentModel.softwareComponentDFG.componentsWithPeriodicity(deploymentModel.period))»
			
			(check-sat)
			(get-model)
		'''
	}
	
	private def static generateAllocationConsistencyAssertions(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
													Map<Integer, List<String>> periodMap) {
		'''«FOR pairSc : softwareComponentDFG.componentsWithStringConsecutivePeriodicity(periodMap) SEPARATOR "\n"
				»(assert (= (mapping «pairSc.key.key») (mapping «pairSc.value.key»)))«ENDFOR»'''
	}
	
	private def static generateConstantPeriodicityAssertions(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
													Map<Integer, List<String>> periodMap) {
		'''«FOR pairSc : softwareComponentDFG.componentsWithStringConsecutivePeriodicity(periodMap) SEPARATOR "\n"
				»(assert (= (start «pairSc.key.key») (+ (start «pairSc.value.key») T«pairSc.key.value.periodTime(periodMap)»)))«ENDFOR»'''
														
	}
	
	private def static declarePeriods(List<Integer> periodValues) {
		'''
			«FOR period : periodValues SEPARATOR "\n"»
			(declare-const T«period.toString» Int)
			(assert (= T«period.toString» «period.toString»))
			«ENDFOR»
		'''
	}
	
	private def static simplifyMapping(List<Pair<String, Vertex>> vertexList) {
		'''
		«FOR sc : vertexList SEPARATOR "\n"»
		(declare-const mapping«sc.key» platform)
		(assert (= mapping«sc.key» (mapping «sc.key»)))
		«ENDFOR»
		'''
	}
	
	private def static simplifyStartTimes(List<Pair<String, Vertex>> vertexList) {
		'''
		«FOR sc : vertexList SEPARATOR "\n"»
		(declare-const start«sc.key» Int)
		(assert (= start«sc.key» (start «sc.key»)))
		«ENDFOR»
		'''
	}
	
	private def static generateTransmissionDurationConstraints(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG,
														Map<Pair<String, String>, Integer> transmissionDuration,
														Map<Integer, List<String>> periodMap) {
		'''(assert (>= «periodMap.lcmOfComponents» (+ «FOR edge : softwareComponentDFG.edges SEPARATOR " "
			»«IF edge.src(softwareComponentDFG) != "" && edge.dst(softwareComponentDFG) != ""
					» (ite (not (= (mapping «edge.src(softwareComponentDFG)»_1) (mapping «edge.dst(softwareComponentDFG)»_1))) «
					edge.duration(transmissionDuration)» 0)«ENDIF»«ENDFOR»)))'''														
	}
	
	private def static generateTransmissionLatenciesConstraints(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG,
														Map<Pair<String, String>, Integer> transmissionLatency,
														Map<Integer, List<String>> periodMap) {
		val usedComponentPairs = new HashSet()
		val assertions = new StringBuilder()
		softwareComponentDFG.edges.filter[
			!usedComponentPairs.contains(new Pair(softwareComponentDFG.getSource(it).id, softwareComponentDFG.getDest(it).id))
		].forEach[
			val outComponentPort = it.id.substring(1, it.id.indexOf(", "))
			val inComponentPort = it.id.substring(it.id.indexOf(", ") + 2, it.id.indexOf(")"))
			var latency = transmissionLatency.get(new Pair(outComponentPort, ""))
			// Latency was not defined in this case for the component.
			if (latency == null && outComponentPort.contains(".") && inComponentPort.contains(".")) latency = 0
			
			if (latency != null) {
				val outComponent = outComponentPort.substring(0, outComponentPort.indexOf("."))
				val inComponent = inComponentPort.substring(0, inComponentPort.indexOf("."))
				for (per : 1..outComponent.periodNrOfExecutions(periodMap)) {
					assertions.append("(assert " + 
					'''(=> (not (= (mapping «softwareComponentDFG.getDest(it).id»_1) (mapping «softwareComponentDFG.getSource(it).id»_1)))
					''' + "\t(>= (+ (start " + softwareComponentDFG.getDest(it).id + "_1) \n\t(* T" + inComponent.periodTime(periodMap) + " " + 
					'''(ite (= (* (/ (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)») «inComponent.periodTime(periodMap)») (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1))) (/ (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)») (+ 1 (/ (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)»)))))
					''')
					assertions.append("\t(+ (finish " + softwareComponentDFG.getSource(it).id + "_" + per + ") " + latency + "))))\n")	
				}
				usedComponentPairs.add(new Pair(softwareComponentDFG.getSource(it).id, softwareComponentDFG.getDest(it).id))	
			}
		]
		
		assertions
	}

	private def static generatePrecedenceConstraints(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG,
														Map<Integer, List<String>> periodMap) {
		'''«FOR channel : softwareComponentDFG.edges SEPARATOR "\n"»«
				FOR precComponents : softwareComponentDFG.getSource(channel).precedenceComponents(
										softwareComponentDFG.getDest(channel), periodMap) SEPARATOR "\n"»(assert (<= (finish «
										precComponents.key») (start «precComponents.value»)))«ENDFOR»«ENDFOR»'''
	}
	
	private def static generateAtomicSoftwareComponentsAssumption(List<List<Pair<String, String>>> ascList,
																	Map<Integer, List<String>> periodMap) {
		if (ascList.size == 0) return ""
		'''(assert (= «FOR ascComponents : ascList SEPARATOR " "
		»«FOR components : ascComponents SEPARATOR " "»«FOR per : 1..components.value.periodNrOfExecutions(periodMap)
			SEPARATOR " "»(mapping «components.key»_«per»)«ENDFOR»«ENDFOR»«ENDFOR»))
		'''
	}
	
	private def static generateLatencyRequirements(
											Map<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> robustnessMap,
											Map<Integer, List<String>> periodMap) {
		  '''«FOR robPair : robustnessMap.entrySet SEPARATOR "\n"»«FOR per : 1..robPair.key.key.value.periodNrOfExecutions(periodMap)»
				(declare-const «robPair.key.value.value»«id=id+1» Int)
				(assert (= «robPair.key.value.value»«id» (ite (= (mod (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «
					robPair.key.value.value.periodTime(periodMap)») 0) (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key
					»_1))  «robPair.key.value.value.periodTime(periodMap)») (+ 1 (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»)))))
				(assert (>= (+ («robPair.key.value.key»_1) (* T«robPair.key.value.value.periodTime(periodMap)» «robPair.key.value.value»«id»))
				  		(+ («robPair.key.key.key»_«per») «robPair.value.key»)))
				(assert (<= (+ («robPair.key.value.key»_1) (* T«robPair.key.value.value.periodTime(periodMap)» «robPair.key.value.value»«id»))
				  		(+ («robPair.key.key.key»_«per») «robPair.value.value»)))
		  	«ENDFOR»«ENDFOR»
		  '''
	}

	private def static generateFinishAssertions(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
													Map<Integer, List<String>> periodMap) {
		'''«FOR sc : softwareComponentDFG.componentsWithPeriodicity(periodMap) SEPARATOR "\n"
						»(assert (<= (finish «sc.key») (* «sc.key.substring(sc.key.lastIndexOf("_")+1)»  T«sc.value.periodTime(periodMap)»)))«ENDFOR»'''
	}

	private def static generateDuration(DeploymentModel deploymentModel) {
		'''
			«FOR sc : deploymentModel.softwareComponentDFG.componentsWithPeriodicity(deploymentModel.period) SEPARATOR "\n"»(ite (= c «sc.key») «
				FOR pc : deploymentModel.processingComponentDFG.
				components.tail SEPARATOR " "»(ite (= x «pc.id») «sc.value.executionTime(pc, deploymentModel.wcet)»«ENDFOR» «sc.value.executionTime(
				deploymentModel.processingComponentDFG.components.head, deploymentModel.wcet)»«closedParanthesis(
					deploymentModel.processingComponentDFG.components.size - 1)»«ENDFOR» 0«closedParanthesis(
				deploymentModel.softwareComponentDFG.componentsWithPeriodicity(deploymentModel.period).size + 1)»
		'''
	}

	private def static closedParanthesis(int N) {
		var par = ""
		var nr = N
		while (nr > 0) {
			par = par + ")"
			nr = nr - 1
		}
		par
	}

}
