package edu.tum.cs.cadmos.analysis.ui.perspective;


import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.wb.swt.ResourceManager;

import edu.tum.cs.cadmos.analysis.schedule.AssertionNameMapping;
import edu.tum.cs.cadmos.analysis.schedule.IUnsatCoreListener;
import edu.tum.cs.cadmos.analysis.ui.AnalysisUi;

public class ConstraintView extends ViewPart implements IUnsatCoreListener{

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.ConstraintView"; //$NON-NLS-1$
	private Action action;
	private TableViewer constraintViewer;
	
	
	Image SAT = AnalysisUi.getImageDescriptor("icons/sat.gif").createImage();
	Image UNSAT = AnalysisUi.getImageDescriptor("icons/unsat.gif").createImage();

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
		composite.setLayout(new GridLayout(4, false));
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

				executeZ3Script();
			}

			private void executeZ3Script() {
				// Obtain IServiceLocator implementer, e.g. from PlatformUI.getWorkbench():
				IServiceLocator serviceLocator = PlatformUI.getWorkbench();
				// or a site from within a editor or view:
				// IServiceLocator serviceLocator = getSite();

				ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);

				try  { 
				    // Lookup commmand with its ID
				    Command command = commandService.getCommand("edu.tum.cs.cadmos.analysis.ui.SoftwareDeploymentHandler");

				    // Optionally pass a ExecutionEvent instance, default no-param arg creates blank event
				    command.executeWithChecks(new ExecutionEvent());
				        
				} catch (Exception ex) {
				    
				    // Replace with real-world exception handling
				    ex.printStackTrace();
				}
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
		
		
		constraintViewer = new TableViewer(container, SWT.BORDER);
		Table tree = constraintViewer.getTable();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		constraintViewer.setContentProvider(new ArrayContentProvider());
		constraintViewer.setSorter(new ViewerSorter(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				// TODO Auto-generated method stub
				return super.compare(viewer, e1, e2);
			}
		});
		TableViewerColumn treeViewerColumn = new TableViewerColumn(constraintViewer, SWT.NONE);
		TableColumn trclmnName = treeViewerColumn.getColumn();
		trclmnName.setWidth(300);
		trclmnName.setText("Constraint descriptor");
		
		treeViewerColumn.setLabelProvider(new ColumnLabelProvider(){
			
		});
		
		TableViewerColumn treeViewerColumn_1 = new TableViewerColumn(constraintViewer, SWT.NONE);
		TableColumn trclmnValue = treeViewerColumn_1.getColumn();
		trclmnValue.setWidth(100);
		trclmnValue.setText("Status");
		
		treeViewerColumn_1.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				String s = (String) element;
				if(AssertionNameMapping.SINGLETON.isUnsat(s)){
					return "UNSAT";
				}
				return "SAT";
			}
			@Override
			public Image getImage(Object element) {
				String s = (String) element;
				if(AssertionNameMapping.SINGLETON.isUnsat(s)){
					return UNSAT;
				}
				return SAT;
			}
		});
		
		
		

		createActions();
		initializeToolBar();
		initializeMenu();
		addDataListeners();
	}

	private void addDataListeners() {
		AssertionNameMapping.registerListener(this);
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

	@Override
	public void notifyUnsatCoreChange() {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				constraintViewer.setInput(AssertionNameMapping.getContents());
				constraintViewer.refresh();
			}
		});
	}
	
	@Override
	public void dispose() {
		AssertionNameMapping.unregisterListener(this);
	}
}
