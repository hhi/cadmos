package edu.tum.cs.cadmos.analysis.ui.perspective;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.xtext.ui.editor.LanguageSpecificURIEditorOpener;
import org.eclipse.xtext.xbase.lib.Pair;

import edu.tum.cs.cadmos.analysis.architecture.model.DeploymentModel;
import edu.tum.cs.cadmos.analysis.architecture.model.Vertex;
import edu.tum.cs.cadmos.analysis.schedule.AssertionNameMapping;
import edu.tum.cs.cadmos.analysis.schedule.IUnsatCoreListener;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.ui.EditorOpener;

import org.eclipse.wb.swt.SWTResourceManager;

public class ScheduleChart extends ViewPart implements IUnsatCoreListener{
	
	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.ScheduleChart";
	private ScrolledComposite scrolledComposite;
	private Spinner spinner;
	private Scale scale;
	private Canvas canvas;

	public ScheduleChart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		//add listener
		AssertionNameMapping.registerListener(this);
		
		parent.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(6, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		canvas_1 = new Canvas(composite, SWT.INHERIT_DEFAULT);
		GridData gd_canvas_1 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_canvas_1.heightHint = 10;
		canvas_1.setLayoutData(gd_canvas_1);
		canvas_1.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if(colorMap.isEmpty()){
					return;
				}
				GC g = e.gc;
				int maxX = canvas_1.getSize().x;
				int maxY = canvas_1.getSize().y;
				int offset_left = maxX/3;
				g.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
				g.setAlpha(255);
				g.fillRectangle(0,0, maxX,maxY);
				int rowHeight = maxY/colorMap.size();
				int maxPeriod = Collections.max(colorMap.keySet());
				double unit = (double)(maxX-offset_left)/(double)maxPeriod;
				int pad = 2;
				
				int width = offset_left/colorMap.size();
				int row = 0;
				for(Map.Entry<Integer, Color> entry : colorMap.entrySet()){
					g.setBackground(entry.getValue());
					g.setForeground(entry.getValue());
					int period = entry.getKey();
					g.setAlpha(255);
					g.fillRectangle(row*width+pad, pad, maxY-2*pad, maxY-2*pad);
					g.drawText("Period "+period, row*width+pad+maxY-2*pad+10, maxY/2-10, true);
					
					
					int repeat = 1;
					while(period*repeat < maxPeriod){
						repeat++;
					}
					g.setAlpha(150);
					for (int i = 0; i < repeat; i++) {
						g.fillRectangle(offset_left+i*(int)(period*unit)+pad, row*rowHeight+pad, (int)(period*unit)-2*pad, rowHeight-2*pad);
					}
					row++;
				}
			}
		});
		
		Label lblCycles = new Label(composite, SWT.NONE);
		lblCycles.setText("Cycles");
		
		
		spinner = new Spinner(composite, SWT.BORDER);
		spinner.setMaximum(5);
		spinner.setMinimum(1);
		spinner.setSelection(1);
		spinner.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.redraw();
			}
		});
		
		Label lblZoom = new Label(composite, SWT.NONE);
		lblZoom.setText("Zoom");
		

		scale = new Scale(composite, SWT.NONE);
		scale.setPageIncrement(100);
		scale.setMaximum(500);
		scale.setMinimum(50);
		scale.setSelection(100);
		
		Button btnReset = new Button(composite, SWT.NONE);
		btnReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scale.setSelection(100);
				spinner.setSelection(1);
				canvas.redraw();
				scrolledComposite.setOrigin(0, 0);
				colorMap.clear();
			}
		});
		btnReset.setText("Reset");
		scale.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				canvas.redraw();
			}
		});
		
		scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		canvas = new Canvas(scrolledComposite, SWT.NONE);
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		canvas.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e) {
		        
		        if(AssertionNameMapping.SINGLETON.getSchedule()==null){
		        	return;
		        }
		        // Get the canvas and its size
		        Canvas canvas = (Canvas) e.widget;
		        int maxX = scrolledComposite.getSize().x;
		        int maxY = canvas.getSize().y;
		        
		        
		        
		        e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
		        
		        
		        
		        HashMap<EObject, Pair<String, Integer>> schedule = AssertionNameMapping.SINGLETON.getSchedule();
		        DeploymentModel model = AssertionNameMapping.SINGLETON.getDeploymentModel();
		        
		        
		        GC g = e.gc;
		        
		        
		        int cycles = spinner.getSelection();
		        int zoom = scale.getSelection();
		        
		        int pad = 3;
		        int offset_left = 100;
		        int offset_bottom = 30;
		        int offset_right = 30;
		        
		        int maxPeriod = Collections.max(model.getPeriod().keySet());
		        int coresNum = model.getProcessingComponentDFG().getVertexCount();
		        
		        double unit = (double)(maxX-offset_left-offset_right)/(double)maxPeriod*(double)zoom/100.0;
		        int rowHeight = (maxY-offset_bottom)/coresNum;
		        
		        g.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		        g.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		        
		        
		        int row = 0;
		        for(Vertex vcore : model.getProcessingComponentDFG().getVertices()){
		        	
		        	Embedding core = (Embedding) vcore.getData();
		        	for(Map.Entry<EObject, Pair<String, Integer>> task : schedule.entrySet()){
		        		if(task.getValue().getKey().equals(core.getName())){
		        			if (task.getKey() instanceof Embedding) {
		        				for(int cycle_i = 0; cycle_i<cycles; cycle_i++){
		        					int cycle_offset = (int) ((double)cycle_i * unit * (double)maxPeriod);
		        					
									Embedding taskEm = (Embedding) task.getKey();
									int start = task.getValue().getValue();
									int wcet = getWCET(taskEm, core, model);
									int repeat = 1;
									int myperiod = maxPeriod;
									for(Map.Entry<Integer, List<String>> entry : model.getPeriod().entrySet()){
										if(entry.getValue().contains(taskEm.getComponent().getName()) && entry.getKey() < maxPeriod){
											myperiod = entry.getKey();
											
											while(myperiod*repeat<maxPeriod){
												repeat ++;
											}
										}
									}
									
									start = start % myperiod;
									
									for(int i = 0; i<repeat; i++){
										int pOffset = (int) (i*unit*myperiod);
										g.setForeground(getColor(myperiod));
										g.setBackground(getColor(myperiod));
										g.setAlpha(150);
										g.fillRectangle(offset_left+cycle_offset+pOffset+(int)(start*unit)+pad, row*rowHeight+pad, (int)(unit*wcet)-2*pad, rowHeight-2*pad);
										g.setAlpha(255);
										g.drawRectangle(offset_left+cycle_offset+pOffset+(int)(start*unit)+pad, row*rowHeight+pad, (int)(unit*wcet)-2*pad, rowHeight-2*pad);
										g.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
										g.drawText(" "+taskEm.getName(), offset_left+cycle_offset+pOffset+(int)(start*unit)+pad, row*rowHeight+pad, SWT.DRAW_TRANSPARENT);
									}
								
		        				}
							}
		        		}
		        	}
		        	row++;
		        	g.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		        	g.drawLine(0, row*rowHeight, (int) (unit*(double)cycles*(double)maxPeriod*1.2), row*rowHeight);
		        	g.drawText(" "+core.getName(), 0, row*rowHeight-15, SWT.DRAW_TRANSPARENT);
		        	
		        }
		        //draw tick lines
		        int step = getGridStep(unit);
		        Color maxPeriodColor = getColor(maxPeriod);
		        
		        
		        for(int i = 0; i <= cycles*maxPeriod; i++){
	        		if(i%maxPeriod==0){
	        			g.setForeground(maxPeriodColor);
	        			g.setLineStyle(SWT.LINE_DOT);
	        			g.setLineWidth(3);
	        			g.drawLine(offset_left+(int)(i*unit), 0, offset_left+(int)(i*unit), maxY);
	        		}
		        	if(i%step!=0) continue;
		        	g.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		        	g.setLineWidth(1);
		        	g.setLineStyle(SWT.LINE_DOT);
		        	g.drawLine(offset_left+(int)(i*unit), 0, offset_left+(int)(i*unit), maxY);
		        	g.drawText(" "+i,offset_left+(int)(i*unit), maxY-20, SWT.DRAW_TRANSPARENT);
		        	
		        }
		        
		        
		       scrolledComposite.setMinSize((int) (unit*cycles*maxPeriod), SWT.DEFAULT);
		       scrolledComposite.getParent().layout();
			}});
		
		
		
		
		
		
		
		scrolledComposite.setContent(canvas);
		// TODO Auto-generated method stub

	}
	
	protected int getGridStep(double unit) {
		int[] steps = new int[]{1,2,5,10,25,50,100,250,500,1000,2500};
		int threshold = 100;
		for (int i = 0; i < steps.length; i++) {
			if(steps[i]*unit > threshold){
				return steps[i];
			}
		}
		return 1;
	}


	HashMap<Integer, Color> colorMap = new HashMap<Integer, Color>();
	DeploymentModel dm = null;
	private Color getColor(int period){
		if(dm != AssertionNameMapping.SINGLETON.getDeploymentModel()){
			dm = AssertionNameMapping.SINGLETON.getDeploymentModel();
			colorMap.clear();
		}
		if(!colorMap.containsKey(period)){
			colorMap.put(period, getNextColor());
		}
		return colorMap.get(period);
	}

	private Color getNextColor() {
		colorPointer = (colorPointer +1)%colors.length;
		return colors[colorPointer];
	}
	
	private int colorPointer = -1;
	private Color[] colors = new Color[]{
										new Color(null, 0,0,255),	//blue
										new Color(null, 255,150,0),	//orange
										new Color(null, 255, 0, 0), //red
										new Color(null, 0,200,0),	//green
										};
	private Canvas canvas_1;

	protected int getWCET(Embedding task, Embedding core, DeploymentModel model) {
		String taskComp = task.getComponent().getName();
		String coreComp = core.getComponent().getName();
		
		for(Map.Entry<Pair<String, String>, Integer> entry :model.getWcet().entrySet()){
			if(entry.getKey().getKey().equals(taskComp) &&
					entry.getKey().getValue().equals(coreComp)){
				return entry.getValue();
			}
		}
		
		
		
		return 0;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyUnsatCoreChange() {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				canvas_1.redraw();
				canvas.redraw();
				
			}
		});
	}
	
	@Override
	public void dispose() {
		AssertionNameMapping.unregisterListener(this);
	}
}
