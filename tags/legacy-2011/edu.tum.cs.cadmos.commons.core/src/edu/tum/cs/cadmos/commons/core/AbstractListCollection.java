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

package edu.tum.cs.cadmos.commons.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class implementation of the {@link IListCollection} interface.
 * 
 * @param E
 *            the element type.
 * @param R
 *            the return type of {@link #get(IIdentifiable)} and
 *            {@link #get(String)}, which is usually a {@link List} of elements
 *            of type E for multisets.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 * 
 * @see ListSet
 * @see ListMultiSet
 */
public abstract class AbstractListCollection<E extends IIdentifiable, R>
		implements IListCollection<E, R> {

	protected final List<E> list = new ArrayList<>();

	protected final Map<String, R> map = new HashMap<>();

	private final IConsistencyVerifier consistencyVerifier;

	/**
	 * Creates a new AbstractListCollection.
	 */
	protected AbstractListCollection(IConsistencyVerifier consistencyVerifier) {
		this.consistencyVerifier = consistencyVerifier;
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	protected void verifyConsistentAdd(E element) {
		if (consistencyVerifier != null) {
			consistencyVerifier.verifyConsistentAdd(this, element);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void addAll(IListCollection<E, ?> elements) {
		for (final E e : elements) {
			add(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void addAll(Collection<E> elements) {
		for (final E e : elements) {
			add(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(E element) {
		return contains(element.getId());
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(String id) {
		return map.containsKey(id);
	}

	/** {@inheritDoc} */
	@Override
	public R get(E element) {
		return get(element.getId());
	}

	/** {@inheritDoc} */
	@Override
	public E get(int index) {
		return list.get(index);
	}

	/** {@inheritDoc} */
	@Override
	public E getFirst() {
		return list.get(0);
	}

	/** {@inheritDoc} */
	@Override
	public E getLast() {
		return list.get(list.size() - 1);
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return list.size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public List<E> toList() {
		return new ArrayList<>(list);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other) {
		return other instanceof AbstractListCollection<?, ?>
				&& ((AbstractListCollection<?, ?>) other).list.equals(list);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return list.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return list.toString();
	}

}
