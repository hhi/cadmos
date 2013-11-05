package edu.tum.cs.cadmos.analysis.ui.views.livearchitecture;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.xtext.ui.editor.XtextEditor;

import edu.tum.cs.cadmos.analysis.ui.controls.ArchitectureDisplay;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Model;
import edu.tum.cs.cadmos.language.ui.CadmosUi;
import edu.tum.cs.cadmos.language.ui.ICadmosModelSelectionChangedListener;

public class LiveArchitectureViewPart extends ViewPart {

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.views.livearchitecture.LiveArchitectureViewPart"; //$NON-NLS-1$

	public LiveArchitectureViewPart() {
	}

	protected ICadmosModelSelectionChangedListener selectionChangedListener = new ICadmosModelSelectionChangedListener() {
		@Override
		public void selectionChanged(XtextEditor editor, Model model,
				Component selectedComponent, EObject selectedObject) {
			getArchitectureDisplay().setComponent(selectedComponent);
		}
	};

	private ArchitectureDisplay architectureDisplay;

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		{
			architectureDisplay = new ArchitectureDisplay(container,
					SWT.DOUBLE_BUFFERED);
			architectureDisplay.setBackground(SWTResourceManager
					.getColor(SWT.COLOR_WHITE));
		}

		createActions();
		initializeToolBar();
		initializeMenu();

		CadmosUi.addListener(selectionChangedListener);
	}

	@Override
	public void dispose() {
		CadmosUi.removeListener(selectionChangedListener);

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
	private void initializeToolBar() {
		final IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		final IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	public ArchitectureDisplay getArchitectureDisplay() {
		return architectureDisplay;
	}
}
