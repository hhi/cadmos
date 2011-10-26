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

package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.Assert.assertTrue;
import static java.util.Arrays.asList;

import java.util.Deque;
import java.util.LinkedList;

import edu.tum.cs.cadmos.commons.IListSet;
import edu.tum.cs.cadmos.commons.ListSet;

/**
 * A set of utility methods for operations on classes in the <span
 * style="font-variant:small-caps">Cadmos</span> core model package.
 * 
 * @author wolfgang.schwitzer
 * @author $Author$
 * @version $Rev$
 * @ConQAT.Rating RED Hash:
 */
public class ModelUtils {

	/**
	 * Returns a set of all atomic components recursively found in the given
	 * <i>component</i> in DFS order or the <i>component</i> itself if it is
	 * already an atomic component.
	 */
	public static IListSet<IAtomicComponent> getAtomicComponents(
			IComponent component) {
		if (component instanceof IAtomicComponent) {
			return new ListSet<>((IAtomicComponent) component);
		}
		assertTrue(component instanceof ICompositeComponent,
				"Expected ICompositeComponent, but was '%s'", component);
		final ListSet<IAtomicComponent> result = new ListSet<>();
		for (final IComponent child : ((ICompositeComponent) component)
				.getChildren()) {
			result.addAll(getAtomicComponents(child));
		}
		return result;
	}

	/**
	 * Returns an outbound channel of any of the children of the given
	 * <i>parent</i>, equal to the given <i>channel</i> or <code>null</code> if
	 * no such outbound channel exists.
	 * <p>
	 * A channel is <i>outbound</i> if its destination is <code>null</code>.
	 * <p>
	 * For the returned channel <code>c</code> either
	 * <code>(c.getDst() == null) && c.equals(channel) && (c.getSrc().getParent() == parent)</code>
	 * holds or <code>c == null</code>.
	 */
	public static IChannel getOutboundChildChannel(ICompositeComponent parent,
			IChannel channel) {
		for (final IComponent child : parent.getChildren()) {
			final IChannel candidate = child.getOutgoing().get(channel);
			if (candidate != null && candidate.getDst() == null) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * Returns an inbound channel of any of the children of the given
	 * <i>parent</i>, equal to the given <i>channel</i> or <code>null</code> if
	 * no such inbound channel exists.
	 * <p>
	 * A channel is <i>inbound</i> if its source is <code>null</code>.
	 * <p>
	 * For the returned channel <code>c</code> either
	 * <code>(c.getSrc() == null) && c.equals(channel) && (c.getDst().getParent() == parent)</code>
	 * holds or <code>c == null</code>.
	 */
	public static IChannel getInboundChildChannel(ICompositeComponent parent,
			IChannel channel) {
		for (final IComponent child : parent.getChildren()) {
			final IChannel candidate = child.getIncoming().get(channel);
			if (candidate != null && candidate.getSrc() == null) {
				return candidate;
			}
		}
		return null;
	}

	/**
	 * Returns the path of channels to the atomic source component of the given
	 * <i>channel</i> that is transitively reachable within the given
	 * <i>systemBoundary</i> or throws an {@link AssertionError} if no such
	 * source exists.
	 * <p>
	 * The path is returned as a {@link Deque} of {@link IChannel}s with the
	 * channel going out of the transitive source as first element and the given
	 * <i>channel</i> as last element.
	 * 
	 * @throws AssertionError
	 *             if the component model is no well-formed and, hence, the
	 *             transitive source component is not within the system
	 *             boundary.
	 */
	public static Deque<IChannel> getSrcPath(IChannel channel,
			ICompositeComponent systemBoundary) {
		final IComponent src = channel.getSrc();
		if (src instanceof IAtomicComponent) {
			return new LinkedList<>(asList(channel));
		}
		if (src instanceof ICompositeComponent) {
			final ICompositeComponent compSrc = (ICompositeComponent) src;
			final IChannel outbound = getOutboundChildChannel(compSrc, channel);
			assertNotNull(outbound, "outbound");
			final Deque<IChannel> path = getSrcPath(outbound, systemBoundary);
			path.addLast(channel);
			return path;
		}
		final IComponent dst = channel.getDst();
		assertNotNull(dst, "dst");
		final ICompositeComponent parent = dst.getParent();
		if (src == null && (parent == systemBoundary || parent == null)) {
			return new LinkedList<>(asList(channel));
		}
		if (src == null && parent != systemBoundary) {
			final IChannel incoming = parent.getIncoming().get(channel);
			assertNotNull(incoming, "incoming");
			final Deque<IChannel> path = getSrcPath(incoming, systemBoundary);
			path.addLast(channel);
			return path;
		}
		throw new AssertionError("Cannot find path to transitive source for '"
				+ channel + "' within system boundary '" + systemBoundary + "'");
	}

	/**
	 * Returns the path of channels to the atomic destination component of the
	 * given <i>channel</i> that is transitively reachable within the given
	 * <i>systemBoundary</i> or throws an {@link AssertionError} if no such
	 * source exists.
	 * <p>
	 * The path is returned as a {@link Deque} of {@link IChannel}s with the
	 * given <i>channel</i> as first element and the channel incoming to the
	 * transitive destination as last element.
	 * 
	 * @throws AssertionError
	 *             if the component model is no well-formed and, hence, the
	 *             transitive destination component is not within the system
	 *             boundary.
	 */
	public static Deque<IChannel> getDstPath(IChannel channel,
			ICompositeComponent systemBoundary) {
		final IComponent dst = channel.getDst();
		if (dst instanceof IAtomicComponent) {
			return new LinkedList<>(asList(channel));
		}
		if (dst instanceof ICompositeComponent) {
			final ICompositeComponent compDst = (ICompositeComponent) dst;
			final IChannel inbound = getInboundChildChannel(compDst, channel);
			assertNotNull(inbound, "inbound");
			final Deque<IChannel> path = getDstPath(inbound, systemBoundary);
			path.addFirst(channel);
			return path;
		}
		final IComponent src = channel.getSrc();
		assertNotNull(src, "src");
		final ICompositeComponent parent = src.getParent();
		if (dst == null && (parent == systemBoundary || parent == null)) {
			return new LinkedList<>(asList(channel));
		}
		if (dst == null && parent != systemBoundary) {
			final IChannel outgoing = parent.getOutgoing().get(channel);
			assertNotNull(outgoing, "outgoing");
			final Deque<IChannel> path = getDstPath(outgoing, systemBoundary);
			path.addFirst(channel);
			return path;
		}
		throw new AssertionError(
				"Cannot find path to transitive destination for '" + channel
						+ "' within system boundary '" + systemBoundary + "'");
	}

	public static IListSet<IComponent> transformAtomicComponentNetwork(
			ICompositeComponent systemBoundary) {
		/* Clone the network of children components. */
		final IListSet<IComponent> network = new ListSet<>();
		for (final IComponent child : systemBoundary.getChildren()) {
			network.add(child.clone(null));
		}
		/* Rewire. */

		// /* Rewire the channels connected to the children components. */
		// for (final IComponent child : network) {
		// for (final IChannel c : child.getIncoming()) {
		// if (c.getSrc() == null) {
		// assertNotNull(c.getDst(), "c.getDst()");
		// final IComponent newDst = network.get(c.getDst());
		// assertNotNull(newDst, "newDst");
		// final IChannel link = component.getIncoming().get(c);
		// assertNotNull(link, "link");
		// c.clone(newSrc, newDst);
		// }
		// }
		// for (final IChannel c : child.getOutgoing()) {
		// final IComponent newSrc = cloneChildren.get(c.getSrc());
		// final IComponent newDst;
		// if (c.getDst() == null) {
		// newDst = null;
		// } else {
		// newDst = cloneChildren.get(c.getDst());
		// }
		// c.clone(newSrc, newDst);
		// }
		// }

		return network;
	}

}
