package edu.tum.cs.cadmos.analysis.architecture.model.utils

import edu.tum.cs.cadmos.language.cadmos.Costmodel
import edu.tum.cs.cadmos.language.cadmos.Mapping
import java.util.HashMap
import edu.tum.cs.cadmos.language.cadmos.Role
import edu.tum.cs.cadmos.language.cadmos.TargetCost
import edu.tum.cs.cadmos.language.cadmos.Cost

class CostmodelUtils {
	
	def static translateWCET (Costmodel costmodel) {
		val wcetMap = new HashMap()
		//<Pair<Vertex,Vertex>, Integer>() 
		for (Mapping mapping : costmodel.mappings) {
			val sc = mapping.component
			
			if (sc.role == Role::SOFTWARE && mapping.port == null) {
				for (TargetCost targetCost : mapping.targetCosts) {
					val processingType = targetCost.component
					
					if (processingType.role == Role::PROCESSING) {
						val wcetKey = new Pair(sc.name, processingType.name)
						
						for (Cost cost : targetCost.costs) {
							if (cost.key.toLowerCase.equals("wcet")) {
								val wcetValue = cost.value;
								wcetMap.put(wcetKey, wcetValue)
							}	
						}
					}
				}
			}
		}
		
		wcetMap
	}
	
}