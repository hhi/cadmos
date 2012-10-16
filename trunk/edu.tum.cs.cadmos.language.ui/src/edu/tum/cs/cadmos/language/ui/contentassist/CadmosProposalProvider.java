package edu.tum.cs.cadmos.language.ui.contentassist;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import edu.tum.cs.cadmos.common.StringUtils;
import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.CadmosPackage;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Parameter;
import edu.tum.cs.cadmos.language.cadmos.Port;

/**
 * see
 * http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on
 * how to customize content assistant
 */
public class CadmosProposalProvider extends AbstractCadmosProposalProvider {

	@Override
	public void completeEmbedding_Name(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		super.completeEmbedding_Name(model, assignment, context, acceptor);
		final Embedding embedding = (Embedding) model;
		final String name = embedding.getComponent().getName();
		acceptor.accept(createCompletionProposal(
				StringUtils.toFeatureName(name), context));
		acceptor.accept(createCompletionProposal(
				StringUtils.toCCAbbreviatedFeatureName(name), context));
	}

	@Override
	public void complete_PortRef(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		super.complete_PortRef(model, ruleCall, context, acceptor);
		final IScope scope = getScopeProvider().getScope(model,
				CadmosPackage.eINSTANCE.getPortRef_Embedding());
		final Set<Embedding> embeddings = new LinkedHashSet<>();
		for (final IEObjectDescription description : scope.getAllElements()) {
			embeddings.add((Embedding) description.getEObjectOrProxy());
		}
		for (final Embedding embedding : embeddings) {
			final Component component = embedding.getComponent();
			for (final Port port : ModelUtils.getPorts(component)) {
				final String proposal = embedding.getName() + "."
						+ port.getName();
				acceptor.accept(createCompletionProposal(proposal, context));
			}
		}
	}

	@Override
	public void completeEmbedding_ParameterValues(EObject model,
			Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		super.completeEmbedding_ParameterValues(model, assignment, context,
				acceptor);
		final Embedding embedding = (Embedding) model;
		final Component component = embedding.getComponent();
		if (component == null) {
			return;
		}
		final Component containerComponent = EcoreUtil2.getContainerOfType(
				embedding, Component.class);
		for (final Parameter containerComponentParameter : containerComponent
				.getParameters()) {
			final String nameProposal = containerComponentParameter.getName();
			acceptor.accept(createCompletionProposal(nameProposal, context));
		}
	}
}
