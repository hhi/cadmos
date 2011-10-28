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

import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import edu.tum.cs.cadmos.commons.core.Assert;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListSet;

/**
 * A set of utility methods for operations on classes in the <span
 * style="font-variant:small-caps">Cadmos</span> core model package.
 * 
 * @author wolfgang.schwitzer
 * @author nvpopa@gmail.com
 * @author dominik.chessa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash: 25C6DC14D5E2667733012506A11748EC
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
	 * Returns the outbound channel of any of the children of the given
	 * <i>parent</i>, equal to the given <i>channel</i> or <code>null</code> if
	 * no such outbound channels exist.
	 * <p>
	 * Note that a channel is <i>outbound</i> if its destination is
	 * <code>null</code>.
	 * 
	 * @throws AssertionError
	 *             if more than 1 outbound channel has an equal id to the given
	 *             <i>channel</i>.
	 */
	public static IChannel getOutboundChildChannel(ICompositeComponent parent,
			IChannel channel) {
		final List<IChannel> result = new ArrayList<>();
		for (final IComponent child : parent.getChildren()) {
			for (final IChannel candidate : child.getOutgoing().get(channel)) {
				if (candidate.getDst() == null) {
					result.add(candidate);
				}
			}
		}
		Assert.assertTrue(
				result.size() <= 1,
				"Expected exactly 0 or 1 outbound channel with id '%s' in parent '%s', but found %s",
				channel.getId(), parent, result);
		if (result.size() == 0) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * Returns the inbound channels of any of the children of the given
	 * <i>parent</i>, equal to the given <i>channel</i> or an empty list if no
	 * such inbound channels exist.
	 * <p>
	 * Note that a channel is <i>inbound</i> if its source is <code>null</code>.
	 */
	public static List<IChannel> getInboundChildChannels(
			ICompositeComponent parent, IChannel channel) {
		final List<IChannel> result = new ArrayList<>();
		for (final IComponent child : parent.getChildren()) {
			final IChannel candidate = child.getIncoming().get(channel);
			if (candidate != null && candidate.getSrc() == null) {
				result.add(candidate);
			}
		}
		return result;
	}

	/**
	 * Returns the path of channels to the atomic source component of the given
	 * <i>channel</i> that is transitively reachable within the given
	 * <i>systemBoundary</i> or throws an {@link AssertionError} if no such
	 * source exists.
	 * <p>
	 * The path is returned as a {@link Deque} of {@link IChannel}s with the
	 * channel going out of the transitive source (e.g. <i>srcOutgoing</i>) as
	 * first element and the given <i>channel</i> as last element:
	 * <code>[<i>srcOutgoing</i>, ..., <i>channel</i>]</code>.
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
			return createPath(channel);
		}
		if (src instanceof ICompositeComponent) {
			final ICompositeComponent compSrc = (ICompositeComponent) src;
			final IChannel outbound = getOutboundChildChannel(compSrc, channel);
			return extendSrcPath(outbound, systemBoundary, channel);
		}
		final IComponent dst = channel.getDst();
		assertNotNull(dst, "dst");
		final ICompositeComponent parent = dst.getParent();
		if (src == null && (parent == systemBoundary || parent == null)) {
			return createPath(channel);
		}
		if (src == null && parent != systemBoundary) {
			final IChannel incoming = parent.getIncoming().get(channel);
			return extendSrcPath(incoming, systemBoundary, channel);
		}
		throw new AssertionError("Cannot find path to transitive source for '"
				+ channel + "' within system boundary '" + systemBoundary + "'");
	}

	/**
	 * Returns a path initialized with the given <i>channel</i> as single
	 * element.
	 */
	private static Deque<IChannel> createPath(IChannel channel) {
		return new LinkedList<>(asList(channel));
	}

	/**
	 * Returns the result of {@link #getSrcPath(IChannel, ICompositeComponent)}
	 * for the given <i>nextChannel</i> extended by the given <i>channel</i> as
	 * last element.
	 */
	private static Deque<IChannel> extendSrcPath(final IChannel nextChannel,
			ICompositeComponent systemBoundary, IChannel channel) {
		assertNotNull(nextChannel, "nextChannel");
		final Deque<IChannel> path = getSrcPath(nextChannel, systemBoundary);
		path.addLast(channel);
		return path;
	}

	/**
	 * Returns a list of paths of channels to the atomic destination components
	 * of the given <i>channel</i> that are transitively reachable within the
	 * given <i>systemBoundary</i> or throws an {@link AssertionError} if no
	 * such destination exists.
	 * <p>
	 * Each of the paths is returned as a {@link Deque} of {@link IChannel}s
	 * with the given <i>channel</i> as first element and the channel incoming
	 * to a transitive destination (e.g. <i>dstIncoming</i>) as last element:
	 * <code>[<i>channel</i>, ..., <i>dstIncoming</i>]</code>.
	 * <p>
	 * The result is a list of all such paths leading from the given
	 * <i>channel</i> to different destinations (e.g.
	 * <i>dstIncoming<sub>1</sub></i>, ..., <i>dstIncoming<sub>n</sub></i>):
	 * <code>[[<i>channel</i>, ..., <i>dstIncoming<sub>1</sub></i>], ..., [<i>channel</i>, ..., <i>dstIncoming<sub>n</sub></i>]]</code>.
	 * 
	 * @throws AssertionError
	 *             if the component model is no well-formed and, hence, a
	 *             transitive destination component is not within the system
	 *             boundary.
	 */
	public static List<Deque<IChannel>> getDstPaths(IChannel channel,
			ICompositeComponent systemBoundary) {
		final IComponent dst = channel.getDst();
		if (dst instanceof IAtomicComponent) {
			return createPaths(channel);
		}
		if (dst instanceof ICompositeComponent) {
			final ICompositeComponent compDst = (ICompositeComponent) dst;
			final List<IChannel> inbound = getInboundChildChannels(compDst,
					channel);
			return extendDstPaths(inbound, systemBoundary, channel);
		}
		assertTrue(dst == null, "Expected 'dst' to be null, but was '%s'", dst);
		final IComponent src = channel.getSrc();
		assertNotNull(src, "src");
		final ICompositeComponent parent = src.getParent();
		if (dst == null && (parent == systemBoundary || parent == null)) {
			return createPaths(channel);
		}
		if (dst == null && parent != systemBoundary) {
			final List<IChannel> outgoing = parent.getOutgoing().get(channel);
			return extendDstPaths(outgoing, systemBoundary, channel);
		}
		throw new AssertionError(
				"Cannot find path to transitive destination for '" + channel
						+ "' within system boundary '" + systemBoundary + "'");
	}

	/**
	 * Returns the result of {@link #getDstPaths(IChannel, ICompositeComponent)}
	 * for all given <i>nextChannels</i>, each extended by the given
	 * <i>channel</i> as first element.
	 */
	private static List<Deque<IChannel>> extendDstPaths(
			final List<IChannel> nextChannels,
			ICompositeComponent systemBoundary, IChannel channel) {
		assertTrue(nextChannels.size() > 0,
				"Expected 'nextChannels' to have at least 1 element, but was 0");
		final List<Deque<IChannel>> result = new LinkedList<>();
		for (final IChannel inbound : nextChannels) {
			final List<Deque<IChannel>> paths = getDstPaths(inbound,
					systemBoundary);
			for (final Deque<IChannel> path : paths) {
				path.addFirst(channel);
				result.add(path);
			}
		}
		return result;
	}

	/**
	 * Returns a list of paths initialized with one path having the given
	 * <i>channel</i> as single element.
	 */
	private static List<Deque<IChannel>> createPaths(IChannel channel) {
		final List<Deque<IChannel>> result = new LinkedList<>();
		result.add(createPath(channel));
		return result;
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
