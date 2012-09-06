package edu.tum.cs.cadmos.language.ui.quickfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.Issue;

import edu.tum.cs.cadmos.common.StringUtils;
import edu.tum.cs.cadmos.language.cadmos.CadmosFactory;
import edu.tum.cs.cadmos.language.cadmos.CadmosPackage;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Model;

public class CadmosQuickfixProvider extends DefaultQuickfixProvider {

	// @Fix(MyJavaValidator.INVALID_NAME)
	// public void capitalizeName(final Issue issue, IssueResolutionAcceptor
	// acceptor) {
	// acceptor.accept(issue, "Capitalize name", "Capitalize the name.",
	// "upcase.png", new IModification() {
	// public void apply(IModificationContext context) throws
	// BadLocationException {
	// IXtextDocument xtextDocument = context.getXtextDocument();
	// String firstLetter = xtextDocument.get(issue.getOffset(), 1);
	// xtextDocument.replace(issue.getOffset(), 1, firstLetter.toUpperCase());
	// }
	// });
	// }

	@Override
	protected EReference getUnresolvedEReference(Issue issue, EObject target) {
		return super.getUnresolvedEReference(issue, target);
	}

	@Override
	public void createLinkingIssueResolutions(final Issue issue,
			final IssueResolutionAcceptor issueResolutionAcceptor) {
		super.createLinkingIssueResolutions(issue, issueResolutionAcceptor);
		final IModificationContext modificationContext = getModificationContextFactory()
				.createModificationContext(issue);
		final IXtextDocument xtextDocument = modificationContext
				.getXtextDocument();
		if (xtextDocument == null) {
			return;
		}
		xtextDocument.readOnly(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource state) throws Exception {
				final EObject target = state.getEObject(issue.getUriToProblem()
						.fragment());
				final EReference reference = getUnresolvedEReference(issue,
						target);
				if (reference == null) {
					return;
				}
				final String missingTypeName = xtextDocument.get(
						issue.getOffset(), issue.getLength());
				if (target instanceof Embedding
						&& reference
								.equals(CadmosPackage.Literals.EMBEDDING__COMPONENT)) {
					createMissingEmbeddingComponentResolutions(issue,
							issueResolutionAcceptor, (Embedding) target,
							missingTypeName);
				}
			}
		});
	}

	protected void createMissingEmbeddingComponentResolutions(Issue issue,
			IssueResolutionAcceptor issueResolutionAcceptor,
			final Embedding embedding, final String missingTypeName) {
		final String label = "Create component '" + missingTypeName + "'";
		issueResolutionAcceptor.accept(issue, label, label, null,
				new ISemanticModification() {
					@Override
					public void apply(EObject element,
							IModificationContext context) throws Exception {
						final Model model = EcoreUtil2.getContainerOfType(
								embedding, Model.class);
						final Component component = EcoreUtil2
								.getContainerOfType(embedding, Component.class);
						final int index = model.getElements()
								.indexOf(component);
						final Component createdComponent = CadmosFactory.eINSTANCE
								.createComponent();
						createdComponent.setName(missingTypeName);
						model.getElements().add(index + 1, createdComponent);
						if (embedding.getName() == null
								|| embedding.getName().length() == 0) {
							embedding.setName(StringUtils
									.toFeatureName(missingTypeName));
						}
					}
				});
	}

}
