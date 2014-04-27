package edu.tum.cs.cadmos.analysis.ui.perspective;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

public class ScheduleChart extends ViewPart {
	
	public static final String ID = "edu.tum.cs.cadmos.analysis.ui.perspective.ScheduleChart";

	public ScheduleChart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Label lblNothingToDraw = new Label(parent, SWT.NONE);
		lblNothingToDraw.setText("Nothing to draw yet");
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
