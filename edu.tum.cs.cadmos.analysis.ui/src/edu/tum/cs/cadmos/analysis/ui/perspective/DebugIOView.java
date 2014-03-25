package edu.tum.cs.cadmos.analysis.ui.perspective;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import edu.tum.cs.cadmos.analysis.schedule.IOOutput;
import edu.tum.cs.cadmos.analysis.schedule.IOOutputListener;

public class DebugIOView extends ViewPart implements IOOutputListener{

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.DebugIO"; //$NON-NLS-1$
	private TextViewer textViewer;

	public DebugIOView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		{
			textViewer = new TextViewer(container, SWT.BORDER);
			StyledText styledText = textViewer.getTextWidget();
			styledText.setAlwaysShowScrollBars(true);
			styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}

		createActions();
		initializeToolBar();
		initializeMenu();
		addDataListeners();
	}

	private void addDataListeners() {
		IOOutput.registerListener(this);
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
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void notifyIOOutputChange() {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				StyledText styledText = textViewer.getTextWidget();
				styledText.setText(IOOutput.getIOContents());
				styledText.setTopIndex(styledText.getLineCount() - 1);
				
			}
		});
	}
	
	@Override
	public void dispose() {
		IOOutput.unregisterListener(this);
	}

}
