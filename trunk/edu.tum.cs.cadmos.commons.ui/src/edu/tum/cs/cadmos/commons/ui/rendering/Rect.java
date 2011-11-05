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

import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Collection;

/**
 * A rectangular area defined by two points (x1, y1) and (x2, y2).
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class Rect {

	public final float x1;

	public final float y1;

	public final float x2;

	public final float y2;

	/**
	 * Creates a new rectangular area.
	 */
	public Rect(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public Rect move(float deltax, float deltay) {
		return new Rect(x1 + deltax, y1 + deltay, x2 + deltax, y2 + deltay);
	}

	public Rect resize(float deltax, float deltay) {
		return new Rect(x1 - deltax, y1 - deltay, x2 + deltax, y2 + deltay);
	}

	public Rect grow(float radiusx, float radiusy) {
		return new Rect(x1 - .5f * radiusx, y1 - .5f * radiusy, x2 + .5f
				* radiusx, y2 + .5f * radiusy);
	}

	public Rect scale(float scalex, float scaley) {
		return new Rect(scalex * x1, scaley * y1, scalex * x2, scaley * y2);
	}

	public Rect scale(float scale, Rect src, Rect dst) {
		final float deltax = .5f * (dst.width() - scale * src.width());
		final float deltay = .5f * (dst.height() - scale * src.height());
		final float rx1 = deltax + scale * (x1 - src.x1);
		final float rx2 = deltax + scale * (x2 - src.x1);
		final float ry1 = deltay + scale * (y1 - src.y1);
		final float ry2 = deltay + scale * (y2 - src.y1);
		return new Rect(rx1, ry1, rx2, ry2);
	}

	public Rect leftLine() {
		return new Rect(x1, y1, x1, y2);
	}

	public Rect rightLine() {
		return new Rect(x2, y1, x2, y2);
	}

	public Rect topLine() {
		return new Rect(x1, y1, x2, y1);
	}

	public Rect bottomLine() {
		return new Rect(x1, y2, x2, y2);
	}

	public float width() {
		return abs(x2 - x1);
	}

	public float height() {
		return abs(y2 - y1);
	}

	public float centerX() {
		return .5f * (x1 + x2);
	}

	public float centerY() {
		return .5f * (y1 + y2);
	}

	public static Rect cover(Collection<Rect> rects) {
		assertTrue(!rects.isEmpty(), "Expected 'rectangles' to be not empty");
		float x1 = Float.MAX_VALUE;
		float y1 = Float.MAX_VALUE;
		float x2 = -Float.MAX_VALUE;
		float y2 = -Float.MAX_VALUE;
		for (final Rect r : rects) {
			x1 = min(x1, r.x1);
			y1 = min(y1, r.y1);
			x2 = max(x2, r.x2);
			y2 = max(y2, r.y2);
		}
		return new Rect(x1, y1, x2, y2);
	}

}
