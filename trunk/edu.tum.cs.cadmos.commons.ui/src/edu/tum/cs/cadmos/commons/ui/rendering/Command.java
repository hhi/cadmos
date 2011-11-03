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

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.core.Assert.assertWithinRange;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.CONNECTOR;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.ELLIPSE;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.LINE;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.RECTANGLE;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.ROUNDED_RECTANGLE;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.SET_ARROWS;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.SET_DRAW;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.SET_FILL;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.SET_FONT_SIZE;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.SET_OPACITY;
import static edu.tum.cs.cadmos.commons.ui.rendering.ECommandType.TEXT;

import org.eclipse.swt.graphics.RGB;

import edu.tum.cs.cadmos.commons.core.IElement;

/**
 * A rendering command.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public final class Command {

	/**
	 * Defines the type of this rendering command. Use in switch-statements, so
	 * the compiler can give warnings on missing command types.
	 */
	public final ECommandType type;

	/** The element that is associated with this command. */
	public final IElement element;

	/**
	 * The color to use for shapes that are drawn by subsequent commands.
	 * Remains unchanged if value is <code>null</code>.
	 */
	public RGB drawColor;

	/**
	 * The style to use for shapes that are drawn by subsequent commands.
	 * Remains unchanged if value is <code>null</code>.
	 */
	public EDrawStyle drawStyle;

	/**
	 * The color to use for shapes that are filled by subsequent commands.
	 * Remains unchanged if value is <code>null</code>.
	 */
	public RGB fillColor;

	/**
	 * The style to use for shapes that are filled by subsequent commands.
	 * Remains unchanged if value is <code>null</code>.
	 */
	public EFillStyle fillStyle;

	/**
	 * The opacity to use for shapes that are drawn and filled by subsequent
	 * commands, where 0 is fully transparent and 255 is fully opaque. Remains
	 * unchanged if value is <code>null</code>.
	 */
	public Integer opacity;

	/**
	 * The relative font size to use for text that is rendered by subsequent
	 * commands. Remains unchanged if value is <code>null</code>.
	 */
	public EFontSize fontSize;

	/**
	 * The style of arrows to display on the source side of lines and connectors
	 * drawn by subsequent commands. Remains unchanged if value is
	 * <code>null</code>.
	 */
	public EArrowStyle srcArrowStyle;

	/**
	 * The style of arrows to display on the destination side of lines and
	 * connectors drawn by subsequent commands. Remains unchanged if value is
	 * <code>null</code>.
	 */
	public EArrowStyle dstArrowStyle;

	/** The text to render by a text command. */
	public String text;

	/**
	 * The draw and fill area of lines, connectors, text, rectangles and
	 * ellipses.
	 */
	public Rectangle area;

	/** The area covered by the source of a connector. */
	public Rectangle srcArea;

	/** The area covered by the destination of a connector. */
	public Rectangle dstArea;

	/** The corner radius in x-direction of rounded rectangles. */
	public float cornerRadiusX;

	/** The corner radius in y-direction of rounded rectangles. */
	public float cornerRadiusY;

	/**
	 * Creates a new rendering command with the given <i>type</i> and associated
	 * <i>element</i>.
	 * <p>
	 * Note that commands cannot be instantiated directly, since this is a
	 * private constructor. Use the several static methods provided by the
	 * command class instead to create commands.
	 */
	private Command(ECommandType type, IElement element) {
		this.type = type;
		this.element = element;
	}

	/** Sets the draw color. */
	public static Command setDrawColor(RGB color) {
		final Command command = new Command(SET_DRAW, null);
		command.drawColor = color;
		return command;
	}

	/** Sets the draw style. */
	public static Command setDrawStyle(EDrawStyle style) {
		final Command command = new Command(SET_DRAW, null);
		command.drawStyle = style;
		return command;
	}

	/** Sets the draw color and style. */
	public static Command setDraw(RGB color, EDrawStyle style) {
		final Command command = new Command(SET_DRAW, null);
		command.drawColor = color;
		command.drawStyle = style;
		return command;
	}

	/** Sets the fill color. */
	public static Command setFillColor(RGB color) {
		final Command command = new Command(SET_FILL, null);
		command.fillColor = color;
		return command;
	}

	/** Sets the fill style. */
	public static Command setDrawStyle(EFillStyle style) {
		final Command command = new Command(SET_FILL, null);
		command.fillStyle = style;
		return command;
	}

	/** Sets the fill color and style. */
	public static Command setDraw(RGB color, EFillStyle style) {
		final Command command = new Command(SET_FILL, null);
		command.fillColor = color;
		command.fillStyle = style;
		return command;
	}

	/** Sets the opacity of draw and fill operations: [0..255]. */
	public static Command setOpacity(int opacity) {
		assertWithinRange(opacity, 0, 255, "opacity");
		final Command command = new Command(SET_OPACITY, null);
		command.opacity = opacity;
		return command;
	}

	/** Sets the font size. */
	public static Command setFontSize(EFontSize size) {
		final Command command = new Command(SET_FONT_SIZE, null);
		command.fontSize = size;
		return command;
	}

	/** Sets the arrow styles. */
	public static Command setArrows(EArrowStyle srcArrowStyle,
			EArrowStyle dstArrowStyle) {
		final Command command = new Command(SET_ARROWS, null);
		command.srcArrowStyle = srcArrowStyle;
		command.dstArrowStyle = dstArrowStyle;
		return command;
	}

	/** Draws text centered and clipped in the area. */
	public static Command drawText(IElement element, String text, Rectangle area) {
		assertNotNull(text, "text");
		assertNotNull(area, "area");
		final Command command = new Command(TEXT, element);
		command.text = text;
		command.area = area;
		return command;
	}

	/** Draws text centered and clipped in the area. */
	public static Command drawText(String text, Rectangle area) {
		return drawText(null, text, area);
	}

	/** Draws a line inside the area from (x1, y1) to (x2, y2). */
	public static Command drawLine(IElement element, Rectangle area) {
		final Command command = new Command(LINE, element);
		command.area = area;
		return command;
	}

	/** Draws a line inside the area from (x1, y1) to (x2, y2). */
	public static Command drawLine(Rectangle area) {
		return drawLine(null, area);
	}

	/**
	 * Draws a connector from the area's (x1, y1) to (x2, y2) and respects the
	 * extent of the source and destination areas.
	 */
	public static Command drawConnector(IElement element, Rectangle area,
			Rectangle srcArea, Rectangle dstArea) {
		final Command command = new Command(CONNECTOR, element);
		command.area = area;
		command.srcArea = srcArea;
		command.dstArea = dstArea;
		return command;
	}

	/**
	 * Draws a connector from the area's (x1, y1) to (x2, y2) and respects the
	 * extent of the source and destination areas.
	 */
	public static Command drawConnector(Rectangle area, Rectangle srcArea,
			Rectangle dstArea) {
		return drawConnector(null, area, srcArea, dstArea);
	}

	/** Draws and fills a rectangle on the given area. */
	public static Command paintRectangle(IElement element, Rectangle area) {
		final Command command = new Command(RECTANGLE, element);
		command.area = area;
		return command;
	}

	/** Draws and fills a rectangle on the given area. */
	public static Command paintRectangle(Rectangle area) {
		return paintRectangle(null, area);
	}

	/** Draws and fills a rounded rectangle on the given area. */
	public static Command paintRoundedRectangle(IElement element,
			Rectangle area, float cornerRadiusX, float cornerRadiusY) {
		final Command command = new Command(ROUNDED_RECTANGLE, element);
		command.area = area;
		command.cornerRadiusX = cornerRadiusX;
		command.cornerRadiusY = cornerRadiusY;
		return command;
	}

	/** Draws and fills a rounded rectangle on the given area. */
	public static Command paintRoundedRectangle(Rectangle area,
			float cornerRadiusX, float cornerRadiusY) {
		return paintRoundedRectangle(null, area, cornerRadiusX, cornerRadiusY);
	}

	/** Draws and fills an ellipse inside the given area. */
	public static Command paintEllipse(IElement element, Rectangle area) {
		final Command command = new Command(ELLIPSE, element);
		command.area = area;
		return command;
	}

	/** Draws and fills an ellipse inside the given area. */
	public static Command paintEllipse(Rectangle area) {
		return paintEllipse(null, area);
	}

}
