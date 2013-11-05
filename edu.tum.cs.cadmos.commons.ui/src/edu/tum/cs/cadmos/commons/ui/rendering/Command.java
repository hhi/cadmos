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
	 * commands, where 0.0 is fully transparent and 1.0 is fully opaque. Remains
	 * unchanged if value is <code>null</code>.
	 */
	public Float opacity;

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
	public Rect area;

	/** The radius in x-direction covered by the source of a connector. */
	public float srcRadiusX;

	/** The radius in y-direction covered by the source of a connector. */
	public float srcRadiusY;

	/** The radius in x-direction covered by the destination of a connector. */
	public float dstRadiusX;

	/** The radius in y-direction covered by the destination of a connector. */
	public float dstRadiusY;

	/** The corner radius in x-direction of rounded rectangles. */
	public float cornerRadiusX;

	/** The corner radius in y-direction of rounded rectangles. */
	public float cornerRadiusY;

	/** The additional bend of a connector that has multiple siblings. */
	public float bend;

	/**
	 * Creates a new rendering command with the given <i>type</i> and associated
	 * <i>element</i>.
	 * <p>
	 * Note that commands cannot be instantiated by clients directly, since this
	 * is a package-visibility constructor. Use the several methods provided by
	 * {@link RenderingContext} instead to create commands in a rendering
	 * context.
	 */
	/* package */Command(ECommandType type, IElement element) {
		this.type = type;
		this.element = element;
	}

	public void scale(float scale, Rect src, Rect dst) {
		if (area != null) {
			area = area.scale(scale, src, dst);
		}
		srcRadiusX *= scale;
		srcRadiusY *= scale;
		dstRadiusX *= scale;
		dstRadiusY *= scale;
		cornerRadiusX *= scale;
		cornerRadiusY *= scale;
		bend *= scale;
	}

}
