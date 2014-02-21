package edu.tum.cs.cadmos.analysis.architecture.model.utils

import edu.tum.cs.cadmos.language.cadmos.PeriodicityRequirement
import edu.tum.cs.cadmos.language.cadmos.Requirements
import java.util.ArrayList
import java.util.HashMap
import java.util.List

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
	
}