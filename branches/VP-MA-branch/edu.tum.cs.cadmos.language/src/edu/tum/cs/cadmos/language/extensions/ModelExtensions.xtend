package edu.tum.cs.cadmos.language.extensions

import edu.tum.cs.cadmos.language.cadmos.Channel
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.ComponentProperty
import edu.tum.cs.cadmos.language.cadmos.Costmodel
import edu.tum.cs.cadmos.language.cadmos.Embedding
import edu.tum.cs.cadmos.language.cadmos.Mapping
import edu.tum.cs.cadmos.language.cadmos.Model
import edu.tum.cs.cadmos.language.cadmos.Port
import edu.tum.cs.cadmos.language.cadmos.Cost
import org.eclipse.xtext.EcoreUtil2
import edu.tum.cs.cadmos.language.cadmos.TargetCost
import edu.tum.cs.cadmos.language.ECosts

class ModelExtensions {

	def components(Model m) {
		m.elements.filter(Component)
	}

	def costmodels(Model m) {
		m.elements.filter(Costmodel)
	}

	def ports(Component c) {
		c.features.filter(Port)
	}
	
	
	def getComponent(Port p){
		p.eContainer as Component		
	}
	
	def inboundPorts(Component c) {
		c.features.filter(Port).filter[inbound]
	}
	
	def getTrailingChannels(Port p){
		(p.eContainer as Component).channels.filter[it.src.port == p]
	}
	
	def connectsToSibling(Channel ch){
		if (ch.src.embedding == null || ch.snk.embedding == null) {
			return false
		}
		return true		
	}
	
	def isAtomic(Component c) {
		c.embeddings.empty
	}
	
	def connectsToParent(Channel ch){
		return ch.src.embedding != null && ch.snk.embedding == null
	}
	
	def connectsToChild(Channel ch){
		return ch.src.embedding == null && ch.snk.embedding != null
	}	

	def embeddings(Component c) {
		c.features.filter(Embedding)
	}

	def channels(Component c) {
		c.features.filter(Channel)
	}

	def componentProperties(Component c) {
		c.features.filter(ComponentProperty)
	}

	def isComponentMapping(Mapping m) {
		m.port == null
	}

	def isPortMapping(Mapping m) {
		!isComponentMapping(m)
	}

	def qualifiedName(Mapping m) {
		val componentName = if(m.component != null) m.component.name else "<null>"
		componentName + if(m.portMapping) "." + m.port.name else ""
	}

	def qualifiedKey(Cost c) {
		val targetCost = EcoreUtil2.getContainerOfType(c, TargetCost)
		val mapping = EcoreUtil2.getContainerOfType(targetCost, Mapping)
		val targetComponentName = if(targetCost.component != null) targetCost.component.name else "<null>"
		"(" + mapping.qualifiedName + "→" + targetComponentName + ")." + c.key
	}

	def costFor(Costmodel costmodel, Component swc, Component plc, ECosts cost) {
		costmodel.costsFor(swc, plc).findFirst[key == cost.name]
	}

	def costsFor(Costmodel costmodel, Component swc, Component plc) {
		costmodel.mappings.filter[component == swc].map[targetCosts].flatten.filter[component == plc].map[costs].flatten
	}

}
