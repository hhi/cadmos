/*--------------------------------------------------------------------------+
|                                                                          |
| Copyright 2008-2011 Technische Universitaet Muenchen                     |
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

package edu.tum.cs.cadmos.commons.core;

import java.util.HashMap;
import java.util.HashSet;

/**
 * A class that combines two different types of elements into one object.
 * <p>
 * This class can be used in {@link HashMap} and {@link HashSet}. The
 * {@link #equals(Object)}-method is overridden to return equality if the two
 * first and the two second elements are equal
 * 
 * @author nvpopa@gmail.com
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash: 46970363DBB8953DAA2A82D14755A8A7
 */
public class Pair<F, S> {

	/** The first element. */
	private final F first;

	/** The second element. */
	private final S second;

	/**
	 * Creates a new pair with first and second element.
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/** Returns the first element. */
	public F getFirst() {
		return first;
	}

	/** Returns the second element. */
	public S getSecond() {
		return second;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns <code>true</code> if the other object is a {@link Pair} and the
	 * two first elements as well as the two second elements are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) {
			return false;
		}
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		return first.equals(other.first) && second.equals(other.second);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns a combined hash code of the first and second element.
	 */
	@Override
	public int hashCode() {
		return first.hashCode() + second.hashCode();
	}

	/** {@inheritDoc} */

	@Override
	public String toString() {
		return "(" + first.toString() + ", " + second.toString() + ")";
	}

}
