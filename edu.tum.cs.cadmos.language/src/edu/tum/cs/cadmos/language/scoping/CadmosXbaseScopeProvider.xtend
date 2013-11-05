package edu.tum.cs.cadmos.language.scoping

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.xbase.annotations.scoping.XbaseWithAnnotationsScopeProvider

/**
 * This scope provider is usd by Xtext and Xbase for resolving <b>completion proposals</b> and 
 * delegates internally to a {@link CadmosInternalDeclarativeScopeProvider}.
 */
class CadmosXbaseScopeProvider extends XbaseWithAnnotationsScopeProvider {

	@Inject CadmosInternalDeclarativeScopeProvider internalScopeProvider

	override getScope(EObject context, EReference ref) {
		internalScopeProvider.getScope(context, ref)
	}

}
