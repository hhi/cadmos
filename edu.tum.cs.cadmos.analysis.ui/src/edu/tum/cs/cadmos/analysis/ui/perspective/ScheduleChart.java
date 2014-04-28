package edu.tum.cs.cadmos.analysis.ui.perspective;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.part.ViewPart;

public class ScheduleChart extends ViewPart {
	
	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.ScheduleChart";

	public ScheduleChart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSpace = new Label(composite, SWT.NONE);
		lblSpace.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		Spinner spinner = new Spinner(composite, SWT.BORDER);
		spinner.setMaximum(5);
		spinner.setMinimum(1);
		spinner.setSelection(1);
		
		Scale scale = new Scale(composite, SWT.NONE);
		scale.setPageIncrement(100);
		scale.setMaximum(500);
		scale.setMinimum(10);
		scale.setSelection(100);
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Canvas canvas = new Canvas(scrolledComposite, SWT.NONE);
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
		 
		 
		        // Draw a vertical line halfway across the canvas
		        e.gc.drawLine(halfX, 0, halfX, maxY);
		 
		        // Draw a horizontal line halfway down the canvas
		        e.gc.drawLine(0, halfY, maxX, halfY);
			}});
		
		
		
		
		
		
		
		scrolledComposite.setContent(canvas);
		scrolledComposite.setMinSize(canvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
