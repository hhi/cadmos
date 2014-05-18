package edu.tum.cs.cadmos.analysis.schedule

import edu.tum.cs.cadmos.analysis.architecture.model.DeploymentModel
import edu.tum.cs.cadmos.analysis.architecture.model.Edge
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileWriter
import java.util.HashSet
import java.util.List
import java.util.Map
import org.eclipse.core.resources.IFile
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.xbase.lib.Pair

import static edu.tum.cs.cadmos.analysis.schedule.UnsatCorePreferences.*

import static extension edu.tum.cs.cadmos.analysis.schedule.ScheduleSMTUtils.*

class ScheduleSMTGeneratorWithUnsatCore {
	
	var static id = 1;
	var static AssertionNameMapping map;

	/**
	 * Creates the given file and writes the given contents to it.
	 */
	private def static generateFile(File file, CharSequence contents) {
		val writer = new FileWriter(file, false)
		writer.write(contents.toString)
		writer.close
		IOOutput.print(contents.toString());
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
			"UnsatCore.smt2"
		generateFile(new File(outputDirectory, generatedFileName),
			deploymentModel.generateComponents())
		generatedFileName

	}

	private def static generateComponents(DeploymentModel deploymentModel) {
		val s = new StringBuilder
		map = AssertionNameMapping.SINGLETON
		map.clear
		
		
		s.append(
		'''
			;Declare generation of UNSAT core
			(set-option :produce-unsat-cores true)
			
		''')
		if(GENERATE__PERIOD_TIMES){
		s.append('''
			;Declare the period times.
			«deploymentModel.period.keySet.toList.declarePeriods»
			
			''')}
		s.append('''
			;Declare the possible allocation as a function.
			(declare-datatypes () ((components «deploymentModel.softwareComponentDFG.
													componentsStringWithPeriodicity(deploymentModel.period)»)))
			(declare-datatypes () ((platform «deploymentModel.processingComponentDFG.componentsString»)))
			(declare-fun mapping (components) platform)
			
			''')
		if(GENERATE__DURATION_TIMES){
		s.append('''
			;Define the duration times of the components on the different processors.
			«deploymentModel.generateDuration()»
			
			''')}
		if(GENERATE__UPPER_LOWER_LIMIT){
		s.append('''							
			; Declare the starting and finish times of the components in the schedule.
			(declare-fun start (components) Int)
			(declare-fun finish (components) Int)
			
			; Start lower limit.
			(assert (forall ((x components)) (>= (start x) 0)))
			; Finish upper limit.
			«deploymentModel.softwareComponentDFG.generateFinishAssertions(deploymentModel.period)»
			
			''')}
		if(GENERATE__MULTIRATE_CONSTANT_PERIOD){
		s.append('''
			; Multirate schedule constant periodicity.
			«deploymentModel.softwareComponentDFG.generateConstantPeriodicityAssertions(deploymentModel.period)»
			
			''')}
		if(GENERATE__MULTIRATE_ALLOCATION_CONSISTENCY){
		s.append('''
			; Multirate allocation consistency.
			«deploymentModel.softwareComponentDFG.generateAllocationConsistencyAssertions(deploymentModel.period)»
			
			''')}
		s.append('''
			; Finish computation.
			(assert (forall ((x components)) (= (finish x) (+ (start x) (dR x (mapping x))))))
			
			''')
		if(GENERATE__PRECEDENCE_CONSTRAINTS){
		s.append('''
			; Precedence constraints.
			«deploymentModel.softwareComponentDFG.generatePrecedenceConstraints(deploymentModel.period)»
			
			''')}
		if(GENERATE__TRANSMISSION_LATENCY_COSTS){
		s.append('''
			; Transmission latencies costs.
			«deploymentModel.softwareComponentDFG.generateTransmissionLatenciesConstraints(deploymentModel.transmissionLatency, deploymentModel.period)»
			
			''')}
		if(GENERATE__TRANSMISSION_DURATION_CONSTRAINTS){
		s.append('''
			; Transmission duration constraints.
			«deploymentModel.softwareComponentDFG.generateTransmissionDurationConstraints(deploymentModel.transmissionDuration, deploymentModel.period)»
			
			''')}
		if(GENERATE__ATOMIC_ON_SAME_CORE_ASSUMPTION){
		s.append('''
			; Atomic software components run on same core assumption.
			«deploymentModel.atomicSoftwareComponents.generateAtomicSoftwareComponentsAssumption(deploymentModel.period)»
			
			''')}
		if(GENERATE__ROBUSTNESS_REQUIREMENTS){
		s.append('''
			; Robustness requirements.
			«deploymentModel.robustness.generateLatencyRequirements(deploymentModel.period, false)»
			
			''')}
		if(GENERATE__LATENCY_REQUIREMENTS){
		s.append('''
			; Latency requirements.
			«deploymentModel.latency.generateLatencyRequirements(deploymentModel.period, true)»
			
			''')}
		if(GENERATE__OVERLAP_CONSTRAINTS){
		s.append('''
			; Overlap constraints.
			(assert (forall ((x components) (y components)) 
				(=> (and (distinct x y) (= (mapping x) (mapping y)))
					(or (<= (finish x) (start y)) 
						(<= (finish y) (start x))))))
			
			''')}
			
		s.append('''
			; Simplify the start expressions.
			«simplifyStartTimes(deploymentModel.softwareComponentDFG.componentsWithPeriodicity(deploymentModel.period))»
			
			; Simplify the mapping expressions.
			«simplifyMapping(deploymentModel.softwareComponentDFG.componentsWithPeriodicity(deploymentModel.period))»
			
			(check-sat)
			(get-model)
			(get-unsat-core)
		''')
		IOOutput.print(s.toString());
		s.toString
	}
	
	private def static generateAllocationConsistencyAssertions(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
													Map<Integer, List<String>> periodMap) {
		
		if(CORE__MULTIRATE_ALLOCATION_CONSISTENCY){
		val s = new StringBuilder
			for(pairSc : softwareComponentDFG.componentsWithStringConsecutivePeriodicity(periodMap)){
				val name = pairSc.value.key+"_"+pairSc.key.key
				val ass_name = "multirate_alloc_consistency_"+name
				map.put(ass_name, null)
				if(AssertionNameMapping.SINGLETON.isRelax(ass_name)){
					s.append("; relaxed assertion \n;")					
				}
				s.append('''(assert (! (= (mapping «pairSc.key.key») (mapping «pairSc.value.key»)) :named «ass_name»))
				''')
			}
			return s.toString
		}
		'''«FOR pairSc : softwareComponentDFG.componentsWithStringConsecutivePeriodicity(periodMap) SEPARATOR "\n"
				»(assert (= (mapping «pairSc.key.key») (mapping «pairSc.value.key»)))«ENDFOR»'''
	}
	
	private def static generateConstantPeriodicityAssertions(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
													Map<Integer, List<String>> periodMap) {
	if(CORE__MULTIRATE_CONSTANT_PERIOD){
		val s = new StringBuilder
		for(pairSc : softwareComponentDFG.componentsWithStringConsecutivePeriodicity(periodMap)){
			val name = pairSc.value.key+"_"+pairSc.key.key
			val ass_name = "multirate_const_period_"+name
			map.put(ass_name, null)
			if(AssertionNameMapping.SINGLETON.isRelax(ass_name)){
					s.append("; relaxed assertion \n;")					
			}
			s.append('''(assert (! (= (start «pairSc.key.key») (+ (start «pairSc.value.key») T«pairSc.key.value.periodTime(periodMap)»)) :named «ass_name»))
			''')
		}
		
		return s.toString
	}
		'''«FOR pairSc : softwareComponentDFG.componentsWithStringConsecutivePeriodicity(periodMap) SEPARATOR "\n"
				»(assert (= (start «pairSc.key.key») (+ (start «pairSc.value.key») T«pairSc.key.value.periodTime(periodMap)»)))«ENDFOR»'''
														
	}
	
	private def static declarePeriods(List<Integer> periodValues) {
		if(CORE__PERIOD_TIMES){
		val s = new StringBuilder
			for(period : periodValues){
				val name = period.toString
				val ass_name = "iteration_period_T" + name
				map.put(ass_name, null)
				if (AssertionNameMapping.SINGLETON.isRelax(ass_name)) {
					s.append(
					'''; relaxed assertion
(declare-const T«name» Int)
;(assert (!(= T«name» «name») :named «ass_name»))
						
					''')
				} else {
					s.append(
					'''
						(declare-const T«name» Int)
						(assert (!(= T«name» «name») :named «ass_name»))
						
					''')
				}
			}
			return s.toString
		}
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
															
		if(CORE__TRANSMISSION_DURATION_CONSTRAINTS) {
			val s = new StringBuilder
			val ass_name = "transmission_duration_constraints"
			map.put(ass_name, null)
			var relaxed = ""
			if (AssertionNameMapping.SINGLETON.isRelax(ass_name)) {
				s.append("; relaxed assertion \n")
				relaxed = ";"
			}

			s.append('''«relaxed»(assert (>= «periodMap.lcmOfComponents» (+ «FOR edge : softwareComponentDFG.edges SEPARATOR " "
			»«IF edge.src(softwareComponentDFG) != "" && edge.dst(softwareComponentDFG) != ""
					» (ite (not (= (mapping «edge.src(softwareComponentDFG)»_1) (mapping «edge.dst(softwareComponentDFG)»_1))) «
					edge.duration(transmissionDuration)» 0)«ENDIF»«ENDFOR»)))''')
			
			
			
			return s
		}
															
		'''(assert (>= «periodMap.lcmOfComponents» (+ «FOR edge : softwareComponentDFG.edges SEPARATOR " "
			»«IF edge.src(softwareComponentDFG) != "" && edge.dst(softwareComponentDFG) != ""
					» (ite (not (= (mapping «edge.src(softwareComponentDFG)»_1) (mapping «edge.dst(softwareComponentDFG)»_1))) «
					edge.duration(transmissionDuration)» 0)«ENDIF»«ENDFOR»)))'''														
	}
	
	private def static generateTransmissionLatenciesConstraints(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG,
														Map<Pair<String, String>, Integer> transmissionLatency,
														Map<Integer, List<String>> periodMap) {
															
		if (CORE__TRANSMISSION_LATENCY_COSTS) {
			val usedComponentPairs = new HashSet()
			val assertions = new StringBuilder()
			softwareComponentDFG.edges.filter [
				!usedComponentPairs.contains(
					new Pair(softwareComponentDFG.getSource(it).id, softwareComponentDFG.getDest(it).id))
			].forEach [
				
				val src = softwareComponentDFG.getSource(it).id
				val dest = softwareComponentDFG.getDest(it).id
				val ass_name = "transmission_latency_" + src + "_" + dest
				map.put(ass_name, null)
				var relaxed = ""
				if (AssertionNameMapping.SINGLETON.isRelax(ass_name)) {
					assertions.append("; relaxed assertion \n")
					relaxed = ";"
				}
				println(it.id)
				val outComponentPort = it.id.substring(1, it.id.indexOf(", "))
				val inComponentPort = it.id.substring(it.id.indexOf(", ") + 2, it.id.indexOf(")"))
				var latency = transmissionLatency.get(new Pair(outComponentPort, ""))
				// Latency was not defined in this case for the component.
				if(latency == null && outComponentPort.contains(".") && inComponentPort.contains(".")) latency = 0
				if (latency != null) {
					val outComponent = outComponentPort.substring(0, outComponentPort.indexOf("."))
					val inComponent = inComponentPort.substring(0, inComponentPort.indexOf("."))
					for (per : 1 .. outComponent.periodNrOfExecutions(periodMap)) {
					assertions.append(
					'''
					«relaxed»(declare-const Finish«softwareComponentDFG.getSource(it).id»_«per»«id=id+1» Int)
					«relaxed»(assert (= Finish«softwareComponentDFG.getSource(it).id»_«per»«id» (ite (= (* (div (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)») «inComponent.periodTime(periodMap)») (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1))) (/ (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)») (+ 1 (/ (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)»)))))
					''')
					assertions.append('''«relaxed»(assert ''' + 
					'''(=> (not (= (mapping «softwareComponentDFG.getDest(it).id»_1) (mapping «softwareComponentDFG.getSource(it).id»_1)))
					''' + "\t(>= (+ (start " + softwareComponentDFG.getDest(it).id + "_1) \n"+relaxed+"\t(* T" + inComponent.periodTime(periodMap) + " " + 
					'''Finish«softwareComponentDFG.getSource(it).id»_«per»«id»))
					''')
					assertions.append(relaxed+"\t(+ (finish " + softwareComponentDFG.getSource(it).id + "_" + per + ") " + latency + "))))\n")	
				}
				usedComponentPairs.add(new Pair(softwareComponentDFG.getSource(it).id, softwareComponentDFG.getDest(it).id))	
			}
			]
			return assertions
		}
		
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
					assertions.append(
					'''
					(declare-const Finish«softwareComponentDFG.getSource(it).id»_«per»«id=id+1» Int)
					(assert (= Finish«softwareComponentDFG.getSource(it).id»_«per»«id» (ite (= (* (div (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)») «inComponent.periodTime(periodMap)») (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1))) (/ (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)») (+ 1 (/ (- (finish «softwareComponentDFG.getSource(it).id»_«per») (start «softwareComponentDFG.getDest(it).id»_1)) «inComponent.periodTime(periodMap)»)))))
					''')
					assertions.append("(assert " + 
					'''(=> (not (= (mapping «softwareComponentDFG.getDest(it).id»_1) (mapping «softwareComponentDFG.getSource(it).id»_1)))
					''' + "\t(>= (+ (start " + softwareComponentDFG.getDest(it).id + "_1) \n\t(* T" + inComponent.periodTime(periodMap) + " " + 
					'''Finish«softwareComponentDFG.getSource(it).id»_«per»«id»))
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
		if (CORE__PRECEDENCE_CONSTRAINTS) {
			val s = new StringBuilder
			for (channel : softwareComponentDFG.edges) {
				for (precComponents : softwareComponentDFG.getSource(channel).precedenceComponents(
					softwareComponentDFG.getDest(channel), periodMap)) {
					val name = precComponents.key + "_" + precComponents.value
					val ass_name = "precedence_" + name
					map.put(ass_name, null)
					if (AssertionNameMapping.SINGLETON.isRelax(ass_name)) {
						s.append("; relaxed assertion \n;")
					}
					s.append(
						'''(assert (! (<= (finish «precComponents.key») (start «precComponents.value»)) :named «ass_name»))
							''')
				}
			}

			return s.toString
		}
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
											Map<Integer, List<String>> periodMap, boolean isLatencyGeneration) {
												
		if((isLatencyGeneration && CORE__LATENCY_REQUIREMENTS) || (!isLatencyGeneration && CORE__ROBUSTNESS_REQUIREMENTS)){
			val s = new StringBuilder
			var ass_name = "latency_requirement_"
			if(!isLatencyGeneration){
				ass_name = "robustness_requirement_"
			}
			
			for(robPair : robustnessMap.entrySet){
				for(per : 1..robPair.key.key.value.periodNrOfExecutions(periodMap)){
			val element = robPair.key.value.value
			map.put(ass_name+element, null)
			var relaxed = ""
			if (AssertionNameMapping.SINGLETON.isRelax(ass_name+element)) {
				s.append("; relaxed assertion \n")
				relaxed = ";"
			}
					s.append('''
				(declare-const «robPair.key.value.value»«id=id+1» Int)
				«relaxed»(assert (= «robPair.key.value.value»«id» «IF robPair.key.value.key.startsWith("start")»(ite (= (* (div (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «
					robPair.key.value.value.periodTime(periodMap)») «robPair.key.value.value.periodTime(periodMap)») (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1))) (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key
					»_1))  «robPair.key.value.value.periodTime(periodMap)») (+ 1 (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»)))))«
					ELSE»«relaxed»(+ 1 (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»))))«ENDIF»
				«relaxed»(assert (>= (+ («robPair.key.value.key»_1) (* T«robPair.key.value.value.periodTime(periodMap)» «robPair.key.value.value»«id»))
				«relaxed»  		(+ («robPair.key.key.key»_«per») «robPair.value.key»)))
				«relaxed»(assert (<= (+ («robPair.key.value.key»_1) (* T«robPair.key.value.value.periodTime(periodMap)» «robPair.key.value.value»«id»))
				«relaxed»  		(+ («robPair.key.key.key»_«per») «robPair.value.value»)))
					''')
				}
			}
			return s
		}
												
												
		  '''«FOR robPair : robustnessMap.entrySet SEPARATOR "\n"»«FOR per : 1..robPair.key.key.value.periodNrOfExecutions(periodMap)»
				(declare-const «robPair.key.value.value»«id=id+1» Int)
				(assert (= «robPair.key.value.value»«id» «IF robPair.key.value.key.startsWith("start")»(ite (= (* (div (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «
					robPair.key.value.value.periodTime(periodMap)») «robPair.key.value.value.periodTime(periodMap)») (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1))) (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key
					»_1))  «robPair.key.value.value.periodTime(periodMap)») (+ 1 (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»)))))«
					ELSE»(+ 1 (/ (- («robPair.key.key.key»_«per») («robPair.key.value.key»_1)) «robPair.key.value.value.periodTime(periodMap)»))))«ENDIF»
				(assert (>= (+ («robPair.key.value.key»_1) (* T«robPair.key.value.value.periodTime(periodMap)» «robPair.key.value.value»«id»))
				  		(+ («robPair.key.key.key»_«per») «robPair.value.key»)))
				(assert (<= (+ («robPair.key.value.key»_1) (* T«robPair.key.value.value.periodTime(periodMap)» «robPair.key.value.value»«id»))
				  		(+ («robPair.key.key.key»_«per») «robPair.value.value»)))
		  	«ENDFOR»«ENDFOR»
		  '''
	}

	private def static generateFinishAssertions(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG, 
													Map<Integer, List<String>> periodMap) {
		if(CORE__UPPER_LOWER_LIMIT){
			val s = new StringBuilder
			for(sc : softwareComponentDFG.componentsWithPeriodicity(periodMap)){
				val ass_name = "finish_"+sc.key+"_T"+sc.value.periodTime(periodMap)
				map.put(ass_name ,null)
				if(AssertionNameMapping.SINGLETON.isRelax(ass_name)){
					s.append("; relaxed assertion \n;")					
				}
				s.append('''(assert (! (<= (finish «sc.key») (* «sc.key.substring(sc.key.lastIndexOf("_")+1)»  T«sc.value.periodTime(periodMap)»)) :named finish_«sc.key»_T«sc.value.periodTime(periodMap)»))
				''')
			}
			return s.toString
		}
		'''«FOR sc : softwareComponentDFG.componentsWithPeriodicity(periodMap) SEPARATOR "\n"
						»(assert (<= (finish «sc.key») (* «sc.key.substring(sc.key.lastIndexOf("_")+1)»  T«sc.value.periodTime(periodMap)»)))«ENDFOR»'''
	}

	private def static generateDuration(DeploymentModel deploymentModel) {
		if(CORE__DURATION_TIMES){
			val s = new StringBuilder
			s.append("(declare-fun dR ((components) (platform)) Int) \n")
			
			for(sc : deploymentModel.softwareComponentDFG.componentsWithPeriodicity(deploymentModel.period)){
				for(pc : deploymentModel.processingComponentDFG.components){
					val task = sc.key
					val core = pc.id
					val dR = sc.value.executionTime(pc, deploymentModel.wcet)
					val ass_name = "duration_"+task+"_"+core
					map.put(ass_name, null)
					if(AssertionNameMapping.SINGLETON.isRelax(ass_name)){
						s.append("; relaxed assertion \n;")					
					}
					s.append("(assert (!(= (dR "+task+" "+core+") "+dR+") :named "+ass_name+"))\n")
				}
			}
			
			return s.toString
		}
		'''
		(define-fun dR ((c components) (x platform)) Int
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
