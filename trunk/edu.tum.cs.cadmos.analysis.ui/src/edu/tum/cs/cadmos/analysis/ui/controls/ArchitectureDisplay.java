package edu.tum.cs.cadmos.analysis.ui.controls;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
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
import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Port;

public class ArchitectureDisplay extends Canvas implements PaintListener,
		ControlListener {

	protected static final int TIMER_INTERVAL_MILLIS = 33;
	protected static final int MARGIN_X = 65;
	protected static final int MARGIN_Y = 55;
	protected static final int POST_PAINTINGS = 3;
	private static final int VERTEX_ARC_WIDTH = 8;
	private static final int VERTEX_ARC_HEIGHT = 8;

	protected Component component;
	protected HFRNodeLayout layout;

	protected Vector2D componentRadius = new Vector2D(10, 10);
	protected Vector2D portRadius = new Vector2D(5, 5);

	protected Image buffer;
	protected int postPaintings;

	protected Runnable timer = new Runnable() {
		@Override
		public void run() {
			if (isDisposed()) {
				return;
			}
			long time = System.nanoTime();
			if (layout != null && !layout.done()) {
				for (int i = 0; i < 100; i++) {
					layout.step();
					if (layout.done()
							|| (System.nanoTime() - time) / 1_000_000 >= TIMER_INTERVAL_MILLIS / 2) {
						break;
					}
				}
				redraw();
				// update();
				postPaintings = POST_PAINTINGS;
			} else if (postPaintings-- > 0) {
				redraw();
				// update();
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
		layout = new HFRNodeLayout(graph, max(MARGIN_X, size.x - MARGIN_X * 2),
				max(MARGIN_Y, size.y - MARGIN_Y * 2));
	}

	@Override
	public void paintControl(PaintEvent event) {
		if (component == null || layout == null) {
			return;
		}

		final Point size = getSize();
		if (buffer == null || buffer.getBounds().width != size.x
				|| buffer.getBounds().height != size.y) {
			if (buffer != null) {
				buffer.dispose();
			}
			buffer = new Image(getDisplay(), size.x, size.y);
		}

		final Graph graph = layout.getGraph();
		final GC gc = new GC(buffer);
		gc.setAntialias(SWT.ON);
		gc.setTextAntialias(SWT.ON);
		if (postPaintings > 1) {
			// Lower alpha means more "fade out" steps.
			gc.setAlpha(48);
		}
		gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		gc.fillRectangle(getClientArea());

		gc.setForeground(SWTResourceManager.getColor(149, 179, 215));
		gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		gc.setLineWidth(2);
		gc.setAlpha(255);
		gc.setLineStyle(SWT.LINE_DASH);
		gc.drawRoundRectangle(MARGIN_X,
				MARGIN_Y - componentRadius.roundY() - 4, size.x - MARGIN_X * 2,
				size.y - (MARGIN_Y - componentRadius.roundY() - 4) * 2,
				VERTEX_ARC_WIDTH * 2, VERTEX_ARC_HEIGHT * 2);
		gc.setFont(JFaceResources.getBannerFont());
		final String text = "component "
				+ component.getName()
				+ "("
				+ ModelUtils.getParameterNameValuePairs(
						component.getParameters(), ", ") + ")";
		gc.drawString(text, 4, 4, true);

		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setFont(JFaceResources.getDefaultFont());
		if (layout.done()) {
			gc.setAlpha(255);
		} else {
			// Lower alpha means more "washed out" look during layout phase.
			gc.setAlpha(128);
		}

		for (final Pair<Node, Node> e : graph.getEdges()) {
			final Node v1 = e.getFirst();
			final Node v2 = e.getSecond();
			gc.setForeground(SWTResourceManager.getColor(149, 179, 215));
			gc.setBackground(gc.getForeground());
			gc.setLineWidth(1);
			paintDirectedEdge(gc, v1, v2);
		}
		for (final Node v : NodeUtils.filterBySemanticObject(
				graph.getVertices(), Embedding.class, Component.class)) {
			gc.setForeground(SWTResourceManager.getColor(54, 95, 145));
			gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			gc.setLineWidth(2);
			paintVertex(gc, v, false);
		}
		for (final Node v : NodeUtils.filterBySemanticObject(
				graph.getVertices(), Port.class)) {
			gc.setForeground(SWTResourceManager.getColor(79, 129, 189));
			gc.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			gc.setLineWidth(2);
			paintVertex(gc, v, true);
		}
		if (layout.done()) {
			final List<Rectangle> textBoxes = new ArrayList<>();
			for (final Node v : NodeUtils.filterBySemanticObject(
					graph.getVertices(), Embedding.class, Component.class)) {
				gc.setForeground(SWTResourceManager.getColor(128, 128, 128));
				gc.setTextAntialias(SWT.ON);
				gc.setLineWidth(1);
				paintVertexName(gc, v, textBoxes);
			}
			for (final Node v : NodeUtils.filterBySemanticObject(
					graph.getVertices(), Port.class)) {
				if (v.getParent() == null || v.getParent().getParent() != null) {
					continue;
				}
				gc.setForeground(SWTResourceManager.getColor(128, 128, 128));
				gc.setTextAntialias(SWT.ON);
				gc.setLineWidth(1);
				paintVertexName(gc, v, textBoxes);
			}
		}

		gc.dispose();
		event.gc.drawImage(buffer, 0, 0);

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
			layout = new HFRNodeLayout(layout.getGraph(), max(MARGIN_X, size.x
					- MARGIN_X * 2), max(MARGIN_Y, size.y - MARGIN_Y * 2));
		}
	}

	private void paintVertex(final GC gc, final Node v, boolean isPort) {
		final Vector2D p = layout.get(v);
		final Vector2D radius = getRadius(v);
		final Rectangle r = new Rectangle(p.roundX() - radius.roundX(),
				p.roundY() - radius.roundY(), radius.roundX() * 2,
				radius.roundY() * 2);
		r.x += MARGIN_X;
		r.y += MARGIN_Y;
		if (isPort) {
			gc.fillOval(r.x, r.y, r.width, r.height);
			gc.drawOval(r.x, r.y, r.width, r.height);
		} else {
			gc.fillRoundRectangle(r.x, r.y, r.width, r.height,
					VERTEX_ARC_WIDTH, VERTEX_ARC_HEIGHT);
			gc.drawRoundRectangle(r.x, r.y, r.width, r.height,
					VERTEX_ARC_WIDTH, VERTEX_ARC_HEIGHT);
		}
	}

	private void paintVertexName(GC gc, Node v1, List<Rectangle> textBoxes) {
		final Vector2D p1 = layout.get(v1);
		final Vector2D v1radius = getRadius(v1);
		final int angles = 16;
		final int min_radius = 30;
		final int max_radius = 190;
		final int inc_radius = 40;
		final String name = v1.getLocalId();
		final Point extent = gc.stringExtent(name);
		final Rectangle r = new Rectangle(0, 0, extent.x, extent.y);
		int x = p1.roundX();
		int y = p1.roundY();
		int min_intersections = Integer.MAX_VALUE;
		boolean zeroIntersectFound = false;
		float angle;
		float direction;
		if (p1.x > layout.getWidth() / 2) {
			direction = 1;
			if (p1.y < layout.getHeight() / 2) {
				angle = (float) PI * 0.25f;
			} else {
				angle = (float) PI * 0.75f;
			}
		} else {
			direction = -1;
			if (p1.y >= layout.getHeight() / 2) {
				angle = (float) PI * 1.25f;
			} else {
				angle = (float) PI * 1.75f;
			}
		}
		for (int radius = min_radius; radius <= max_radius
				&& !zeroIntersectFound; radius += inc_radius) {
			for (int i = 0; i < angles; i++) {
				final Vector2D p_cand = new Vector2D((float) (p1.x + radius
						* sin(angle)), (float) (p1.y - radius * cos(angle)));
				r.x = round(p_cand.x - 0.5f * extent.x + 0.5f * extent.x
						* (float) sin(angle));
				r.y = round(p_cand.y - 0.5f * extent.y - 0.5f * extent.y
						* (float) cos(angle));
				angle += (direction * 2 * PI / angles);
				if (r.x < -MARGIN_X
						|| r.x + r.width > layout.getWidth() + MARGIN_X
						|| r.y < -MARGIN_Y
						|| r.y + r.height > layout.getHeight() + MARGIN_Y) {
					continue;
				}
				int intersections = 0;
				for (final Node v2 : layout.getGraph()) {
					final Vector2D p2 = layout.get(v2);
					final Vector2D r2 = getRadius(v2);
					if (r.intersects(p2.roundX() - r2.roundX() - 4, p2.roundY()
							- r2.roundY() - 4, r2.roundX() * 2 + 8,
							r2.roundY() * 2 + 8)) {
						intersections++;
					}
				}
				for (final Rectangle textBox : textBoxes) {
					if (r.intersects(textBox)) {
						intersections++;
					}
				}
				if (intersections < min_intersections) {
					min_intersections = intersections;
					x = r.x;
					y = r.y;
					if (intersections == 0) {
						zeroIntersectFound = true;
						break;
					}
				}
			}
		}
		r.x = x;
		r.y = y;
		r.width = extent.x;
		r.height = extent.y;
		textBoxes.add(r);
		/* Draw line. */
		gc.setAlpha(64);
		final Vector2D p2 = new Vector2D(x + extent.x / 2, y + extent.y / 2);
		final Vector2D delta = p2.delta(p1);
		final float len = delta.norm();
		final Transform lineTransform = new Transform(gc.getDevice());
		lineTransform.translate(p1.x + MARGIN_X, p1.y + MARGIN_Y);
		lineTransform.rotate(delta.alpha(true));
		gc.setTransform(lineTransform);
		gc.drawLine(v1radius.roundX() + 2, 0, round(len) - extent.y / 2, 0);
		// gc.drawLine(v1radius.roundX() / 2, 0, (int) round(len - extent.x *
		// 0.5
		// * abs(cos(alpha)) - extent.y * 0.5 * abs(sin(alpha))) - 2, 0);
		gc.setTransform(null);
		lineTransform.dispose();
		/* Draw background box. */
		gc.setAlpha(128);
		gc.fillRoundRectangle(x + MARGIN_X - 6, y + MARGIN_Y - 2,
				extent.x + 12, extent.y + 4, 6, 6);
		/* Draw name. */
		gc.setAlpha(255);
		gc.drawString(name, x + MARGIN_X, y + MARGIN_Y, true);
	}

	private void paintDirectedEdge(GC gc, Node v1, Node v2) {
		final Vector2D p1 = layout.get(v1);
		final Vector2D p2 = layout.get(v2);
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
		lineTransform.translate(p1.x + MARGIN_X, p1.y + MARGIN_Y);

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
		arrowTransform.translate(p2.x + MARGIN_X, p2.y + MARGIN_Y);
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
