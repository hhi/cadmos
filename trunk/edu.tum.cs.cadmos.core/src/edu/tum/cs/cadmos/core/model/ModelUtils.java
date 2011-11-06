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

import static edu.tum.cs.cadmos.commons.core.Assert.assertInstanceOf;
import static edu.tum.cs.cadmos.commons.core.Assert.assertNotNull;
import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.model.ModelPackage.createChannel;
import static java.util.Arrays.asList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.tum.cs.cadmos.commons.core.Assert;
import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.commons.core.ListSet;
import edu.tum.cs.cadmos.core.expressions.IExpression;

/**
 * A set of utility methods for operations on classes in the <span
 * style="font-variant:small-caps">Cadmos</span> <code>core.model</code>
 * package.
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
	// TODO(VP): ensure that component is not null and return null or assertion
	// error otherwise.
	public static IListSet<IAtomicComponent> getAtomicComponents(
			IComponent component) {
		if (component instanceof IAtomicComponent) {
			return new ListSet<>((IAtomicComponent) component);
		}
		assertInstanceOf(component, ICompositeComponent.class, "component");
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
			IComponent systemBoundary) {
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
	 * Returns the result of {@link #getSrcPath(IChannel, IComponent)} for the
	 * given <i>nextChannel</i> extended by the given <i>channel</i> as last
	 * element.
	 */
	private static Deque<IChannel> extendSrcPath(final IChannel nextChannel,
			IComponent systemBoundary, IChannel channel) {
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
			IComponent systemBoundary) {
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
	 * Returns the result of {@link #getDstPaths(IChannel, IComponent)} for all
	 * given <i>nextChannels</i>, each extended by the given <i>channel</i> as
	 * first element.
	 */
	private static List<Deque<IChannel>> extendDstPaths(
			final List<IChannel> nextChannels, IComponent systemBoundary,
			IChannel channel) {
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

	public static IListSet<IAtomicComponent> transformAtomicComponentNetwork(
			IComponent systemBoundary) {
		/* Find the atomic components within the system boundary. */
		final IListSet<IAtomicComponent> atomicComponents = getAtomicComponents(systemBoundary);
		/* Clone the atomic components. */
		final IListSet<IAtomicComponent> clones = new ListSet<>();
		for (final IAtomicComponent atomicComponent : atomicComponents) {
			clones.add((IAtomicComponent) atomicComponent.clone(null));
		}
		/* Rewire the incoming paths. */
		for (final IChannel channel : systemBoundary.getIncoming()) {
			final List<Deque<IChannel>> dstPaths = getDstPaths(channel,
					systemBoundary);
			for (final Deque<IChannel> path : dstPaths) {
				assertTrue(path.size() > 0, "Expected path to be non-empty");
				final IComponent candidate = path.getLast().getDst();
				assertInstanceOf(candidate, IAtomicComponent.class, "dst");
				final IComponent dst = clones.get((IAtomicComponent) candidate);
				assertNotNull(dst, "dst");
				final List<IExpression> initialMessages = getPathInitialMessages(path);
				final SrcDstRate rate = getPathSrcDstRate(path);
				createChannel(path.getLast().getId(), path.getLast().getName(),
						path.getLast().getType(), null, dst, initialMessages,
						rate.srcRate, rate.dstRate);
			}
		}
		/* Rewire the outgoing paths. */
		for (final IChannel channel : systemBoundary.getOutgoing()) {
			final Deque<IChannel> path = getSrcPath(channel, systemBoundary);
			assertTrue(path.size() > 0, "Expected path to be non-empty");
			final IComponent candidate = path.getFirst().getSrc();
			assertInstanceOf(candidate, IAtomicComponent.class, "src");
			final IAtomicComponent src = clones
					.get((IAtomicComponent) candidate);
			assertNotNull(src, "src");
			final List<IExpression> initialMessages = getPathInitialMessages(path);
			final SrcDstRate rate = getPathSrcDstRate(path);
			createChannel(path.getLast().getId(), path.getLast().getName(),
					path.getLast().getType(), src, null, initialMessages,
					rate.srcRate, rate.dstRate);
		}
		/* Rewire the internal paths. */
		for (final IAtomicComponent atomicComponent : atomicComponents) {
			final IAtomicComponent src = clones.get(atomicComponent);
			assertNotNull(src, "src");
			for (final IChannel channel : atomicComponent.getOutgoing()) {
				final List<Deque<IChannel>> dstPaths = getDstPaths(channel,
						systemBoundary);
				for (final Deque<IChannel> path : dstPaths) {
					assertTrue(path.size() > 0, "Expected path to be non-empty");
					final IComponent candidate = path.getLast().getDst();
					if (candidate == null) {
						/* Skip channels that go out of the system boundary. */
						continue;
					}
					assertInstanceOf(candidate, IAtomicComponent.class, "dst");
					final IComponent dst = clones
							.get((IAtomicComponent) candidate);
					final List<IExpression> initialMessages = getPathInitialMessages(path);
					final SrcDstRate rate = getPathSrcDstRate(path);
					createChannel(path.getLast().getId(), path.getLast()
							.getName(), path.getLast().getType(), src, dst,
							initialMessages, rate.srcRate, rate.dstRate);
				}
			}
		}
		return clones;
	}

	private static class SrcDstRate {

		public final int srcRate;

		public final int dstRate;

		/**
		 * Creates an immutable ModelUtils.SrcDstRate.
		 */
		public SrcDstRate(int srcRate, int dstRate) {
			this.srcRate = srcRate;
			this.dstRate = dstRate;
		}

	}

	/**
	 * Returns the total source and destination rate of a path. Both rates are
	 * normalized according to their greatest common divisor.
	 */
	private static SrcDstRate getPathSrcDstRate(Deque<IChannel> path) {
		final BigInteger srcRate = getPathSrcRate(path);
		final BigInteger dstRate = getPathDstRate(path);
		final BigInteger gcd = srcRate.gcd(dstRate);
		return new SrcDstRate(srcRate.divide(gcd).intValue(), dstRate.divide(
				gcd).intValue());
	}

	/**
	 * Returns the initial messages on all channels of the given <i>path</i> in
	 * the order as they are received by the last channel's destination
	 * component.
	 */
	public static List<IExpression> getPathInitialMessages(Deque<IChannel> path) {
		final List<IExpression> result = new ArrayList<>();
		final Iterator<IChannel> it = path.descendingIterator();
		while (it.hasNext()) {
			result.addAll(it.next().getInitialMessages());
		}
		return result;
	}

	/**
	 * Returns the product of all source rates on the given <i>path</i>.
	 */
	public static BigInteger getPathSrcRate(Deque<IChannel> path) {
		assertTrue(!path.isEmpty(), "Expected path to be not empty");
		BigInteger result = BigInteger.ONE;
		for (final IChannel channel : path) {
			result = result.multiply(BigInteger.valueOf(channel.getSrcRate()));
		}
		return result;
	}

	/**
	 * Returns the product of all destination rates on the given <i>path</i>.
	 */
	public static BigInteger getPathDstRate(Deque<IChannel> path) {
		assertTrue(!path.isEmpty(), "Expected path to be not empty");
		BigInteger result = BigInteger.ONE;
		for (final IChannel channel : path) {
			result = result.multiply(BigInteger.valueOf(channel.getDstRate()));
		}
		return result;
	}

}
