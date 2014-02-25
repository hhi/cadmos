package edu.tum.cs.cadmos.analysis.architecture.model.utils

import edu.tum.cs.cadmos.language.cadmos.Costmodel
import java.util.HashMap
import edu.tum.cs.cadmos.language.cadmos.Role
import edu.tum.cs.cadmos.language.ECosts

class CostmodelUtils {
	
	def static translateWCET (Costmodel costmodel) {
		val wcetMap = new HashMap()
		
		costmodel.mappings.filter[component.role == Role::SOFTWARE && port == null].forEach [
			val sc = component
			targetCosts.filter[component.role == Role::PROCESSING].forEach [
				val wcetKey = new Pair(sc.name, it.component.name)
				costs.filter[key == ECosts::WCET.name].forEach[
					wcetMap.put(wcetKey, value)
				]
			]
		]
		
		return wcetMap
	}
	
}