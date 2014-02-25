package edu.tum.cs.cadmos.analysis.schedule

import java.io.File
import java.io.FileWriter
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.tum.cs.cadmos.analysis.architecture.model.Edge

import static extension edu.tum.cs.cadmos.analysis.schedule.ScheduleSMTUtils.*
import java.util.List
import edu.tum.cs.cadmos.analysis.architecture.model.DeploymentModel
import java.util.Map

class ScheduleSMTGenerator {

	/**
	 * Creates the given file and writes the given contents to it.
	 */
	private def static generateFile(File file, CharSequence contents) {
		val writer = new FileWriter(file, false)
		writer.write(contents.toString)
		writer.close
	}

	def static doGenerate(File outputDirectory, DeploymentModel deploymentModel) {
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

	private def static generatePrecedenceConstraints(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG,
														Map<Integer, List<String>> periodMap) {
		'''«FOR channel : softwareComponentDFG.edges SEPARATOR "\n"»«
				FOR precComponents : softwareComponentDFG.getSource(channel).precedenceComponents(
										softwareComponentDFG.getDest(channel), periodMap) SEPARATOR "\n"»(assert (<= (finish «
										precComponents.key») (start «precComponents.value»)))«ENDFOR»«ENDFOR»'''
	}
	
	private def static generateAtomicSoftwareComponentsAssumption(List<List<Pair<String, String>>> ascList,
																	Map<Integer, List<String>> periodMap) {
		'''(assert (= «FOR ascComponents : ascList SEPARATOR " "
		»«FOR components : ascComponents SEPARATOR " "»«FOR per : 1..components.value.periodNrOfExecutions(periodMap)
			SEPARATOR " "»(mapping «components.key»_«per»)«ENDFOR»«ENDFOR»«ENDFOR»))
		'''
	}
	
	private def static generateLatencyRequirements(
											Map<Pair<Pair<String, String>, Pair<String, String>>, Pair<Integer, Integer>> robustnessMap,
											Map<Integer, List<String>> periodMap) {
		  '''«FOR robPair : robustnessMap.entrySet SEPARATOR "\n"»«FOR per : 1..robPair.key.key.value.periodNrOfExecutions(periodMap)»
			  (assert (>= (+ («robPair.key.value.key»_1) (* T«robPair.key.value.value.periodTime(periodMap)» 
			  		(ite (> («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) (+ 1 (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»)) 
			  		(/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»))))
			  		(+ («robPair.key.key.key»_«per») «robPair.value.key»)))
			  (assert (<= (+ («robPair.key.value.key»_1) (* T«robPair.key.value.value.periodTime(periodMap)» 
			  		(ite (> («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) (+ 1 (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»)) 
			  		(/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»))))
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
