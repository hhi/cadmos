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
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.RGB;

import edu.tum.cs.cadmos.commons.core.IElement;

/**
 * A context for rendering {@link Command}s.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class RenderingContext {

	private final List<Command> commands = new ArrayList<>();

	/** Returns the commands. */
	public List<Command> getCommands() {
		return commands;
	}

	public void scale(Rect dst) {
		scale(getCover(), dst);
	}

	public void scale(Rect src, Rect dst) {
		final float scaleX = dst.width() / src.width();
		final float scaleY = dst.height() / src.height();
		final float scale = min(scaleX, scaleY);
		for (final Command command : commands) {
			command.scale(scale, src, dst);
		}
	}

	public Rect getCover() {
		final List<Rect> rects = new ArrayList<>(commands.size());
		for (final Command command : commands) {
			if (command.area != null) {
				rects.add(command.area);
			}
		}
		return Rect.cover(rects);
	}

	/** Sets the draw color. */
	public RenderingContext setDrawColor(RGB color) {
		final Command command = new Command(SET_DRAW, null);
		command.drawColor = color;
		commands.add(command);
		return this;
	}

	/** Sets the draw color. */
	public RenderingContext setDrawColor(int r, int g, int b) {
		return setDrawColor(new RGB(r, g, b));
	}

	/** Sets the draw style. */
	public RenderingContext setDrawStyle(EDrawStyle style) {
		final Command command = new Command(SET_DRAW, null);
		command.drawStyle = style;
		commands.add(command);
		return this;
	}

	/** Sets the draw style to {@link EDrawStyle#NONE}. */
	public RenderingContext setNoDraw() {
		return setDrawStyle(EDrawStyle.NONE);
	}

	/** Sets the draw style to {@link EDrawStyle#SOLID}. */
	public RenderingContext setSolidDraw() {
		return setDrawStyle(EDrawStyle.SOLID);
	}

	/** Sets the draw color and style. */
	public RenderingContext setDraw(RGB color, EDrawStyle style) {
		final Command command = new Command(SET_DRAW, null);
		command.drawColor = color;
		command.drawStyle = style;
		commands.add(command);
		return this;
	}

	/** Sets the fill color. */
	public RenderingContext setFillColor(RGB color) {
		final Command command = new Command(SET_FILL, null);
		command.fillColor = color;
		commands.add(command);
		return this;
	}

	/** Sets the fill color. */
	public RenderingContext setFillColor(int r, int g, int b) {
		return setFillColor(new RGB(r, g, b));
	}

	/** Sets the fill style. */
	public RenderingContext setFillStyle(EFillStyle style) {
		final Command command = new Command(SET_FILL, null);
		command.fillStyle = style;
		commands.add(command);
		return this;
	}

	/** Sets the fill style to {@link EFillStyle#NONE}. */
	public RenderingContext setNoFill() {
		return setFillStyle(EFillStyle.NONE);
	}

	/** Sets the fill style to {@link EFillStyle#SOLID}. */
	public RenderingContext setSolidFill() {
		return setFillStyle(EFillStyle.SOLID);
	}

	/** Sets the fill color and style. */
	public RenderingContext setFill(RGB color, EFillStyle style) {
		final Command command = new Command(SET_FILL, null);
		command.fillColor = color;
		command.fillStyle = style;
		commands.add(command);
		return this;
	}

	/** Sets the opacity of draw and fill operations: [0.0..1.0]. */
	public RenderingContext setOpacity(float opacity) {
		assertWithinRange(opacity, 0f, 1f, "opacity");
		final Command command = new Command(SET_OPACITY, null);
		command.opacity = opacity;
		commands.add(command);
		return this;
	}

	/** Sets the font size. */
	public RenderingContext setFontSize(EFontSize size) {
		final Command command = new Command(SET_FONT_SIZE, null);
		command.fontSize = size;
		commands.add(command);
		return this;
	}

	/** Sets the arrow styles. */
	public RenderingContext setArrows(EArrowStyle srcArrowStyle,
			EArrowStyle dstArrowStyle) {
		final Command command = new Command(SET_ARROWS, null);
		command.srcArrowStyle = srcArrowStyle;
		command.dstArrowStyle = dstArrowStyle;
		commands.add(command);
		return this;
	}

	/** Draws text centered and clipped in the area. */
	public RenderingContext drawText(IElement element, String text, Rect area) {
		assertNotNull(text, "text");
		assertNotNull(area, "area");
		final Command command = new Command(TEXT, element);
		command.text = text;
		command.area = area;
		commands.add(command);
		return this;
	}

	/** Draws a line inside the area from (x1, y1) to (x2, y2). */
	public RenderingContext drawLine(IElement element, Rect area) {
		assertNotNull(area, "area");
		final Command command = new Command(LINE, element);
		command.area = area;
		commands.add(command);
		return this;
	}

	/**
	 * Draws a connector from the area's (x1, y1) to (x2, y2) labeled with the
	 * optionally given text, respecting the extent of the source and
	 * destination areas.
	 */
	public RenderingContext drawConnector(IElement element, Rect area,
			float srcRadiusX, float srcRadiusY, float dstRadiusX,
			float dstRadiusY, String text, float bend) {
		assertNotNull(area, "area");
		final Command command = new Command(CONNECTOR, element);
		command.area = area;
		command.srcRadiusX = srcRadiusX;
		command.srcRadiusY = srcRadiusY;
		command.dstRadiusX = dstRadiusX;
		command.dstRadiusY = dstRadiusY;
		command.text = text;
		command.bend = bend;
		commands.add(command);
		return this;
	}

	/**
	 * /** Draws and fills a rectangle on the given area.
	 */
	public RenderingContext paintRectangle(IElement element, Rect area) {
		final Command command = new Command(RECTANGLE, element);
		command.area = area;
		commands.add(command);
		return this;
	}

	/** Draws and fills a rounded rectangle on the given area. */
	public RenderingContext paintRoundedRectangle(IElement element, Rect area,
			float cornerRadiusX, float cornerRadiusY) {
		final Command command = new Command(ROUNDED_RECTANGLE, element);
		command.area = area;
		command.cornerRadiusX = cornerRadiusX;
		command.cornerRadiusY = cornerRadiusY;
		commands.add(command);
		return this;
	}

	/** Draws and fills an ellipse inside the given area. */
	public RenderingContext paintEllipse(IElement element, Rect area) {
		final Command command = new Command(ELLIPSE, element);
		command.area = area;
		commands.add(command);
		return this;
	}

}
