package edu.tum.cs.cadmos.analysis.architecture.model.utils

import edu.tum.cs.cadmos.language.cadmos.PeriodicityRequirement
import edu.tum.cs.cadmos.language.cadmos.Requirements
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import edu.tum.cs.cadmos.language.cadmos.RobustnessRequirement
import edu.tum.cs.cadmos.language.cadmos.LatencyRequirement
import edu.tum.cs.cadmos.language.cadmos.Port

class RequirementsUtils {
	
	def static translateComponentPeriodicity(Requirements requirements) {
		val periodicityMap = new HashMap()
		requirements.eContents.filter(PeriodicityRequirement).forEach[
			var List<String> componentList = periodicityMap.get(it.period)
			if (componentList == null) {
				componentList = new ArrayList();
			}
			componentList.add(it.component.name)
			
			periodicityMap.put(it.period, componentList)		
		]
		
		periodicityMap
	}
	
	def static translateComponentRobustness(Requirements requirements) {
		val robustnessMap = new HashMap()
		
		requirements.eContents.filter(RobustnessRequirement).forEach[
			val robustnessPair = new Pair (new Pair("finish " + it.fromEmbedding.name, it.fromEmbedding.component.name),
											new Pair ("start " + it.toEmbedding.name, it.toEmbedding.component.name)
			)
			val robustnessInterval = new Pair (it.robustness.fromValue, it.robustness.toValue)
			robustnessMap.put(robustnessPair, robustnessInterval)
		]
		
		robustnessMap
	}
	
	def static translateComponentLatency(Requirements requirements) {
		val latencyMap = new HashMap()
		
		requirements.eContents.filter(LatencyRequirement).forEach[
			val latencyPair = new Pair (new Pair(it.fromPort.ioType + it.fromEmbedding.name, it.fromEmbedding.component.name),
											new Pair (it.toPort.ioType + it.toEmbedding.name, it.toEmbedding.component.name)
			)
			val latencyInterval = new Pair (it.latency.fromValue, it.latency.toValue)
			latencyMap.put(latencyPair, latencyInterval)
		]
		
		latencyMap
	}
	
	private def static ioType (Port port) {
		var type = ""
		
		if (!port.inbound) {
			type = "finish "
		} else {
			type = "start "
		}
		
		type
	}
	
}