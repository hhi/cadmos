package edu.tum.cs.cadmos.language.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import edu.tum.cs.cadmos.analysis.Architecture2Node;
import edu.tum.cs.cadmos.analysis.Node;
import edu.tum.cs.cadmos.language.cadmos.Component;

public class CadmosXtextModelSelectionChangedListener implements
		ISelectionChangedListener {

	private final XtextEditor editor;

	public CadmosXtextModelSelectionChangedListener(XtextEditor editor) {
		this.editor = editor;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSelection().isEmpty()
				|| !(event.getSelection() instanceof ITextSelection)) {
			return;
		}
		final ITextSelection selection = (ITextSelection) event.getSelection();
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
			@Override
			public void process(XtextResource resource) throws Exception {
				final IParseResult parseResult = resource.getParseResult();
				if (parseResult == null) {
					return;
				}
				final ICompositeNode rootNode = parseResult.getRootNode();
				final ILeafNode leafNode = NodeModelUtils.findLeafNodeAtOffset(
						rootNode, selection.getOffset());
				EObject selectedObject = NodeModelUtils
						.findActualSemanticObjectFor(leafNode);

				// FIXME: The following is for debugging only
				if (!(selectedObject instanceof Component)) {
					selectedObject = EcoreUtil2.getContainerOfType(
							selectedObject, Component.class);
					if (selectedObject == null) {
						return;
					}
				}
				final Component component = (Component) selectedObject;
				final Architecture2Node architecture2Node = new Architecture2Node(
						component);
				long t = System.nanoTime();
				final Node node = architecture2Node.translate();
				t = System.nanoTime() - t;
				System.out.println();
				System.out.println(node);
				System.out.println((t / 1000) / 1000.0 + "ms");
			}
		});
	}
}
