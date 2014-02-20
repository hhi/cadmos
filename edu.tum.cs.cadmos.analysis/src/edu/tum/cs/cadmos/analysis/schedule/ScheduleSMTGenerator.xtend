package edu.tum.cs.cadmos.analysis.schedule

import java.io.File
import java.io.FileWriter
import edu.uci.ics.jung.graph.DirectedSparseMultigraph
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex
import edu.tum.cs.cadmos.analysis.architecture.model.Edge

import static extension edu.tum.cs.cadmos.analysis.schedule.ScheduleSMTUtils.*
import java.util.List
import edu.tum.cs.cadmos.analysis.architecture.model.DeploymentModel

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
			;Declare the response time.
			(declare-const T1 Int)
			(assert (= T1 10))
			
			;Declare the possible allocation as a function.
			(declare-datatypes () ((components «deploymentModel.softwareComponentDFG.componentsString»)))
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
			«deploymentModel.softwareComponentDFG.generateFinishAssertions»
			
			; TODO: Multirate schedule constant periodicity.
			
			; TODO: Multirate allocation consistency.
			
			; Finish computation.
			(assert (forall ((x components)) (= (finish x) (+ (start x) (dR x (mapping x))))))
			
			; Precedence constraints.
			«deploymentModel.softwareComponentDFG.generatePrecedenceConstraints»
			
			; Overlap constraints.
			(assert (forall ((x components) (y components)) 
				(=> (and (distinct x y) (= (mapping x) (mapping y)))
					(or (<= (finish x) (start y)) 
						(<= (finish y) (start x))))))
				
			; Simplify the start expressions.
			«simplifyStartTimes(deploymentModel.softwareComponentDFG.components)»
			
			; Simplify the mapping expressions.
			«simplifyMapping(deploymentModel.softwareComponentDFG.components)»
			
			(check-sat)
			(get-model)
		'''
	}
	
	private def static simplifyMapping(List<Vertex> vertexList) {
		'''
		«FOR sc : vertexList SEPARATOR "\n"»
		(declare-const mapping«sc.id» platform)
		(assert (= mapping«sc.id» (mapping «sc.id»)))
		«ENDFOR»
		'''
	}
	
	private def static simplifyStartTimes(List<Vertex> vertexList) {
		'''
		«FOR sc : vertexList SEPARATOR "\n"»
		(declare-const start«sc.id» Int)
		(assert (= start«sc.id» (start «sc.id»)))
		«ENDFOR»
		'''
	}

	private def static generatePrecedenceConstraints(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG) {
		'''«FOR channel : softwareComponentDFG.edges SEPARATOR "\n"»(assert (<= (finish «softwareComponentDFG.
			getSource(channel).id») (start «softwareComponentDFG.getDest(channel).id»)))«ENDFOR»'''
	}

	private def static generateFinishAssertions(DirectedSparseMultigraph<Vertex, Edge> softwareComponentDFG) {
		'''«FOR sc : softwareComponentDFG.components SEPARATOR "\n"»(assert (<= (finish «sc.id») T1))«ENDFOR»'''
	}

	private def static generateDuration(DeploymentModel deploymentModel) {
		'''
			«FOR sc : deploymentModel.softwareComponentDFG.components SEPARATOR "\n"»(ite (= c «sc.id») «
				FOR pc : deploymentModel.processingComponentDFG.
				components.tail SEPARATOR " "»(ite (= x «pc.id») «sc.executionTime(pc, deploymentModel.wcet)»«ENDFOR» «sc.executionTime(
				deploymentModel.processingComponentDFG.components.head, deploymentModel.wcet)»«closedParanthesis(
					deploymentModel.processingComponentDFG.components.size - 1)»«ENDFOR» 0«closedParanthesis(
				deploymentModel.softwareComponentDFG.components.size + 1)»
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
