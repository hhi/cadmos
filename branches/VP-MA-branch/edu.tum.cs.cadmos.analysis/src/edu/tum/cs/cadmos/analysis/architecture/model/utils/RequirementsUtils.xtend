package edu.tum.cs.cadmos.analysis.architecture.model.utils

import edu.tum.cs.cadmos.language.cadmos.PeriodicityRequirement
import edu.tum.cs.cadmos.language.cadmos.Requirements
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import edu.tum.cs.cadmos.language.cadmos.RobustnessRequirement

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
			val robustnessPair = new Pair (new Pair(req.fromEmbedding.name, req.fromEmbedding.component.name),
											new Pair (req.toEmbedding.name, req.toEmbedding.component.name)
			)
			val robustnessInterval = new Pair (req.robustness.fromValue, req.robustness.toValue)
			robustnessMap.put(robustnessPair, robustnessInterval)
		}
		
		robustnessMap
	}
	
}