package edu.tum.cs.cadmos.analysis.ui.views.architecture;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.ui.CadmosUi;
import edu.tum.cs.cadmos.language.ui.ICadmosEditorSelectionChangedListener;

public class ArchitectureViewPart extends ViewPart {

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.views.architecture.ArchitectureViewPart"; //$NON-NLS-1$

	public ArchitectureViewPart() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));

		architectureControl = new ArchitectureControl(container, SWT.NONE);

		createActions();
		initializeToolBar();
		initializeMenu();

		CadmosUi.getInstance()
				.addListener(cadmosEditorSelectionChangedListener);
	}

	@Override
	public void dispose() {
		CadmosUi.getInstance().removeListener(
				cadmosEditorSelectionChangedListener);
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	@SuppressWarnings("unused")
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	@SuppressWarnings("unused")
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	private ICadmosEditorSelectionChangedListener cadmosEditorSelectionChangedListener = new ICadmosEditorSelectionChangedListener() {
		@Override
		public void selectionChanged(XtextEditor editor, EObject selectedObject) {
			final Component component = EcoreUtil2.getContainerOfType(
					selectedObject, Component.class);
			getArchitectureControl().setRootComponent(component);
			getArchitectureControl().setSelectedObject(selectedObject);
		};
	};

	private ArchitectureControl architectureControl;

	public ArchitectureControl getArchitectureControl() {
		return architectureControl;
	}
}
