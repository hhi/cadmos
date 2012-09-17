package edu.tum.cs.cadmos.analysis.ui.controls;

import static java.lang.Math.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.xtext.util.Pair;

import edu.tum.cs.cadmos.analysis.Architecture2Node;
import edu.tum.cs.cadmos.analysis.Graph;
import edu.tum.cs.cadmos.analysis.HFRNodeLayout;
import edu.tum.cs.cadmos.analysis.Node;
import edu.tum.cs.cadmos.analysis.Node2Graph;
import edu.tum.cs.cadmos.analysis.NodeUtils;
import edu.tum.cs.cadmos.analysis.Vector2D;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Port;

public class ArchitectureDisplay extends Canvas implements PaintListener,
		ControlListener {

	protected static final int TIMER_INTERVAL_MILLIS = 30;
	protected static final int MARGIN = 15;

	protected Component component;
	protected HFRNodeLayout layout;

	protected Vector2D componentRadius = new Vector2D(10, 10);
	protected Vector2D portRadius = new Vector2D(5, 5);

	protected Runnable timer = new Runnable() {
		@Override
		public void run() {
			if (isDisposed()) {
				return;
			}
			long time = System.nanoTime();
			if (layout != null && !layout.done()) {
				for (int i = 0; i < 50; i++) {
					layout.step();
					if (layout.done()
							|| (System.nanoTime() - time) / 1_000_000 >= TIMER_INTERVAL_MILLIS / 2) {
						break;
					}
				}
				redraw();
				update();
			}
			time = max(0, TIMER_INTERVAL_MILLIS - (System.nanoTime() - time)
					/ 1_000_000);
			Display.getDefault().timerExec((int) time, this);
		}
	};

	public ArchitectureDisplay(Composite parent, int style) {
		super(parent, style);
		addPaintListener(this);
		addControlListener(this);
		Display.getDefault().timerExec(TIMER_INTERVAL_MILLIS, timer);
	}

	public void setComponent(Component component) {
		this.component = component;
		if (component == null) {
			return;
		}
		final Point size = getSize();
		final Node node = new Architecture2Node(component).translate();
		final Graph graph = Node2Graph.translate(node);
		layout = new HFRNodeLayout(graph, max(MARGIN, size.x - MARGIN * 2),
				max(MARGIN, size.y - MARGIN * 2));
	}

	@Override
	public void paintControl(PaintEvent event) {
		if (component == null || layout == null) {
			return;
		}

		final Graph graph = layout.getGraph();
		final GC gc = event.gc;
		gc.setAntialias(SWT.ON);

		for (final Pair<Node, Node> e : graph.getEdges()) {
			final Node v1 = e.getFirst();
			final Node v2 = e.getSecond();
			final Vector2D p1 = layout.get(v1);
			final Vector2D p2 = layout.get(v2);
			gc.setForeground(SWTResourceManager.getColor(149, 179, 215));
			gc.setBackground(gc.getForeground());
			gc.setLineWidth(1);
			paintDirectedEdge(gc, v1, v2, p1, p2);
		}
		for (final Node v : NodeUtils.filterBySemanticObject(
				graph.getVertices(), Embedding.class, Component.class)) {
			final Vector2D p = layout.get(v);
			gc.setForeground(SWTResourceManager.getColor(54, 95, 145));
			gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			gc.setLineWidth(2);
			final Vector2D radius = getRadius(v);
			final Rectangle r = new Rectangle(p.roundX() - radius.roundX(),
					p.roundY() - radius.roundY(), radius.roundX() * 2,
					radius.roundY() * 2);
			r.x += MARGIN;
			r.y += MARGIN;
			gc.fillRoundRectangle(r.x, r.y, r.width, r.height, 8, 8);
			gc.drawRoundRectangle(r.x, r.y, r.width, r.height, 8, 8);
		}
		for (final Node v : NodeUtils.filterBySemanticObject(
				graph.getVertices(), Port.class)) {
			final Vector2D p = layout.get(v);
			gc.setForeground(SWTResourceManager.getColor(79, 129, 189));
			gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			gc.setLineWidth(2);
			final Vector2D radius = getRadius(v);
			final Rectangle r = new Rectangle(p.roundX() - radius.roundX(),
					p.roundY() - radius.roundY(), radius.roundX() * 2,
					radius.roundY() * 2);
			r.x += MARGIN;
			r.y += MARGIN;
			gc.fillOval(r.x, r.y, r.width, r.height);
			gc.drawOval(r.x, r.y, r.width, r.height);
		}

	}

	private Vector2D getRadius(Node v) {
		if (v.getSemanticObject() instanceof Port) {
			return portRadius;
		}
		return componentRadius;
	}

	@Override
	public void controlMoved(ControlEvent e) {
		// Do nothing.
	}

	@Override
	public void controlResized(ControlEvent e) {
		if (layout != null) {
			final Point size = getSize();
			layout = new HFRNodeLayout(layout.getGraph(), max(MARGIN, size.x
					- MARGIN * 2), max(MARGIN, size.y - MARGIN * 2));
		}
	}

	private void paintDirectedEdge(GC gc, Node v1, Node v2, Vector2D p1,
			Vector2D p2) {
		final float bend = 0;
		final float center = 0.5f;
		final Vector2D v1radius = getRadius(v1);
		final Vector2D v2radius = getRadius(v2);
		/* Pre-calculate geometric variables. */
		final Path line = new Path(gc.getDevice());
		final float dX = p2.x - p1.x;
		final float dY = p2.y - p1.y;
		final float len = (float) sqrt(dX * dX + dY * dY);
		final float alpha = (float) (180.0 / PI * atan2(dY, dX));
		/* Draw edge's line as rotated cubic curve. */
		final Transform lineTransform = new Transform(gc.getDevice());
		lineTransform.translate(p1.x + MARGIN, p1.y + MARGIN);

		lineTransform.rotate(alpha);
		gc.setTransform(lineTransform);
		line.moveTo(v1radius.x, 0);
		line.quadTo(center * len, bend, len - v2radius.x, 0);
		gc.drawPath(line);
		gc.setTransform(null);
		lineTransform.dispose();
		line.dispose();
		/* Draw edge's arrow as rotated polygon. */
		final Transform arrowTransform = new Transform(gc.getDevice());
		arrowTransform.translate(p2.x + MARGIN, p2.y + MARGIN);
		// final float beta = (float) (180.0 / PI * atan2(bend, (center + 0.05f)
		// * len));

		arrowTransform.rotate(alpha);
		gc.setTransform(arrowTransform);
		final int[] arrow = new int[] { -v2radius.roundX(), 0,
				-v2radius.roundX() - 8, -2, -v2radius.roundX() - 8, 3 };
		gc.fillPolygon(arrow);
		gc.setTransform(null);
		arrowTransform.dispose();
	}

}
