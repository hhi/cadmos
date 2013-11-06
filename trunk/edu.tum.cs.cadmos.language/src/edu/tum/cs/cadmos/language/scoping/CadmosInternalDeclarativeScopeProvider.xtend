package edu.tum.cs.cadmos.language.scoping

import com.google.inject.Inject
import edu.tum.cs.cadmos.language.utils.ComponentUtils
import org.eclipse.xtext.scoping.IScope
import edu.tum.cs.cadmos.language.cadmos.PortRef
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.EcoreUtil2
import edu.tum.cs.cadmos.language.cadmos.Component
import org.eclipse.xtext.scoping.Scopes
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider

/**
 * This is the internal &quot;single&quot; scope provider that is employed by
 * the other scope providers ({@link CadmosXbaseScopeProvider} and {@link CadmosXbaseBatchScopeProvider})
 * and it uses methods with signature
 * <code>IScope scope_[EClassName]_[EReferenceName](MyType context, EReference ref)</code>
 * to provide scopes.
 */
class CadmosInternalDeclarativeScopeProvider extends AbstractDeclarativeScopeProvider {

	@Inject extension ComponentUtils

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

}
