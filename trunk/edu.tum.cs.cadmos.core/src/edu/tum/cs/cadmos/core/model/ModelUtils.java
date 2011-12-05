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
import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static edu.tum.cs.cadmos.core.model.EPortDirection.INBOUND;
import static edu.tum.cs.cadmos.core.model.EPortDirection.OUTBOUND;
import static edu.tum.cs.cadmos.core.types.VoidType.VOID;
import static java.lang.String.format;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.tum.cs.cadmos.commons.core.IListMultiSet;
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
 * @ConQAT.Rating RED Hash: FE098789A84DA1255F89925314898CBF
 */
public class ModelUtils {

	/** Returns the union of all incoming channels of the given <i>ports</i>. */
	public static IListSet<IChannel> getIncoming(Iterable<IPort> ports) {
		final IListSet<IChannel> result = new ListSet<>();
		for (final IPort port : ports) {
			result.add(port.getIncoming());
		}
		return result;
	}

	/** Returns the union of all outgoing channels of the given <i>ports</i>. */
	public static IListSet<IChannel> getOutgoing(Iterable<IPort> ports) {
		final IListSet<IChannel> result = new ListSet<>();
		for (final IPort port : ports) {
			result.addAll(port.getOutgoing());
		}
		return result;
	}

	/**
	 * Returns a set of all atomic components recursively found in the given
	 * <i>component</i> in <a
	 * href="http://en.wikipedia.org/wiki/Breadth-first_search">BFS order</a> or
	 * the <i>component</i> itself if it is already an atomic component.
	 */
	public static IListSet<IAtomicComponent> getAtomicComponents(
			IComponent component) {
		final ListSet<IAtomicComponent> result = new ListSet<>();
		/* Initialize dynamic work queue. */
		final Deque<IComponent> queue = new LinkedList<>();
		queue.add(component);
		while (!queue.isEmpty()) {
			final IComponent current = queue.removeFirst();
			if (current instanceof IAtomicComponent) {
				result.add((IAtomicComponent) current);
			} else {
				assertInstanceOf(current, ICompositeComponent.class, "current");
				queue.addAll(((ICompositeComponent) current).getChildren()
						.toList());
			}
		}
		return result;
	}

	/**
	 * Returns the path of channels to the source of the given <i>port</i> that
	 * is transitively reachable within the given <i>systemBoundary</i>.
	 * <p>
	 * The path is returned as a {@link Deque} of {@link IChannel}s with the
	 * outgoing channel of the transitive source as first element and the
	 * incoming channel of the given <i>port</i> as last element:
	 * <code>[<i>source.getOutgoing()<sub>i</sub></i>, ..., <i>port.getIncoming()</i>]</code>.
	 * <p>
	 * Use <code>null</code> as a system boundary to search without constraints
	 * on the system boundary.
	 */
	public static Deque<IChannel> getSrcPath(IPort port,
			IComponent systemBoundary) {
		final Deque<IChannel> result = new LinkedList<>();
		IPort current = port;
		while (current.getIncoming() != null
				&& !(current.getComponent() == systemBoundary && current
						.getDirection() == INBOUND)) {
			result.addFirst(current.getIncoming());
			current = current.getIncomingOppositePort();
		}
		return result;
	}

	/**
	 * Returns a list of paths of channels to the destinations of the given
	 * <i>port</i> that are transitively reachable within the given
	 * <i>systemBoundary</i>.
	 * <p>
	 * Each of the paths is returned as a {@link Deque} of {@link IChannel}s
	 * with the an outgoing channel of the given <i>port</i> as first element
	 * and the channel incoming to a transitive destination as last element:
	 * <code>[<i>port.getOutgoing()<sub>i</sub></i>, ..., <i>destination.getIncoming()</i>]</code>.
	 * <p>
	 * The result is a list with length <i>n</i> of all such paths leading from
	 * the given source <i>port</i> to different destinations:
	 * <code>[[<i>port.getOutgoing()<sub>1</sub></i>, ..., <i>destination<sub>1</sub>.getIncoming()</i>], ..., [<i>port.getOutgoing()<sub>n</sub></i>, ..., <i>destination<sub>n</sub>.getIncoming()</i>]]</code>.
	 * <p>
	 * Use <code>null</code> as a system boundary to search without constraints
	 * on the system boundary.
	 */
	public static List<Deque<IChannel>> getDstPaths(IPort port,
			IComponent systemBoundary) {
		/* Phase 1: find destination ports. */
		final List<IPort> destinations = new ArrayList<>();
		/* Initialize dynamic work queue. */
		final Deque<IPort> queue = new LinkedList<>();
		queue.add(port);
		while (!queue.isEmpty()) {
			final IPort current = queue.removeFirst();
			final IListMultiSet<IPort> next = current
					.getOutgoingOppositePorts();
			final boolean boundaryReached = current.getComponent() == systemBoundary
					&& current.getDirection() == OUTBOUND;
			if (boundaryReached || next.isEmpty()) {
				destinations.add(current); // current is a destination
			} else {
				queue.addAll(next.toList()); // continue search
			}
		}
		/* Phase 2: get result by searching a source path for each destination. */
		final List<Deque<IChannel>> result = new LinkedList<>();
		for (final IPort dst : destinations) {
			final Deque<IChannel> path = getSrcPath(dst, port.getComponent());
			assertTrue(path.getFirst().getSrc() == port,
					"Expected 'path' to begin at 'port', but was '%s'", path);
			result.add(path);
		}
		return result;
	}

	public static ICompositeComponent transformAtomicComponentNetwork(
			ICompositeComponent systemBoundary) {
		/* Get a shallow clone of the system boundary and its ports. */
		final ICompositeComponent cloneBoundary = new CompositeComponent(
				systemBoundary.getId(), systemBoundary.getName(), null);
		systemBoundary.clonePorts(cloneBoundary);
		/* Find the atomic components within the system boundary. */
		final IListSet<IAtomicComponent> atomics = getAtomicComponents(systemBoundary);
		/* Clone the atomic components. */
		for (final IAtomicComponent atomic : atomics) {
			atomic.clone(cloneBoundary);
		}
		final IListSet<IComponent> clones = cloneBoundary.getChildren();
		/*
		 * Rewire the cloned system boundary with its cloned atomic components
		 * and the cloned atomic components with each other.
		 */
		for (final IAtomicComponent atomic : atomics) {
			final IComponent clone = clones.get(atomic);
			for (final IPort dstPort : atomic.getInbound()) {
				final Deque<IChannel> path = getSrcPath(dstPort, systemBoundary);
				assertTrue(!path.isEmpty(), "Expected 'path' to be not empty");
				final IPort srcPort = path.getFirst().getSrc();
				final IPort cloneSrcPort;
				if (srcPort.getComponent() == systemBoundary) {
					cloneSrcPort = cloneBoundary.getInbound().get(srcPort);
				} else {
					final IComponent cloneSrcComponent = clones.get(srcPort
							.getComponent());
					if (cloneSrcComponent == null) {
						continue; /* Path cannot be rewired. */
					}
					cloneSrcPort = cloneSrcComponent.getOutbound().get(srcPort);
				}
				final IPort cloneDstPort = clone.getInbound().get(dstPort);
				transformPathToChannel(path, cloneSrcPort, cloneDstPort);
			}
		}
		for (final IPort dstPort : systemBoundary.getOutbound()) {
			final Deque<IChannel> path = getSrcPath(dstPort, systemBoundary);
			assertTrue(!path.isEmpty(), "Expected 'path' to be not empty");
			final IPort srcPort = path.getFirst().getSrc();
			final IComponent cloneSrcComponent = clones.get(srcPort
					.getComponent());
			if (cloneSrcComponent == null) {
				continue; /* Path cannot be rewired. */
			}
			final IPort cloneSrcPort = cloneSrcComponent.getOutbound().get(
					srcPort);
			final IPort cloneDstPort = cloneBoundary.getOutbound().get(dstPort);
			transformPathToChannel(path, cloneSrcPort, cloneDstPort);
		}
		return cloneBoundary;
	}

	/**
	 * A pair of source- and destination-rates.
	 * 
	 * @author wolfgang.schwitzer
	 * 
	 * @see ModelUtils#getPathRates(Deque)
	 */
	private static class Rates {

		/** The source rate. */
		public final int srcRate;

		/** The destination rate. */
		public final int dstRate;

		/**
		 * Creates an immutable src-/dst-rates pair.
		 */
		public Rates(int srcRate, int dstRate) {
			this.srcRate = srcRate;
			this.dstRate = dstRate;
		}

	}

	/**
	 * Returns the total source and destination rate of a path. Both rates are
	 * normalized according to their greatest common divisor.
	 * 
	 * @see ModelUtils#getPathSrcRate(Deque)
	 * @see ModelUtils#getPathDstRate(Deque)
	 */
	private static Rates getPathRates(Deque<IChannel> path) {
		final BigInteger srcRate = getPathSrcRate(path);
		final BigInteger dstRate = getPathDstRate(path);
		final BigInteger gcd = srcRate.gcd(dstRate);
		return new Rates(srcRate.divide(gcd).intValue(), dstRate.divide(gcd)
				.intValue());
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
	 * 
	 * @see ModelUtils#getPathRates(Deque)
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
	 * 
	 * @see ModelUtils#getPathRates(Deque)
	 */
	public static BigInteger getPathDstRate(Deque<IChannel> path) {
		assertTrue(!path.isEmpty(), "Expected path to be not empty");
		BigInteger result = BigInteger.ONE;
		for (final IChannel channel : path) {
			result = result.multiply(BigInteger.valueOf(channel.getDstRate()));
		}
		return result;
	}

	/**
	 * Returns a unique id for the given non-empty <i>path</i>.
	 * 
	 * @throws AssertionError
	 *             if the given path is empty.
	 */
	public static String getPathId(Deque<IChannel> path) {
		assertTrue(!path.isEmpty(), "Expected path to be not empty");
		final IPort src = path.getFirst().getSrc();
		final IPort dst = path.getLast().getDst();
		return src.getComponent().getId() + "." + src.getId() + "::"
				+ dst.getComponent().getId() + "." + dst.getId();
	}

	/**
	 * Returns a newly created channel that has the combined properties of all
	 * channels on the given <i>path</i> (e.g. <i>id</i>, <i>initial
	 * messages</i>, <i>rates</i>) and that connects from to given <i>src-</i>
	 * to the given <i>dst-</i>port.
	 */
	public static IChannel transformPathToChannel(Deque<IChannel> path,
			IPort src, IPort dst) {
		final List<IExpression> initialMessages = getPathInitialMessages(path);
		final Rates rates = getPathRates(path);
		return new Channel(getPathId(path), path.getLast().getName(), src, dst,
				initialMessages, rates.srcRate, rates.dstRate);
	}

	/**
	 * Creates and returns a new channel and the respective source and
	 * destination ports.
	 * <p>
	 * This is a convenience method intended to be used mainly in unit testing
	 * context.
	 */
	public static IChannel createChannel(String id, IComponent src,
			IComponent dst, int delay) {
		final EPortDirection srcDirection;
		final EPortDirection dstDirection;
		if (src.getParent() == dst.getParent()) {
			srcDirection = OUTBOUND;
			dstDirection = INBOUND;
		} else if (src == dst.getParent()) {
			srcDirection = INBOUND;
			dstDirection = INBOUND;

		} else if (src.getParent() == dst) {
			srcDirection = OUTBOUND;
			dstDirection = OUTBOUND;
		} else {
			throw new IllegalArgumentException(format(
					"Cannot connect 'src' and 'dst' components: '%s' -> '%s'",
					src, dst));
		}
		IPort srcPort = src.getPorts(srcDirection).get(id);
		if (srcPort == null) {
			srcPort = new Port(id, null, VOID, src, srcDirection);
		}
		IPort dstPort = dst.getPorts(dstDirection).get(id);
		if (dstPort == null) {
			dstPort = new Port(id, null, VOID, dst, dstDirection);
		}
		return new Channel(src.getId() + "." + srcPort.getId() + "::"
				+ dst.getId() + "." + dstPort.getId(), srcPort, dstPort, delay);
	}

	public static Deque<IChannel> getSrcPaths(IPort port,
			IComponent systemBoundary, IListSet<IComponent> blackBoxes) {
		// TODO(WS,VP->VP): Implement
		return null;
	}

	public static List<Deque<IChannel>> getDstPaths(IPort port,
			IComponent systemBoundary, IListSet<IComponent> blackBoxes) {
		// TODO(WS,VP->VP): Implement
		return null;
	}

}
