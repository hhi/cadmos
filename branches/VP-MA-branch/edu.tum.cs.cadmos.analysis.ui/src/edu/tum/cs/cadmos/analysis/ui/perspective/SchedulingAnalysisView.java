package edu.tum.cs.cadmos.analysis.ui.perspective;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.xtext.xbase.lib.Pair;

import edu.tum.cs.cadmos.analysis.architecture.model.Edge;
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex;
import edu.tum.cs.cadmos.analysis.schedule.AssertionNameMapping;
import edu.tum.cs.cadmos.analysis.schedule.IUnsatCoreListener;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.Role;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

public class SchedulingAnalysisView extends ViewPart implements IUnsatCoreListener{

	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.SchedulingAnalysisView"; //$NON-NLS-1$
	private Canvas canvas;

	public SchedulingAnalysisView() {
		setPartName("Scheduling Analysis");
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		//listener
		AssertionNameMapping.registerListener(this);
		
		parent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		Composite container = new Composite(parent, SWT.NONE);
		container.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		container.setLayout(new GridLayout(1, false));
		{
			canvas = new Canvas(container, SWT.NONE);
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
			        
			        if(AssertionNameMapping.SINGLETON.getDeploymentModel() == null || AssertionNameMapping.SINGLETON.getDeploymentModel().getSoftwareComponentDFG() == null){
			        	return;
			        }
			        
			        DirectedSparseMultigraph<Vertex, Edge> graph = AssertionNameMapping.SINGLETON.getDeploymentModel().getSoftwareComponentDFG();
	        		FRLayout<Vertex, Edge> layout = new FRLayout<Vertex, Edge>(graph, new Dimension(maxX, halfY));
	        		layout.setAttractionMultiplier(0.3);
	        		layout.setRepulsionMultiplier(0.973);
	        		
	        		
	        		
	        		layout.initialize();
	        		final int size = 10;
	        		
	        		for(Vertex v : graph.getVertices()){
	        			if(v.getData() instanceof Port){
	        				Port p = (Port) v.getData();
	        				if(p.isInbound()){
	        					layout.setLocation(v, size, size);
	        					layout.lock(v, true);
	        				} else {
	        					layout.setLocation(v, maxX-size, halfY-size);
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
	        			
	        			e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
	        			if(isRelaxed(graph, ed)){
	        				e.gc.setAlpha(100);
	        				e.gc.setLineWidth(4);
	        				e.gc.setLineStyle(SWT.LINE_DOT);
	        			}
	        			e.gc.drawLine(x1, y1, x2, y2);
	        			e.gc.setAlpha(255);
	        			e.gc.setLineWidth(1);
	        			e.gc.setLineStyle(SWT.LINE_SOLID);
	        		}
	        		for(Vertex v : graph.getVertices()){
	        			double x = layout.getX(v);
	        			double y = layout.getY(v);
	        			
	        			e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
	        			e.gc.fillOval((int)x-size/2, (int)y-size/2, size, size);
	        			
	        			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
	        			e.gc.drawText(v.getId(), (int)x+size/2, (int)y+size/2, SWT.DRAW_TRANSPARENT);
	        		}
			        
			        
	        		DirectedSparseMultigraph<Vertex, Edge> graphD = AssertionNameMapping.SINGLETON.getDeploymentModel().getProcessingComponentDFG();
	        		FRLayout<Vertex, Edge> layoutD = new FRLayout<Vertex, Edge>(graph);
	        		layoutD.setSize(new Dimension(maxX, halfY));
	        		layoutD.setAttractionMultiplier(0.3);
	        		layoutD.setRepulsionMultiplier(0.43);
	        		
	        		
	        		
	        		
	        		layoutD.initialize();
	        		
	        		for(Vertex v : graphD.getVertices()){
	        			if(v.getData() instanceof Embedding){
	        				Embedding p = (Embedding) v.getData();
	        				if(p.getComponent().getRole() == Role.BUS){
	        					layoutD.setLocation(v, halfX, halfY-2*size);
	        					layoutD.lock(v, true);
	        				} else {
//	        					layoutD.setLocation(v, halfX, halfY-150);
	        					layoutD.lock(v, false);
	        				}
	        			}
	        		}
	        		
	        		for (int i = 0; i < 100; i++) {
	        			layoutD.step();	
	        			if(layoutD.done()){
	        				break;
	        			}
	        		}
	        		
	        		e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLUE));
	        		for(Edge ed: graphD.getEdges()){
	        			int x1 = (int) layoutD.getX(graphD.getSource(ed));
	        			int y1 = (int) layoutD.getY(graphD.getSource(ed));
	        			int x2 = (int) layoutD.getX(graphD.getDest(ed));
	        			int y2 = (int) layoutD.getY(graphD.getDest(ed));
	        			e.gc.drawLine(x1, y1+halfY, x2, y2+halfY);
	        		}
	        		for(Vertex v : graphD.getVertices()){
	        			double x = layoutD.getX(v);
	        			double y = layoutD.getY(v);
	        			
	        			e.gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
	        			e.gc.fillOval((int)x-size/2, (int)y-size/2+halfY, size, size);

	        			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_BLACK));
	        			e.gc.drawText(v.getId(), (int)x+size/2, (int)y+size/2+halfY, SWT.DRAW_TRANSPARENT);
	        		}
	        		
	        		if(AssertionNameMapping.SINGLETON.getSchedule() != null){
	        			e.gc.setLineStyle(SWT.LINE_DOT);
	        			e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_DARK_GRAY));
	        			for(Entry<EObject, Pair<String, Integer>>  entry : AssertionNameMapping.SINGLETON.getSchedule().entrySet()){
	        				Vertex s = null;
	        				if (entry.getKey() instanceof Embedding) {
								Embedding em = (Embedding) entry.getKey();
								s = getVertex(em.getName(), graph);
							}

	        				
	        				Vertex p = getVertex(entry.getValue().getKey(), graphD);
	        				
	        				if(s == null || p ==null) continue;
	        				
	        				int x1 = (int) layout.getX(s);
		        			int y1 = (int) layout.getY(s);
		        			int x2 = (int) layoutD.getX(p);
		        			int y2 = (int) layoutD.getY(p);
		        			e.gc.drawLine(x1, y1, x2, y2+halfY);
	        				
	        			}
	        			
	        		}
	        		
	        		
				}

				private Vertex getVertex(String key,
						DirectedSparseMultigraph<Vertex, Edge> graph) {
					for(Vertex v : graph.getVertices()){
						EObject data = v.getData();
						if (data instanceof Embedding) {
							Embedding em = (Embedding) data;
							if(em.getName().equals(key)){
								return v;
							}
						} else if (data instanceof Port) {
							Port po = (Port) data;
							if(po.getName().equals(key)){
								return v;
							}
						}
					}
					return null;
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

	protected boolean isRelaxed(DirectedSparseMultigraph<Vertex, Edge> graph,
			Edge ed) {
		HashSet<String> relax = AssertionNameMapping.SINGLETON.getRelaxSet();
		if(relax.size() == 0){
			return false;
		}
		String type = "precedence";
		String c1 = graph.getSource(ed).getId();
		String c2 = graph.getDest(ed).getId();
		for(String s : relax){
			if(s.contains(type) &&
					s.contains(c1) &&
					s.contains(c2)){
				return true;
			}
		}
		
		
		return false;
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
	public void notifyUnsatCoreChange() {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				canvas.redraw();
				
			}
		});
	}
	
	@Override
	public void dispose() {
		AssertionNameMapping.unregisterListener(this);
	}
}
