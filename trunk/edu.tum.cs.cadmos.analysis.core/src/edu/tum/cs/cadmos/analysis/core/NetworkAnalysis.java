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

package edu.tum.cs.cadmos.analysis.core;

import static edu.tum.cs.cadmos.commons.core.Assert.assertTrue;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

import edu.tum.cs.cadmos.analysis.core.utils.NetworkUtils;
import edu.tum.cs.cadmos.commons.core.Assert;
import edu.tum.cs.cadmos.core.model.IAtomicComponent;
import edu.tum.cs.cadmos.core.model.IChannel;
import edu.tum.cs.cadmos.core.model.IComponent;

/**
 * A set of iterative analysis techniques for networks of components.
 * 
 * @author nvpopa@gmail.com
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash: 9784A0106717D734AA5BB7657F88D3A9
 */
public class NetworkAnalysis {
	/**
	 * The maximum number of cycles returned by the long running operation
	 * {@link #analyzeCycles(Graph)}. If there actually existed more cycles,
	 * only the first MAX_CYCLES cycles are returned.
	 */
	public static final int MAX_CYCLES = 100;

	/**
	 * Returns all components in the given network that are member of a strongly
	 * connected component (SCC) and therefore belong to a system state.
	 */
	public static Collection<IAtomicComponent> analyzeStatefulVertices(
			INetwork network) {
		final Set<IAtomicComponent> result = new HashSet<>();
		for (final Collection<IAtomicComponent> scc : analyzeStronglyConnectedComponents(network)) {
			result.addAll(scc);
		}
		return result;
	}

	/**
	 * Returns all cycles in the given <i>network</i>. Each cycle
	 * <i>C<sub>i</sub></i> is returned as a path described by a list of
	 * components. <br>
	 * Note, that in general <i>C<sub>i</sub></i> and <i>C<sub>k</sub></i>
	 * (<i>i</i> &#8800; <i>k</i>) need not to be disjoint component sets.
	 * <p>
	 * Example: G={V, E} with V={X, Y, Z} and E={XY, YZ, ZX} then
	 * analyzeCycles(G) == {C}, where C = [X, Y, Z] or [Y, Z, X] or [Z, X, Y],
	 * depending on the internal ordering of components and channels in G.
	 */
	public static Collection<List<IAtomicComponent>> analyzeCycles(
			INetwork network) {
		final Collection<List<IAtomicComponent>> result = new HashSet<>();
		for (final Collection<IAtomicComponent> scc : analyzeStronglyConnectedComponents(network)) {
			final INetwork sccGraph = NetworkTransformation
					.transformSubnetwork(scc);
			final Set<Set<IAtomicComponent>> equivalentCycles = new HashSet<>();
			final List<IAtomicComponent> stack = new ArrayList<>();
			final Set<IAtomicComponent> stackSet = new HashSet<>();
			internal_analyzeCycles(sccGraph,
					NetworkUtils.getAnyComponent(sccGraph), result,
					equivalentCycles, stack, stackSet);
		}
		return result;
	}

	/**
	 * Adds all cycles reachable from component <i>c</i> to the <i>result</i>
	 * collection. Used internally by {@link #analyzeCycles(Graph)}.
	 * 
	 * @param c
	 *            the component from which cycle detection is started.
	 * @param result
	 *            the collection to which all cycles are added that are
	 *            reachable from c. Each cycle is returned as a list of
	 *            components that contains each component at most once.
	 * @param equivalentCycles
	 *            a set of all equivalent cycles that have been detected, so
	 *            far. If a cycle is detected that is present in this set
	 *            already, that cycle is not added to the result.
	 * @param stack
	 *            a list of all components that have been visited in the current
	 *            path of DFS-recursion, so far. In order to execute this method
	 *            efficiently, this list must implement the {@link RandomAccess}
	 *            interface (e.g. {@link ArrayList}).
	 * @param stackSet
	 *            a set of all components that have been visited in the current
	 *            path of DFS-recursion, so far. In order to execute this method
	 *            efficiently, the stack-set is used to determine whether a
	 *            component is on the stack in constant time (<i>O</i>(1)).
	 */
	private static void internal_analyzeCycles(INetwork network,
			IAtomicComponent c, Collection<List<IAtomicComponent>> result,
			Set<Set<IAtomicComponent>> equivalentCycles,
			List<IAtomicComponent> stack, Set<IAtomicComponent> stackSet) {
		if (result.size() >= MAX_CYCLES) {
			return;
		}
		if (stackSet.contains(c)) {
			// Found a cycle on top of the stack.
			final Set<IAtomicComponent> candidate = new HashSet<>();
			int i = stack.size();
			do {
				candidate.add(stack.get(--i));
			} while (stack.get(i) != c);
			if (!equivalentCycles.contains(candidate)) {
				equivalentCycles.add(candidate);
				final List<IAtomicComponent> cycle = new ArrayList<>(
						stack.subList(i, stack.size()));
				result.add(cycle);
			}
			// Finishes recursion in this path.
			return;
		}
		// Recurse into all successors of v.
		stack.add(c);
		stackSet.add(c);
		for (final IAtomicComponent s : network.getSuccessors(c)) {
			internal_analyzeCycles(network, s, result, equivalentCycles, stack,
					stackSet);
		}
		stack.remove(stack.size() - 1);
		stackSet.remove(c);
	}

	/**
	 * Returns the maximum cycle mean of the given {@link Graph}.
	 */
	public static double analyzeMCM(INetwork network) {
		final Collection<List<IAtomicComponent>> cycles = analyzeCycles(network);

		double mcm = 0;
		for (final List<IAtomicComponent> cycle : cycles) {
			// FIXME: Workaround until IAtomicComponent's have a execution time;
			final double execTime = cycle.size();

			Collection<IChannel> channelSet = network.getChannels(
					cycle.get(cycle.size() - 1), cycle.get(0));
			int min = Integer.MAX_VALUE;
			for (final IChannel channel : channelSet) {
				if (min > channel.getDelay()) {
					min = channel.getDelay();
				}
			}
			int delayCount = min;
			for (int i = 0; i < cycle.size() - 1; i++) {
				channelSet = network
						.getChannels(cycle.get(i), cycle.get(i + 1));
				min = Integer.MAX_VALUE;
				for (final IChannel channel : channelSet) {
					if (min > channel.getDelay()) {
						min = channel.getDelay();
					}
				}
				delayCount += min;
			}
			final double temp = execTime / delayCount;
			if (mcm < temp) {
				mcm = temp;
			}
		}

		return mcm;
	}

	/**
	 * Analyzes the precedence order of all components in the given
	 * <i>network</i>. This method returns a {@link HashMap} containing for each
	 * component its precedence order.
	 * <p>
	 * Note that this method only works for directed acyclic networks.
	 * <p>
	 * For example, the method
	 * {@link NetworkTransformation#transformAcyclicIntraIterationPrecedenceGraph(Graph)}
	 * can be used to make a cyclic network acyclic.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given network contains cycles.
	 */
	public static Map<IAtomicComponent, Integer> analyzePrecedenceOrder(
			INetwork network) {
		final HashMap<IAtomicComponent, Integer> precedenceOrder = new HashMap<>();
		final List<IAtomicComponent> sortedList = analyzeTopologicalSort(network);

		for (final IAtomicComponent c : sortedList) {
			int order = -1;
			for (final IAtomicComponent pred : network.getPredecessors(c)) {
				final int predOrder = precedenceOrder.get(pred);
				// If the components are correctly sorted, this assertion will
				// be
				// true.
				Assert.assertNotNull(predOrder, "predOrder" + "");
				order = max(order, predOrder);
			}
			precedenceOrder.put(c, order + 1);
		}

		return precedenceOrder;
	}

	/**
	 * Analyzes the guaranteed causal delays of all channels in the given
	 * <i>network</i>. It returns a {@link Map} containing for each channel its
	 * minimum output delay.
	 */
	public static Map<IChannel, Integer> analyzeGuaranteedDelays(
			INetwork network) {
		final HashMap<IChannel, Integer> channelOutputDelays = new HashMap<>();
		final HashMap<IAtomicComponent, Integer> componentOutputDelays = new HashMap<>();

		for (final IChannel ch : network.getAllChannels()) {
			final IAtomicComponent env = (IAtomicComponent) ch.getSrc();
			if (env == null) {
				getOutgoingDelays(network, (IAtomicComponent) ch.getDst(),
						componentOutputDelays, channelOutputDelays, 0);
			}
		}

		return channelOutputDelays;
	}

	/**
	 * Performs a dfs search updating the guaranteed delays on each channel and
	 * component.
	 */
	private static void getOutgoingDelays(INetwork network,
			IAtomicComponent component,
			HashMap<IAtomicComponent, Integer> componentOutputDelays,
			HashMap<IChannel, Integer> channelOutputDelays, int delay) {
		if (componentOutputDelays.containsKey(component)
				&& componentOutputDelays.get(component) <= delay) {
			return;
		}

		componentOutputDelays.put(component, delay);
		for (final IChannel channel : component.getOutgoing()) {
			channelOutputDelays.put(channel, delay + channel.getDelay());
			final IComponent dst = channel.getDst();
			Assert.assertInstanceOf(dst, IAtomicComponent.class, "dst");
			getOutgoingDelays(network, (IAtomicComponent) dst,
					componentOutputDelays, channelOutputDelays,
					delay + channel.getDelay());
		}
	}

	/**
	 * Returns the list of topological sorted components in the given
	 * <i>network</i>.
	 * <p>
	 * Note that this method only works for directed acyclic networks.
	 * <p>
	 * For example, the method
	 * {@link NetworkTransformation#transformAcyclicIntraIterationPrecedenceGraph(Graph)}
	 * can be used to make a cyclic network acyclic.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given network contains cycles.
	 */
	public static List<IAtomicComponent> analyzeTopologicalSort(INetwork network) {
		final ArrayList<IAtomicComponent> queue = new ArrayList<>(network
				.getAllComponents().size());
		final HashMap<IAtomicComponent, Integer> inDegree = new HashMap<>();

		for (final IAtomicComponent c : network.getAllComponents()) {
			inDegree.put(c, network.getPredecessors(c).size());
			if (network.getPredecessors(c).size() == 0) {
				queue.add(c);
			}
		}

		// TODO(WS): throw IllegalArgumentException if queue is emtpy at this
		// point, though network is not empty (network must have a cycle then)

		for (int i = 0; i < network.getAllComponents().size(); i++) {
			final IAtomicComponent c = queue.get(i);
			for (final IAtomicComponent n : network.getSuccessors(c)) {
				// TODO(WS): throw IllegalArgumentException if cycle is detected
				// (I don't known if this is easy to detect here, maybe a
				// "visited" set is needed...).
				final int deg = inDegree.get(n) - 1;
				inDegree.put(n, deg);
				if (deg == 0) {
					queue.add(n);
				}
			}
		}

		return queue;
	}

	/**
	 * Returns a collection of all strongly connected components
	 * <i>SCC<sub>i</sub></i> in the given <i>network</i>. Each <i>'inner'</i>
	 * collection represents one SCC.
	 * <p>
	 * This implementation is based on <a href=
	 * "http://en.wikipedia.org/wiki/Tarjan's_strongly_connected_components_algorithm"
	 * ><i>Tarjan's</i> strongly connected component algorithm</a> that has
	 * runtime complexity <i>O(|V| + |E|)</i>.
	 */
	public static Collection<Collection<IAtomicComponent>> analyzeStronglyConnectedComponents(
			INetwork network) {
		final Collection<Collection<IAtomicComponent>> sccs = new LinkedHashSet<>();
		final List<IAtomicComponent> stack = new ArrayList<>();
		final Set<IAtomicComponent> stackSet = new HashSet<>();
		final Map<IAtomicComponent, Integer> indexMap = new HashMap<>();
		final Map<IAtomicComponent, Integer> lowlinkMap = new HashMap<>();
		int index = 0;
		for (final IAtomicComponent c : network.getAllComponents()) {
			if (!indexMap.containsKey(c)) {
				index = analyzeStronglyConnectedComponent(network, c, index,
						sccs, stack, stackSet, indexMap, lowlinkMap);
			}
		}
		return sccs;
	}

	/**
	 * Adds the strongly connected component to which the given component
	 * <i>comp</i> belongs to the given <i>sccs</i> collection.
	 */
	private static int analyzeStronglyConnectedComponent(INetwork network,
			IAtomicComponent comp, int index,
			Collection<Collection<IAtomicComponent>> sccs,
			List<IAtomicComponent> stack, Set<IAtomicComponent> stackSet,
			Map<IAtomicComponent, Integer> indexMap,
			Map<IAtomicComponent, Integer> lowlinkMap) {
		int localIndex = index;
		stackSet.add(comp);
		indexMap.put(comp, localIndex);
		lowlinkMap.put(comp, localIndex);
		localIndex++;
		stack.add(comp);
		final Collection<IAtomicComponent> successors = network
				.getSuccessors(comp);
		for (final IAtomicComponent s : successors) {
			if (indexMap.get(s) == null) {
				localIndex = analyzeStronglyConnectedComponent(network, s,
						localIndex, sccs, stack, stackSet, indexMap, lowlinkMap);
				lowlinkMap.put(comp,
						min(lowlinkMap.get(comp), lowlinkMap.get(s)));
			} else if (stackSet.contains(s)) {
				lowlinkMap.put(comp,
						min(lowlinkMap.get(comp), lowlinkMap.get(s)));
			}
		}
		if (lowlinkMap.get(comp).equals(indexMap.get(comp))) {
			final Collection<IAtomicComponent> scc = new LinkedHashSet<>();
			IAtomicComponent member;
			do {
				scc.add(member = stack.remove(stack.size() - 1));
				stackSet.remove(member);
			} while (member != comp);
			assertTrue(!scc.isEmpty(), "Expected scc to be non-empty");
			if (scc.size() > 1 || successors.contains(comp)) {
				sccs.add(scc);
			}
		}
		return localIndex;
	}

	/**
	 * Returns the causality of the given component <i>comp</i>. The causality
	 * &#x3b3; of a function <i>F</i> is determined by the minimum outgoing
	 * channel delay as follows
	 * <p>
	 * <center>&#x3b3;(<i>F</i>) = <i>min</i>{&#x3b4;(<i>e</i>) : <i>e</i>
	 * &#x2208; <i>outgoing</i>(<i>F</i>)}</center>.
	 * <p>
	 * Causality is further classified as <i>weak</i> and <i>strong</i>
	 * causality:
	 * <ul>
	 * <li>A weakly causal function has a minimum outgoing delay equal to zero.
	 * <li>A strongly causal function has a minimum outgoing delay greater than
	 * zero.
	 * </ul>
	 * Note that outputs to the environment are strongly causal by definition.
	 */
	public static int analyzeCausality(IAtomicComponent comp) {
		boolean initialized = false;
		int result = 0;
		for (final IChannel ch : comp.getOutgoing()) {
			if (initialized) {
				result = min(result, ch.getDelay());
			} else {
				result = ch.getDelay();
				initialized = true;
			}
			if (result == 0) {
				break;
			}
		}
		return result;
	}

	// /** Returns all weak components of the given network. */
	// public static Set<Set<IAtomicComponent>>
	// analyzeWeaklyConnectedComponents(
	// INetwork network) {
	// final WeakComponentClusterer<IAtomicComponent, IChannel> clusterer = new
	// WeakComponentClusterer<IAtomicComponent, IChannel>();
	// return clusterer.transform(network);
	// }

	/**
	 * Returns an histogram of the given network's channels summarized by delay.
	 * Each key in the returned map represents a delay value and the
	 * corresponding value is a collection of all channels that have equal
	 * delay.
	 * <p>
	 * If a delay value is not present in the network, the map does not contain
	 * a key for it and consequently returns <code>null</code>.
	 */
	public static Map<Integer, Collection<IChannel>> analyzeChannelDelayHistogram(
			INetwork network) {
		final Map<Integer, Collection<IChannel>> result = new HashMap<>();
		for (final IChannel ch : network.getAllChannels()) {
			Collection<IChannel> channels = result.get(ch.getDelay());
			if (channels == null) {
				channels = new ArrayList<>();
				result.put(ch.getDelay(), channels);
			}
			channels.add(ch);
		}
		return result;
	}

	// TODO(VP): adapt method analyzeDelayProfile() to the new architecture.
	// /**
	// * Creates and returns the delay profile as a {@link Collection} of
	// * {@link Pair} for the given component to all the output components. The
	// second
	// * element of the {@link Pair} is the guaranteed delay to the output
	// component.
	// * <p>
	// * <i> Note: The given component has to be a environment input.
	// * Otherwise the method will return null.</i>
	// */
	// public static Collection<Pair<IAtomicComponent, Integer>>
	// analyzeDelayProfile(
	// IAtomicComponent component, INetwork network) {
	// final Collection<Pair<IAtomicComponent, Integer>> profile = new
	// HashSet<>();
	// final HashSet<IAtomicComponent> components = new HashSet<>();
	// final HashMap<IAtomicComponent, Integer> componentOutputDelays = new
	// HashMap<>();
	//
	// for (final IAtomicComponent comp : network.getAllComponents()) {
	// components.add(comp);
	// }
	//
	// final INetwork subGraph = NetworkTransformation
	// .transformSubnetwork(components);
	// final HashMap<IChannel, Integer> minDelays = (HashMap<IChannel, Integer>)
	// NetworkAnalysis
	// .analyzeGuaranteedDelays(subGraph);
	//
	// for (final Entry<IChannel, Integer> entry : minDelays.entrySet()) {
	// final IAtomicComponent c = (IAtomicComponent) entry.getKey()
	// .getDst();
	// if (c == null && !componentOutputDelays.containsKey(c)) {
	// int minDelay = Integer.MAX_VALUE >> 1;
	// for (final IChannel ch : c.getIncoming()) {
	// minDelay = min(minDelay, minDelays.get(ch));
	// }
	// componentOutputDelays.put(c, minDelay);
	// profile.add(new Pair<>(c, minDelay));
	// }
	// }
	//
	// return profile;
	// }

	/**
	 * Returns all the transitive successors from component in the given
	 * {@link Graph}.
	 */
	public static Set<IAtomicComponent> analyzeTransitiveSuccessors(
			INetwork network, IAtomicComponent component) {
		return internal_analyzeTransitiveNeighbors(network, component, true);
	}

	/**
	 * Returns all the transitive predecessors from component in the given
	 * {@link Graph}.
	 */
	public static Set<IAtomicComponent> analyzeTransitivePredecessors(
			INetwork network, IAtomicComponent component) {
		return internal_analyzeTransitiveNeighbors(network, component, false);
	}

	/**
	 * Returns all the transitive successors or predecessors from
	 * <i>component</i> in the given {@link Graph}. If <i>findSuccessors</i> is
	 * <code>true</code> then the transitive successors are returned, otherwise
	 * the transitive predecessors are returned.
	 */
	public static Set<IAtomicComponent> internal_analyzeTransitiveNeighbors(
			INetwork network, IAtomicComponent component, boolean findSuccessors) {
		final Set<IAtomicComponent> neighbors = new HashSet<>();
		final ArrayList<IAtomicComponent> queue = new ArrayList<>();
		queue.add(component);

		for (int index = 0; index < queue.size(); index++) {
			final IAtomicComponent current = queue.get(index);
			final Collection<IAtomicComponent> list = findSuccessors ? network
					.getSuccessors(current) : network.getPredecessors(current);

			Assert.assertNotNull(list, "list");

			for (final IAtomicComponent c : list) {
				/* Be robust against cycles. */
				if (!neighbors.contains(c)) {
					queue.add(c);
					neighbors.add(c);
				}
			}
		}

		return neighbors;
	}

}
