package edu.tum.cs.cadmos.analysis.ui.controls;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.tum.cs.cadmos.analysis.Architecture2Node;
import edu.tum.cs.cadmos.analysis.Graph;
import edu.tum.cs.cadmos.analysis.HFRNodeLayout;
import edu.tum.cs.cadmos.analysis.Node;
import edu.tum.cs.cadmos.analysis.Node2Graph;
import edu.tum.cs.cadmos.language.cadmos.Component;

public class ArchitectureDisplay extends Canvas implements PaintListener {

	private static final int TIMER_INTERVAL = 30;

	private Component component;
	private HFRNodeLayout layout;

	protected Runnable timer = new Runnable() {
		@Override
		public void run() {
			if (isDisposed()) {
				return;
			}
			Display.getDefault().timerExec(TIMER_INTERVAL, this);
		}
	};

	public ArchitectureDisplay(Composite parent, int style) {
		super(parent, style);
		addPaintListener(this);
		Display.getDefault().timerExec(TIMER_INTERVAL, timer);
	}

	public void setComponent(Component component) {
		this.component = component;
		if (component == null) {
			return;
		}
		final Node node = new Architecture2Node(component).translate();
		final Graph graph = Node2Graph.translate(node);
		final Point size = getSize();
		layout = new HFRNodeLayout(graph, size.x, size.y, layout);
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (component == null || layout == null) {
			return;
		}
	}

}
