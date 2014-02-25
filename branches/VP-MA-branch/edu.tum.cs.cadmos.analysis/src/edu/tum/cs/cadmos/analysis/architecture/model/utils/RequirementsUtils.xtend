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
		val periodicityRequirements = requirements.eContents.filter(PeriodicityRequirement)
		
		for (PeriodicityRequirement req : periodicityRequirements) {
			var List<String> componentList = periodicityMap.get(req.period)
			if (componentList == null) {
				componentList = new ArrayList();
			}
			componentList.add(req.component.name)
			
			periodicityMap.put(req.period, componentList)
		}
		
		periodicityMap
	}
	
	def static translateComponentRobustness(Requirements requirements) {
		val robustnessMap = new HashMap()
		val robustnessRequirements = requirements.eContents.filter(RobustnessRequirement)
		
		for (RobustnessRequirement req : robustnessRequirements) {
			val robustnessPair = new Pair (new Pair("finish " + req.fromEmbedding.name, req.fromEmbedding.component.name),
											new Pair ("start " + req.toEmbedding.name, req.toEmbedding.component.name)
			)
			val robustnessInterval = new Pair (req.robustness.fromValue, req.robustness.toValue)
			robustnessMap.put(robustnessPair, robustnessInterval)
		}
		
		robustnessMap
	}
	
	def static translateComponentLatency(Requirements requirements) {
		val latencyMap = new HashMap()
		val latencyRequirements = requirements.eContents.filter(LatencyRequirement)
		
		for (LatencyRequirement req : latencyRequirements) {
			val latencyPair = new Pair (new Pair(req.fromPort.ioType + req.fromEmbedding.name, req.fromEmbedding.component.name),
											new Pair (req.toPort.ioType + req.toEmbedding.name, req.toEmbedding.component.name)
			)
			val latencyInterval = new Pair (req.latency.fromValue, req.latency.toValue)
			latencyMap.put(latencyPair, latencyInterval)
		}
		
		latencyMap
	}
	
	private def static ioType (Port port) {
		var type = ""
		
		if (!port.inbound) {
			type = "finish "
		} else {
			// val list = port.eClass.EAllAttributes.filter[it.name.equals("inbound")]
			type = "start "
		}
		
		type
	}
	
}