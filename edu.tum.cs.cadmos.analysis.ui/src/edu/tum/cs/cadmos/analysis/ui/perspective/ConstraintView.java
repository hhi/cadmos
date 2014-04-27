package edu.tum.cs.cadmos.analysis.ui.perspective;


import java.util.HashSet;
import java.util.Random;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.wb.swt.ResourceManager;

import edu.tum.cs.cadmos.analysis.schedule.AssertionNameMapping;
import edu.tum.cs.cadmos.analysis.schedule.AssertionPrefixes;
import edu.tum.cs.cadmos.analysis.schedule.IUnsatCoreListener;
import edu.tum.cs.cadmos.analysis.ui.AnalysisUi;
import edu.tum.cs.cadmos.analysis.ui.commons.ModelElementLinker;
import edu.tum.cs.cadmos.analysis.ui.constraints.AssertionTypeFilter;
import edu.tum.cs.cadmos.analysis.ui.constraints.ConstraintViewSets;
import edu.tum.cs.cadmos.analysis.ui.constraints.SATFilter;

public class ConstraintView extends ViewPart implements IUnsatCoreListener{

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.ConstraintView"; //$NON-NLS-1$
	private Action action;
	private TableViewer constraintViewer;
	
	
	
	
	Image RELAX= AnalysisUi.getImageDescriptor("icons/relax.gif").createImage();
	Image SAT = AnalysisUi.getImageDescriptor("icons/sat.gif").createImage();
	Image UNSAT = AnalysisUi.getImageDescriptor("icons/unsat.gif").createImage();
	Image UNDEFINED = AnalysisUi.getImageDescriptor("icons/undef.gif").createImage();
	private Menu menu_sat;
	private Button btnSatFilter;
	private Menu menu_type;
	private Menu menu_subs;
	private Button btnTypeFilter;
	private Button btnSubsystemFilter;

	public ConstraintView() {
		setTitleImage(ResourceManager.getPluginImage("edu.tum.cs.cadmos.analysis.ui", "icons/constraints.gif"));
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
		
		Button btnAddViewInstance = new Button(composite, SWT.NONE);
		btnAddViewInstance.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView(ID,Math.random()+"",IWorkbenchPage.VIEW_CREATE);
				} catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		btnAddViewInstance.setText("Add View Instance");
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("\n");
		
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

		btnOpen.setImage(ResourceManager.getPluginImage("edu.tum.cs.cadmos.analysis.ui", "icons/runz32.gif"));
		btnOpen.setText("Run Z3 calculation");
		
		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new GridLayout(4, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label lblFilters = new Label(composite_1, SWT.NONE);
		lblFilters.setText("Filters:");
		
		btnSatFilter = new Button(composite_1, SWT.NONE);
		btnSatFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (menu_sat.getItemCount() == 0)
					return;
				Point pt = btnSatFilter.toDisplay(new Point(e.x, e.y));
				menu_sat.setLocation(pt);
				menu_sat.setVisible(true);
			}
		});
		btnSatFilter.setText("Satisfaction");
		
		menu_sat = new Menu(btnSatFilter);
		btnSatFilter.setMenu(menu_sat);
		
		MenuItem mntmAny = new MenuItem(menu_sat, SWT.RADIO);
		mntmAny.setSelection(true);
		mntmAny.setText("any");
		mntmAny.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean checked = ((MenuItem)e.widget).getSelection();
				ConstraintViewSets.SINGLETON.adjustFilter(ConstraintView.this, SATFilter.ANY, !checked);
				if(checked){
					btnSatFilter.setText("any");
				}
			}
		});
		
		MenuItem mntmSat = new MenuItem(menu_sat, SWT.RADIO);
		mntmSat.setText("SAT");
		mntmSat.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean checked = ((MenuItem)e.widget).getSelection();
				ConstraintViewSets.SINGLETON.adjustFilter(ConstraintView.this, SATFilter.SAT, !checked);
				if(checked){
					btnSatFilter.setText("SAT");
				}
			}
		});
		
		MenuItem mntmUnsat = new MenuItem(menu_sat, SWT.RADIO);
		mntmUnsat.setText("UNSAT");
		mntmUnsat.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean checked = ((MenuItem)e.widget).getSelection();
				ConstraintViewSets.SINGLETON.adjustFilter(ConstraintView.this, SATFilter.UNSAT, !checked);
				if(checked){
					btnSatFilter.setText("UNSAT");
				}
			}
		});
		
		MenuItem mntmRelax = new MenuItem(menu_sat, SWT.RADIO);
		mntmRelax.setText("RELAX");
		mntmRelax.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean checked = ((MenuItem)e.widget).getSelection();
				ConstraintViewSets.SINGLETON.adjustFilter(ConstraintView.this, SATFilter.RELAX, !checked);
				if(checked){
					btnSatFilter.setText("RELAX");
				}
			}
		});
		
		btnTypeFilter = new Button(composite_1, SWT.NONE);
		btnTypeFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (menu_type.getItemCount() == 0)
					return;
				Point pt = btnTypeFilter.toDisplay(new Point(e.x, e.y));
				menu_type.setLocation(pt);
				menu_type.setVisible(true);
			}
		});
		btnTypeFilter.setText("Types");
		
		menu_type = new Menu(btnTypeFilter);
		btnTypeFilter.setMenu(menu_type);
		
		fillTypeMenu();
		
		btnSubsystemFilter = new Button(composite_1, SWT.NONE);
		btnSubsystemFilter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (menu_subs.getItemCount() == 0)
					return;
				Point pt = btnSubsystemFilter.toDisplay(new Point(e.x, e.y));
				menu_subs.setLocation(pt);
				menu_subs.setVisible(true);
			}
		});
		btnSubsystemFilter.setText("Subsystem");
		
		menu_subs = new Menu(btnSubsystemFilter);
		btnSubsystemFilter.setMenu(menu_subs);
		
		MenuItem mntmTopLevel = new MenuItem(menu_subs, SWT.RADIO);
		mntmTopLevel.setSelection(true);
		mntmTopLevel.setText("Top Level");

		MenuItem mntmReader = new MenuItem(menu_subs, SWT.RADIO);
		mntmReader.setText("+ reader");
		
		MenuItem mntmF = new MenuItem(menu_subs, SWT.RADIO);
		mntmF.setText("+ f1");
		
		MenuItem mntmF_1 = new MenuItem(menu_subs, SWT.RADIO);
		mntmF_1.setText("+ f2");
		
		MenuItem mntmF_2 = new MenuItem(menu_subs, SWT.RADIO);
		mntmF_2.setText("+ f3");
		
		MenuItem mntmWriter = new MenuItem(menu_subs, SWT.RADIO);
		mntmWriter.setText("+ writer");
		
		
		constraintViewer = new TableViewer(container, SWT.BORDER);
		Table tree = constraintViewer.getTable();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		constraintViewer.setContentProvider(new ArrayContentProvider());
		constraintViewer.setSorter(new ViewerSorter(){
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				return super.compare(viewer, e1, e2);
			}
		});
		
		constraintViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				String s = (String) ((IStructuredSelection)event.getSelection()).getFirstElement();
//				ModelElementLinker.INSTANCE.findEmbedding("writer");
//				for(String sub : s.split("_")){
//					ModelElementLinker.INSTANCE.findEmbedding(sub);
//				}
				//FIXME reactivate
				if(AssertionNameMapping.SINGLETON.isRelax(s)){
					AssertionNameMapping.SINGLETON.removeRelax(s);
				} else {
					AssertionNameMapping.SINGLETON.addRelax(s);
				}
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
				if(AssertionNameMapping.SINGLETON.isRelax(s)){
					return "RELAX";
				}
				if(AssertionNameMapping.SINGLETON.isUnsat(s)){
					return "UNSAT";
				}
				if(AssertionNameMapping.SINGLETON.isSat(s)){
					return "     SAT";
				}
				return "undefined";
			}
			@Override
			public Image getImage(Object element) {
				String s = (String) element;
				if(AssertionNameMapping.SINGLETON.isRelax(s)){
					return RELAX;
				}
				if(AssertionNameMapping.SINGLETON.isUnsat(s)){
					return UNSAT;
				} 
				if(AssertionNameMapping.SINGLETON.isSat(s)){
					return SAT;
				} 
				return UNDEFINED;
			}
		});
		
		
		createActions();
		initializeToolBar();
		initializeMenu();
		addDataListeners();
		notifyUnsatCoreChange();
	}

	private void fillTypeMenu() {
//		MenuItem all = new MenuItem(menu_type, SWT.PUSH);
//		all.setText("ALL");
//		all.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				for(int i=2; i<menu_type.getItems().length; i++){
//					menu_type.getItem(i).setSelection(true);
//				}
//			}
//		});
//		MenuItem none = new MenuItem(menu_type, SWT.PUSH);
//		none.setText("none");
//		none.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				for(int i=2; i<menu_type.getItems().length; i++){
//					menu_type.getItem(i).setSelection(false);
//				}
//			}
//		});
		
		
		for(final AssertionTypeFilter atf : AssertionTypeFilter.getAll()){
			MenuItem item = new MenuItem(menu_type, SWT.CHECK);
			item.setText(atf.toString());
			item.setSelection(true);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean checked = ((MenuItem)e.widget).getSelection();
					ConstraintViewSets.SINGLETON.adjustFilter(ConstraintView.this, atf, checked);
				}
			});
			
		}
		
		
		
	}

	private void addDataListeners() {
		ConstraintViewSets.SINGLETON.registerView(this);
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
//				constraintViewer.setInput(AssertionNameMapping.getContents());
//				System.out.println(ConstraintView.this);
//				System.out.println(constraintViewer);
//				System.out.println(ConstraintViewSets.SINGLETON.getContents(ConstraintView.this));
				constraintViewer.setInput(ConstraintViewSets.SINGLETON.getContents(ConstraintView.this));
				constraintViewer.refresh();
			}
		});
	}
	
	@Override
	public void dispose() {
		ConstraintViewSets.SINGLETON.unregisterView(this);
	}
}
