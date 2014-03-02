/*
 * generated by Xtext
 */
package edu.tum.cs.cadmos.language.scoping

import com.google.inject.Inject
import edu.tum.cs.cadmos.language.cadmos.Component
import edu.tum.cs.cadmos.language.cadmos.Mapping
import edu.tum.cs.cadmos.language.cadmos.PortRef
import edu.tum.cs.cadmos.language.extensions.ModelExtensions
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.scoping.IScope
import org.eclipse.xtext.scoping.Scopes
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider
import edu.tum.cs.cadmos.language.cadmos.RobustnessRequirement
import edu.tum.cs.cadmos.language.cadmos.LatencyRequirement
import edu.tum.cs.cadmos.language.cadmos.ResourceSchedule
import edu.tum.cs.cadmos.language.cadmos.Task

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation.html#scoping
 * on how and when to use it 
 *
 */
class CadmosScopeProvider extends AbstractDeclarativeScopeProvider {

	@Inject extension ModelExtensions

	def IScope scope_PortRef_port(PortRef portRef, EReference ref) {
		val embedding = portRef.embedding
		val component = if (embedding == null) {
				EcoreUtil2.getContainerOfType(portRef, Component);
			} else {
				embedding.component;
			}
		if (component == null) {
			return IScope.NULLSCOPE;
		}
		return Scopes.scopeFor(component.ports);
	}
	
	def IScope scope_Mapping_port(Mapping mapping, EReference ref) {
		if (mapping.component == null) {
			return IScope.NULLSCOPE;
		}
		return Scopes.scopeFor(mapping.component.ports);
	}
	
	def IScope scope_RobustnessRequirement_fromEmbedding(RobustnessRequirement robustnessRequirement, EReference ref) {
		val component = robustnessRequirement.fromComponent
		
		if (component == null) {
			return IScope.NULLSCOPE;
		}
		return Scopes.scopeFor(component.embeddings);
	}
	
	def IScope scope_RobustnessRequirement_toEmbedding(RobustnessRequirement robustnessRequirement, EReference ref) {
		val component = robustnessRequirement.toComponent
		
		if (component == null) {
			return IScope.NULLSCOPE;
		}
		return Scopes.scopeFor(component.embeddings);
	}
	
	def IScope scope_LatencyRequirement_fromEmbedding(LatencyRequirement latencyRequirement, EReference ref) {
		val component = latencyRequirement.fromComponent
		
		if (component == null) {
			return IScope.NULLSCOPE;
		}
		return Scopes.scopeFor(component.embeddings);
	}
	
	def IScope scope_LatencyRequirement_toEmbedding(LatencyRequirement latencyRequirement, EReference ref) {
		val component = latencyRequirement.toComponent
		
		if (component == null) {
			return IScope.NULLSCOPE;
		}
		return Scopes.scopeFor(component.embeddings);
	}
	
	def IScope scope_LatencyRequirement_fromPort(LatencyRequirement latencyRequirement, EReference ref) {
		val fromEmbedded = latencyRequirement.fromEmbedding
		if (fromEmbedded == null) {
			val fromComponent = latencyRequirement.fromComponent
			if (fromComponent == null) {
				return IScope.NULLSCOPE;	
			}
			return Scopes.scopeFor(fromComponent.ports);
		}
		return Scopes.scopeFor(fromEmbedded.component.ports)
	}
	
	def IScope scope_LatencyRequirement_toPort(LatencyRequirement latencyRequirement, EReference ref) {
		val toEmbedded = latencyRequirement.toEmbedding
		if (toEmbedded == null) {
			val toComponent = latencyRequirement.toComponent
			if (toComponent == null) {
				return IScope.NULLSCOPE;	
			}
			return Scopes.scopeFor(toComponent.ports);
		}
		return Scopes.scopeFor(toEmbedded.component.ports)
	}
	
	def IScope scope_ResourceSchedule_embedding(ResourceSchedule resourceSchedule, EReference ref) {
		val component = resourceSchedule.resource
		
		if (component == null) {
			return IScope.NULLSCOPE;
		}
		return Scopes.scopeFor(component.embeddings);
	}
	
	def IScope scope_Task_embedding(Task task, EReference ref) {
		val component = task.component
		
		if (component == null) {
			return IScope.NULLSCOPE;
		}
		return Scopes.scopeFor(component.embeddings);
	}

}
