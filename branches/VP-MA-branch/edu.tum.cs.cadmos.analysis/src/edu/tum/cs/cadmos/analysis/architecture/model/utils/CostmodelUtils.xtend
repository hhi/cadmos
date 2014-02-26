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
	
	def static translateTransmissionLatency (Costmodel costmodel) {
		val transmissionLatencyMap = new HashMap()
		
		costmodel.mappings.filter[component.role == Role::SOFTWARE && port != null].forEach [
			val sc = component
			val p = port
			targetCosts.filter[component.role == Role::BUS].forEach [
				// TODO: add as value bus type if mapping on channels on bus also relevant
				val transmissionLatencyKey = new Pair(sc.name + '.' + p.name, "")
				costs.filter[key == ECosts::TRANSMISSION_LATENCY.name].forEach[
					if (transmissionLatencyMap.containsKey(transmissionLatencyKey)) {
						val maxValue = Math::max(transmissionLatencyMap.get(transmissionLatencyKey), value)
						transmissionLatencyMap.put(transmissionLatencyKey, maxValue)
					} else {
						transmissionLatencyMap.put(transmissionLatencyKey, value)
					}
				]
			]
		]
		
		return transmissionLatencyMap
	}
	
	def static translateTransmissionDuration (Costmodel costmodel) {
		val transmissionLatencyMap = new HashMap()
		
		costmodel.mappings.filter[component.role == Role::SOFTWARE && port != null].forEach [
			val sc = component
			val p = port
			targetCosts.filter[component.role == Role::BUS].forEach [
				// TODO: add as value bus type if mapping on channels on bus also relevant
				val transmissionLatencyKey = new Pair(sc.name + '.' + p.name, "")
				costs.filter[key == ECosts::TRANSMISSION_DURATION.name].forEach[
					if (transmissionLatencyMap.containsKey(transmissionLatencyKey)) {
						val maxValue = Math::max(transmissionLatencyMap.get(transmissionLatencyKey), value)
						transmissionLatencyMap.put(transmissionLatencyKey, maxValue)
					} else {
						transmissionLatencyMap.put(transmissionLatencyKey, value)
					}
				]
			]
		]
		
		return transmissionLatencyMap
	}
	
}