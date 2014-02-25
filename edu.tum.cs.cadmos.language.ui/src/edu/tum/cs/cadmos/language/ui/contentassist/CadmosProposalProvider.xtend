/*
 * generated by Xtext
 */
package edu.tum.cs.cadmos.language.ui.contentassist

import edu.tum.cs.cadmos.language.ui.contentassist.AbstractCadmosProposalProvider
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor
import edu.tum.cs.cadmos.language.EComponentProperties
import edu.tum.cs.cadmos.language.cadmos.ComponentProperty
import edu.tum.cs.cadmos.language.EBusTypes
import edu.tum.cs.cadmos.language.ECosts
import edu.tum.cs.cadmos.language.EAssumptions

/**
 * see http://www.eclipse.org/Xtext/documentation.html#contentAssist on how to customize content assistant
 */
class CadmosProposalProvider extends AbstractCadmosProposalProvider {

	override completeComponentProperty_Name(EObject model, Assignment assignment, ContentAssistContext context,
		ICompletionProposalAcceptor acceptor) {
		super.completeComponentProperty_Name(model, assignment, context, acceptor)
		for (p : EComponentProperties.values) {
			acceptor.accept(createCompletionProposal(p.name, context));
		}
	}

	override completeComponentProperty_Value(EObject model, Assignment assignment, ContentAssistContext context,
		ICompletionProposalAcceptor acceptor) {
		super.completeComponentProperty_Value(model, assignment, context, acceptor)
		switch model {
			ComponentProperty: {
				switch model.name {
					case EComponentProperties.BUS_TYPE.name: {
						for (t : EBusTypes.values) {
							acceptor.accept(createCompletionProposal("'" + t.name + "'", context));
						}
					}
				}
			}
		}
	}

	override completeCost_Key(EObject model, Assignment assignment, ContentAssistContext context,
		ICompletionProposalAcceptor acceptor) {
		super.completeCost_Key(model, assignment, context, acceptor)
		for (c : ECosts.values) {
			acceptor.accept(createCompletionProposal(c.name, context));
		}
	}
	
	override completeAssumption_Value(EObject model, Assignment assignment, ContentAssistContext context,
		ICompletionProposalAcceptor acceptor) {
		super.completeCost_Key(model, assignment, context, acceptor)
		for (a : EAssumptions.values) {
			acceptor.accept(createCompletionProposal(a.name, context));
		}
	}

}
