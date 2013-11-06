package edu.tum.cs.cadmos.language.jvmmodel

import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.common.types.JvmField
import org.eclipse.xtext.common.types.JvmTypeReference
import org.eclipse.xtext.xbase.XExpression
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder
import org.eclipse.xtext.common.types.JvmVisibility
import java.util.List

class InferrerUtils {

	@Inject extension JvmTypesBuilder

	def JvmField toField(EObject obj, String name, JvmTypeReference typeRef, boolean isFinal) {
		obj.toField(name, typeRef, isFinal, null)
	}

	def JvmField toField(EObject obj, String name, JvmTypeReference typeRef, boolean isFinal,
		XExpression initializerExpr) {
		obj.toField(name, typeRef) [
			documentation = obj.documentation
			final = isFinal
			initializer = initializerExpr
			visibility = JvmVisibility::PROTECTED
		]
	}
	
	
}
