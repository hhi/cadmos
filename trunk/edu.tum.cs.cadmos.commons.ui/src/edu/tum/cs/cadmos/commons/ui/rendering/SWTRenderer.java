/*--------------------------------------------------------------------------+
|                                                                          |
| Copyright 2008-2012 Technische Universitaet Muenchen                     |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+--------------------------------------------------------------------------*/

package edu.tum.cs.cadmos.commons.ui.rendering;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;

/**
 * Renders a {@link RenderingContext} on a SWT {@link Canvas}.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class SWTRenderer implements PaintListener {

	public static final int ARROW_LENGTH = 12;

	public static final int ARROW_WIDTH = 8;

	public static final float CUBIC_LINE_DIVISOR = .45f;

	private final Canvas canvas;

	private RenderingContext context;

	private final Map<RGB, Color> colors = new HashMap<>();

	private boolean fontsPrepared;

	private Font smallFont;

	private Font normalFont;

	private Font largeFont;

	private boolean drawEnabled;

	private boolean fillEnabled;

	private EArrowStyle srcArrowStyle;

	private EArrowStyle dstArrowStyle;

	private Point size;

	/**
	 * Creates a new renderer that renders on the given canvas.
	 */
	public SWTRenderer(Canvas canvas) {
		this.canvas = canvas;
		canvas.addPaintListener(this);
	}

	/** Returns the canvas. */
	public Canvas getCanvas() {
		return canvas;
	}

	/** Returns the context. */
	public RenderingContext getContext() {
		return context;
	}

	/** Sets the context. */
	public void setContext(RenderingContext context) {
		this.context = context;
	}

	/** {@inheritDoc} */
	@Override
	public void paintControl(PaintEvent e) {
		if (context == null) {
			return;
		}
		final GC gc = e.gc;
		drawEnabled = true;
		fillEnabled = true;
		gc.setAntialias(SWT.ON);
		gc.setLineCap(SWT.CAP_ROUND);
		gc.setLineJoin(SWT.JOIN_ROUND);
		gc.setLineWidth(1);
		gc.setTextAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		srcArrowStyle = EArrowStyle.NONE;
		dstArrowStyle = EArrowStyle.NONE;
		prepareFonts(gc);
		gc.setFont(normalFont);
		if (size == null || !size.equals(canvas.getSize())) {
			size = canvas.getSize();
			final Rectangle clientArea = canvas.getClientArea();
			clientArea.width--;
			clientArea.height--;
			context.scale(Rect.fromRectangle(clientArea));
		}
		for (final Command command : context.getCommands()) {
			executeCommand(command, gc);
		}
	}

	private void prepareFonts(GC gc) {
		if (fontsPrepared) {
			return;
		}
		final Device device = gc.getDevice();
		normalFont = JFaceResources.getDialogFont();
		smallFont = JFaceResources.getDialogFontDescriptor().increaseHeight(-1)
				.createFont(device);
		largeFont = JFaceResources.getDialogFontDescriptor().increaseHeight(3)
				.createFont(device);
		fontsPrepared = true;
	}

	/**
	 * Executes the given <i>command</i> on the given <i>graphics context</i>.
	 */
	private void executeCommand(Command command, GC gc) {
		switch (command.type) {
		case SET_DRAW:
			executeSetDrawCommand(command, gc);
			break;
		case SET_FILL:
			executeSetFillCommand(command, gc);
			break;
		case SET_OPACITY:
			executeSetOpacityCommand(command, gc);
			break;
		case SET_FONT_SIZE:
			executeSetFontSizeCommand(command, gc);
			break;
		case SET_ARROWS:
			executeSetArrowsCommand(command);
			break;
		case TEXT:
			executeTextCommand(command, gc);
			break;
		case LINE:
			executeLineCommand(command, gc);
			break;
		case CONNECTOR:
			executeConnectorCommand(command, gc);
			break;
		case RECTANGLE:
			executeRectangleCommand(command, gc);
			break;
		case ROUNDED_RECTANGLE:
			executeRoundedRectangleCommand(command, gc);
			break;
		case ELLIPSE:
			executeEllipseCommand(command, gc);
			break;
		}
	}

	/**
	 * Executes a command of type {@link ECommandType#SET_DRAW}.
	 */
	private void executeSetDrawCommand(Command command, GC gc) {
		if (command.drawColor != null) {
			gc.setForeground(getColor(command.drawColor, gc.getDevice()));
		}
		if (command.drawStyle != null) {
			switch (command.drawStyle) {
			case NONE:
				drawEnabled = false;
				break;
			case SOLID:
				drawEnabled = true;
				gc.setLineStyle(SWT.LINE_SOLID);
				break;
			case DOTTED:
				drawEnabled = true;
				gc.setLineStyle(SWT.LINE_DOT);
				break;
			case DASHED:
				drawEnabled = true;
				gc.setLineStyle(SWT.LINE_DASH);
				break;
			}
		}
	}

	/**
	 * Executes a command of type {@link ECommandType#SET_FILL}.
	 */
	private void executeSetFillCommand(Command command, GC gc) {
		if (command.fillColor != null) {
			gc.setBackground(getColor(command.fillColor, gc.getDevice()));
		}
		if (command.fillStyle != null) {
			switch (command.fillStyle) {
			case NONE:
				fillEnabled = false;
				break;
			case SOLID:
				drawEnabled = true;
				break;
			}
		}
	}

	/**
	 * Executes a command of type {@link ECommandType#SET_OPACITY}.
	 */
	private static void executeSetOpacityCommand(Command command, GC gc) {
		if (command.opacity != null) {
			gc.setAlpha(round(command.opacity * 255f));
		}
	}

	/**
	 * Executes a command of type {@link ECommandType#SET_FONT_SIZE}.
	 */
	private void executeSetFontSizeCommand(Command command, GC gc) {
		if (command.fontSize != null) {
			switch (command.fontSize) {
			case SMALL:
				gc.setFont(smallFont);
				break;
			case NORMAL:
				gc.setFont(normalFont);
				break;
			case LARGE:
				gc.setFont(largeFont);
				break;
			}
		}
	}

	/**
	 * Executes a command of type {@link ECommandType#SET_ARROWS}.
	 */
	private void executeSetArrowsCommand(Command command) {
		if (command.srcArrowStyle != null) {
			srcArrowStyle = command.srcArrowStyle;
		}
		if (command.dstArrowStyle != null) {
			dstArrowStyle = command.dstArrowStyle;
		}
	}

	/**
	 * Executes a command of type {@link ECommandType#TEXT}.
	 */
	private static void executeTextCommand(Command command, GC gc) {
		final Rectangle area = command.area.toRectangle();
		String text = command.text;
		if (gc.stringExtent(text).x > area.width) {
			/* Truncate text, so that it fits inside the area. */
			while (text.length() > 0
					&& gc.stringExtent(text + "...").x > area.width) {
				text = text.substring(0, text.length() - 1);
			}
			text = text + "...";
			while (text.length() > 0 && gc.stringExtent(text).x > area.width) {
				text = text.substring(0, text.length() - 1);
			}
		}
		/* Center text within the area. */
		final Point extent = gc.stringExtent(text);
		final int x = area.x + (area.width - extent.x) / 2;
		final int y = area.y + (area.height - extent.y) / 2;
		gc.drawString(text, x, y, true);
	}

	/**
	 * Executes a command of type {@link ECommandType#LINE}.
	 */
	private void executeLineCommand(Command command, GC gc) {
		if (!drawEnabled) {
			return;
		}
		final Rectangle area = command.area.toRectangle();
		final int len = (int) round(sqrt(area.width * area.width + area.height
				* area.height));
		final float alpha = (float) (180.0 / PI * atan2(area.height, area.width));

		final Transform transform = new Transform(gc.getDevice());
		transform.translate(area.x, area.y);
		transform.rotate(alpha);
		gc.setTransform(transform);

		gc.drawLine(0, 0, len, 0);
		fillSrcArrow(srcArrowStyle, 0, gc);
		fillDstArrow(dstArrowStyle, len, gc);

		gc.setTransform(null);
		transform.dispose();
	}

	/**
	 * Executes a command of type {@link ECommandType#CONNECTOR}.
	 */
	private void executeConnectorCommand(Command command, GC gc) {
		if (!drawEnabled) {
			return;
		}
		final Rectangle area = command.area.toRectangle();
		final int len = (int) round(sqrt(area.width * area.width + area.height
				* area.height));
		final float alpha = (float) (180.0 / PI * atan2(area.height, area.width));

		final Transform lineTransform = new Transform(gc.getDevice());
		lineTransform.translate(area.x, area.y);
		lineTransform.rotate(alpha);
		gc.setTransform(lineTransform);

		final Path line = new Path(gc.getDevice());
		final double ca = cos(alpha / 180.0 * PI);
		final double sa = sin(alpha / 180.0 * PI);
		final int srcShift = (int) round(command.srcRadiusX * ca
				+ command.srcRadiusY * sa);
		final int dstShift = (int) round(command.dstRadiusX * ca
				+ command.dstRadiusY * sa);
		final int visibleLen = len - srcShift - dstShift;
		line.moveTo(0, 0);
		line.cubicTo(srcShift + CUBIC_LINE_DIVISOR * visibleLen, command.bend,
				srcShift + (1 - CUBIC_LINE_DIVISOR) * visibleLen, command.bend,
				len, 0);
		gc.drawPath(line);
		line.dispose();

		// TODO: Rotate arrows.
		fillSrcArrow(srcArrowStyle, srcShift, gc);
		fillDstArrow(dstArrowStyle, len - dstShift, gc);

		gc.setTransform(null);
		lineTransform.dispose();
	}

	/**
	 * Executes a command of type {@link ECommandType#RECTANGLE}.
	 */
	private void executeRectangleCommand(Command command, GC gc) {
		final Rectangle area = command.area.toRectangle();
		if (fillEnabled) {
			gc.fillRectangle(area);
		}
		if (drawEnabled) {
			gc.drawRectangle(area);
		}
	}

	/**
	 * Executes a command of type {@link ECommandType#ROUNDED_RECTANGLE}.
	 */
	private void executeRoundedRectangleCommand(Command command, GC gc) {
		final Rectangle area = command.area.toRectangle();
		final int cornerRadiusX = round(command.cornerRadiusX);
		final int cornerRadiusY = round(command.cornerRadiusY);
		if (fillEnabled) {
			gc.fillRoundRectangle(area.x, area.y, area.width, area.height,
					cornerRadiusX, cornerRadiusY);
		}
		if (drawEnabled) {
			gc.drawRoundRectangle(area.x, area.y, area.width, area.height,
					cornerRadiusX, cornerRadiusY);
		}
	}

	/**
	 * Executes a command of type {@link ECommandType#ELLIPSE}.
	 */
	private void executeEllipseCommand(Command command, GC gc) {
		final Rectangle area = command.area.toRectangle();
		if (fillEnabled) {
			gc.fillOval(area.x, area.y, area.width, area.height);
		}
		if (drawEnabled) {
			gc.drawOval(area.x, area.y, area.width, area.height);
		}
	}

	/**
	 * Paints an arrow polygon for the source side of lines and connectors.
	 */
	private static void fillSrcArrow(EArrowStyle style, int shift, GC gc) {
		final int[] arrow;
		switch (style) {
		case NONE:
			/* Nothing to paint. */
			return;
		case NORMAL:
			arrow = new int[] { shift, 0, shift + ARROW_LENGTH,
					-ARROW_WIDTH / 2, shift + ARROW_LENGTH, ARROW_WIDTH / 2 };
			gc.fillPolygon(arrow);
			gc.drawPolygon(arrow);
			break;
		case STEALTH:
			arrow = new int[] { shift, 0, shift + ARROW_LENGTH,
					-ARROW_WIDTH / 2, shift + ARROW_LENGTH / 2, 0,
					shift + ARROW_LENGTH, ARROW_WIDTH / 2 };
			gc.fillPolygon(arrow);
			gc.drawPolygon(arrow);
			break;
		}
	}

	/**
	 * Paints an arrow polygon for the destination side of lines and connectors.
	 */
	private static void fillDstArrow(EArrowStyle style, int shift, GC gc) {
		final int[] arrow;
		switch (style) {
		case NONE:
			/* Nothing to paint. */
			return;
		case NORMAL:
			arrow = new int[] { shift, 0, shift - ARROW_LENGTH,
					-ARROW_WIDTH / 2, shift - ARROW_LENGTH, ARROW_WIDTH / 2 };
			gc.fillPolygon(arrow);
			gc.drawPolygon(arrow);
			break;
		case STEALTH:
			arrow = new int[] { shift, 0, shift - ARROW_LENGTH,
					-ARROW_WIDTH / 2, shift - ARROW_LENGTH / 2, 0,
					shift - ARROW_LENGTH, ARROW_WIDTH / 2 };
			gc.fillPolygon(arrow);
			gc.drawPolygon(arrow);
			break;
		}
	}

	/**
	 * Returns a color with the given red, green and blue values from the
	 * {@link #colors}-cache.
	 */
	private Color getColor(RGB rgb, Device device) {
		Color color = colors.get(rgb);
		if (color == null) {
			color = new Color(device, rgb);
			colors.put(rgb, color);
		}
		return color;
	}

}
