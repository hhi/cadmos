package edu.tum.cs.cadmos.analysis.ui.perspective;

import java.awt.Dimension;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import edu.tum.cs.cadmos.analysis.architecture.model.Edge;
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex;
import edu.tum.cs.cadmos.analysis.schedule.AssertionNameMapping;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class SchedulingAnalysisView extends ViewPart {

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.SchedulingAnalysisView"; //$NON-NLS-1$

	public SchedulingAnalysisView() {
		setPartName("Scheduling Analysis");
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Composite container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setLayout(new GridLayout(1, false));
		{
			Canvas canvas = new Canvas(container, SWT.NONE);
			canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			canvas.addPaintListener(new PaintListener(){

				@Override
				public void paintControl(PaintEvent e) {
					// Get the canvas and its size
			        Canvas canvas = (Canvas) e.widget;
			        int maxX = canvas.getSize().x;
			        int maxY = canvas.getSize().y;
			 
			        // Calculate the middle
			        int halfX = (int) maxX / 2;
			        int halfY = (int) maxY / 2;
			 
			        // Set the drawing color to blue
			        e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
			 
			 
//			        // Draw a vertical line halfway across the canvas
//			        e.gc.drawLine(halfX, 0, halfX, maxY);
//			 
//			        // Draw a horizontal line halfway down the canvas
//			        e.gc.drawLine(0, halfY, maxX, halfY);
			        
			        if(AssertionNameMapping.SINGLETON.getDeploymentModel() == null){
			        	return;
			        }
			        
			        DirectedSparseMultigraph<Vertex, Edge> graph = AssertionNameMapping.SINGLETON.getDeploymentModel().getSoftwareComponentDFG();
	        		FRLayout<Vertex, Edge> layout = new FRLayout<Vertex, Edge>(graph, new Dimension(maxX, maxY));
	        		layout.initialize();
	        		final int size = 10;
	        		
	        		for(Vertex v : graph.getVertices()){
	        			if(v.getData() instanceof Port){
	        				Port p = (Port) v.getData();
	        				if(p.isInbound()){
	        					layout.setLocation(v, size, size);
	        					layout.lock(v, true);
	        				} else {
	        					layout.setLocation(v, maxX-size, maxY-size);
	        					layout.lock(v, true);
	        				}
	        			}
	        		}
	        		
	        		for (int i = 0; i < 100; i++) {
	        			layout.step();	
	        			if(layout.done()){
	        				break;
	        			}
					}
	        		for(Edge ed: graph.getEdges()){
	        			int x1 = (int) layout.getX(graph.getSource(ed));
	        			int y1 = (int) layout.getY(graph.getSource(ed));
	        			int x2 = (int) layout.getX(graph.getDest(ed));
	        			int y2 = (int) layout.getY(graph.getDest(ed));
	        			e.gc.drawLine(x1, y1, x2, y2);
	        		}
	        		e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
	        		for(Vertex v : graph.getVertices()){
	        			double x = layout.getX(v);
	        			double y = layout.getY(v);
	        			
	        			e.gc.fillOval((int)x-size/2, (int)y-size/2, size, size);
	        		}
				}
				
			});
		}
		
		Button btnViewSchedule = new Button(container, SWT.NONE);
		btnViewSchedule.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().showView(ScheduleChart.ID);
				} catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnViewSchedule.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnViewSchedule.setText("View Schedule");

		createActions();
		initializeToolBar();
		initializeMenu();
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
}
