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
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.ui.EditorOpener;

public class ScheduleChart extends ViewPart {
	
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
		parent.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSpace = new Label(composite, SWT.NONE);
		lblSpace.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
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
		canvas.addPaintListener(new PaintListener(){

			@Override
			public void paintControl(PaintEvent e) {
				// Get the canvas and its size
		        Canvas canvas = (Canvas) e.widget;
		        int maxX = scrolledComposite.getSize().x;
		        int maxY = canvas.getSize().y;
		 
		        // Calculate the middle
		        int halfX = (int) maxX / 2;
		        int halfY = (int) maxY / 2;
		 
		        e.gc.setForeground(e.display.getSystemColor(SWT.COLOR_RED));
		        
		        
		        
		        if(AssertionNameMapping.SINGLETON.getSchedule()==null){
		        	return;
		        }
		        
		        HashMap<EObject, Pair<String, Integer>> schedule = AssertionNameMapping.SINGLETON.getSchedule();
		        DeploymentModel model = AssertionNameMapping.SINGLETON.getDeploymentModel();
		        
		        
		        GC g = e.gc;
		        
		        int cycles = spinner.getSelection();
		        int zoom = scale.getSelection();
		        
		        int pad = 3;
		        
		        int maxPeriod = Collections.max(model.getPeriod().keySet());
		        int coresNum = model.getProcessingComponentDFG().getVertexCount();
		        
		        double unit = (double)maxX/(double)maxPeriod*(double)zoom/100.0;
		        int rowHeight = maxY/coresNum;
		        
		        g.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		        g.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		        
		        
		        int row = 0;
		        for(Vertex vcore : model.getProcessingComponentDFG().getVertices()){
		        	
		        	Embedding core = (Embedding) vcore.getData();
		        	for(Map.Entry<EObject, Pair<String, Integer>> task : schedule.entrySet()){
		        		if(task.getValue().getKey().equals(core.getName())){
		        			if (task.getKey() instanceof Embedding) {
								Embedding taskEm = (Embedding) task.getKey();
								int start = task.getValue().getValue();
								int wcet = getWCET(taskEm, core, model);
								
								
								int repeat = 1;
								int myperiod = maxPeriod;
								////
								for(Map.Entry<Integer, List<String>> entry : model.getPeriod().entrySet()){
									if(entry.getValue().contains(taskEm.getComponent().getName()) && entry.getKey() < maxPeriod){
										myperiod = entry.getKey();
										
										while(myperiod*repeat<maxPeriod){
											repeat ++;
										}
									}
										System.out.println(taskEm.getComponent().getName()+"   "+entry.getKey()+"    "+repeat);
								}
								
								if(taskEm.getName().equals("reader")){
									int asdas = 68; //FIXME Offset bug
								}
								///
								
								for(int i = 0; i<repeat; i++){
									int offset = (int) (i*unit*myperiod);
									g.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
									g.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
									g.setAlpha(100);
									g.fillRectangle(offset+(int)(start*unit)+pad, row*rowHeight+pad, (int)(unit*wcet)-2*pad, rowHeight-2*pad);
									g.setAlpha(255);
									g.drawRectangle(offset+(int)(start*unit)+pad, row*rowHeight+pad, (int)(unit*wcet)-2*pad, rowHeight-2*pad);
									g.drawText(taskEm.getName(), offset+(int)(start*unit)+pad, row*rowHeight+pad);
								}
								
								
							}
		        		}
		        	}
		        	row++;
		        	g.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		        	g.drawLine(0, row*rowHeight, maxX, row*rowHeight);
		        	g.drawText(core.getName(), 0, row*rowHeight-15, SWT.DRAW_TRANSPARENT);
		        	
		        }
		        //draw tick lines
		        int step = getGridStep(unit);
		        for(int i = 1; i < cycles*(maxPeriod+2); i++){
		        	if(i%step!=0) continue;
		        	g.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		        	g.setLineStyle(SWT.LINE_DOT);
		        	g.drawLine((int)(i*unit), 0, (int)(i*unit), maxY);
		        	g.drawText(i+"",(int)(i*unit), maxY-20, SWT.DRAW_TRANSPARENT);
		        	
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

	private class PeriodEntry{
		public Embedding task;
		public Embedding core;
		public int start;
		public int wcet;
		public int period;
		
	}


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
}
