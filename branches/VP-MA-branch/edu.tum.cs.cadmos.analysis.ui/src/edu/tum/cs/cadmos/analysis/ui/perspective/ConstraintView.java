package edu.tum.cs.cadmos.analysis.ui.perspective;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.jface.action.Action;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ConstraintView extends ViewPart {

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.ConstraintView"; //$NON-NLS-1$
	private Action action;

	public ConstraintView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setImage(ResourceManager.getPluginImage("org.eclipse.pde.ui", "/icons/obj16/req_plugin_obj.gif"));
		
		Label lblRequirementX = new Label(composite, SWT.NONE);
		lblRequirementX.setImage(ResourceManager.getPluginImage("edu.tum.cs.cadmos.analysis.ui", "icons/architecture_browser.gif"));
		lblRequirementX.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblRequirementX.setText("Requirement X");
		
		Button btnOpen = new Button(composite, SWT.NONE);
		btnOpen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		btnOpen.setImage(ResourceManager.getPluginImage("org.eclipse.jdt.ui", "/icons/full/etool16/opentype.gif"));
		btnOpen.setText("Open...");
		
		Menu menu = new Menu(btnOpen);
		btnOpen.setMenu(menu);
		
		MenuItem mntmReq = new MenuItem(menu, SWT.NONE);
		mntmReq.setText("Req 01");
		
		MenuItem mntmReq_1 = new MenuItem(menu, SWT.NONE);
		mntmReq_1.setText("Req 02");
		
		MenuItem mntmReq_2 = new MenuItem(menu, SWT.CHECK);
		mntmReq_2.setText("Req 03");
		
		CheckboxTreeViewer checkboxTreeViewer = new CheckboxTreeViewer(container, SWT.BORDER);
		Tree tree = checkboxTreeViewer.getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(checkboxTreeViewer, SWT.NONE);
		TreeColumn trclmnName = treeViewerColumn.getColumn();
		trclmnName.setWidth(100);
		trclmnName.setText("Name");
		
		TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(checkboxTreeViewer, SWT.NONE);
		TreeColumn trclmnValue = treeViewerColumn_1.getColumn();
		trclmnValue.setWidth(100);
		trclmnValue.setText("Value");
		
		TreeItem trtmTest = new TreeItem(tree, SWT.NONE);
		trtmTest.setText("Test");
		
		TreeItem trtmAsdf = new TreeItem(tree, SWT.NONE);
		trtmAsdf.setText("asdf");
		
		TreeItem trtmFasdfasd = new TreeItem(tree, SWT.NONE);
		trtmFasdfasd.setText("fasdfasd");

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			action = new Action("New Action") {
			};
			action.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.eclipse.mylyn.tasks.ui", "/icons/etool16/open-repository-task.gif"));
		}
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
}
