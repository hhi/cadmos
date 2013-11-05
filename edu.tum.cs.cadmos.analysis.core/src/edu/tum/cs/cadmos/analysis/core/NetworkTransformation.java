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

import java.util.Collection;

import edu.tum.cs.cadmos.core.model.AtomicComponent;
import edu.tum.cs.cadmos.core.model.IAtomicComponent;

/**
 * A NetworkTransformation.
 * 
 * @author
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class NetworkTransformation {
	/**
	 * Returns the subnetwork that is spanned by the given collection of
	 * <i>components</i>.
	 */
	public static INetwork transformSubnetwork(
			Collection<IAtomicComponent> components) {
		final INetwork subnetwork = new Network();
		for (final IAtomicComponent comp : components) {
			final IAtomicComponent newComponent = new AtomicComponent(
					comp.getId(), comp.getName(), comp.getParent(),
					comp.getMachine());
			subnetwork.addComponent(newComponent);
		}
		// for (final IAtomicComponent comp : components) {
		// for (final IChannel ch : comp.getOutbound()) {
		// if (subnetwork.getAllComponents().contains(
		// (IAtomicComponent) ch.getDst())) {
		// new Channel(ch.getId(), ch.getName(), ch.getType(),
		// ch.getSrc(), ch.getDst(), ch.getInitialMessages(),
		// ch.getSrcRate(), ch.getDstRate());
		// }
		// }
		// for (final IChannel ch : comp.getInbound()) {
		// if (subnetwork.getAllComponents().contains(
		// (IAtomicComponent) ch.getSrc())) {
		// new Channel(ch.getId(), ch.getName(), ch.getType(),
		// ch.getSrc(), ch.getDst(), ch.getInitialMessages(),
		// ch.getSrcRate(), ch.getDstRate());
		// }
		// }
		// }
		return subnetwork;
	}

	// /**
	// * Returns the acyclic intra-iteration precedence graph of the given
	// * <i>graph</i>, that is a set of directed acyclic graphs (DAG's) that
	// cover
	// * all original vertices, but only contain original edges with zero delay.
	// * <p>
	// * Note, that the returned graph may comprise significantly more weakly
	// * connected components than the original graph does.
	// */
	// public static INetwork transformAcyclicIntraIterationPrecedenceGraph(
	// INetwork graph) {
	// final INetwork acyclicGraph = new Network();
	// for (final IAtomicComponent v : graph.getVertices()) {
	// acyclicGraph.addVertex(v);
	// }
	// for (final IEdge e : graph.getEdges()) {
	// if (e.getDelay() == 0) {
	// acyclicGraph.addEdge(e, graph.getSource(e), graph.getDest(e));
	// }
	// }
	// return acyclicGraph;
	// }
	//
	// /**
	// * Returns the given iterative data-flow <i>graph</i> unfolded by the
	// given
	// * <i>factor</i>.
	// * <p>
	// * Algorithm according to:<br>
	// * <i> Keshab K. Parhi and David G. Messerschmitt. 1991. Static
	// Rate-Optimal
	// * Scheduling of Iterative Data-Flow Programs Via Optimum Unfolding. IEEE
	// * Trans. Comput. 40, 1991, 178-195.</i>
	// */
	// public static INetwork transformUnfolding(INetwork graph, int factor) {
	// assertTrue(factor >= 1, "Expected factor to be >= 1, but was %s",
	// factor);
	// final INetwork unfoldedGraph = new Network();
	// /* Step 1. */
	// final Map<IAtomicComponent, List<IAtomicComponent>> vertexInstances = new
	// HashMap<IAtomicComponent, List<IAtomicComponent>>();
	// for (final IAtomicComponent vertex : graph.getVertices()) {
	// final ArrayList<IAtomicComponent> instances = new
	// ArrayList<IAtomicComponent>(
	// factor);
	// vertexInstances.put(vertex, instances);
	// for (int i = 1; i <= factor; i++) {
	// final IAtomicComponent vertexInstance = new VertexInstance(
	// vertex, i);
	// instances.add(vertexInstance);
	// unfoldedGraph.addVertex(vertexInstance);
	// }
	// }
	// for (final IEdge edge : graph.getEdges()) {
	// final IAtomicComponent src = graph.getSource(edge);
	// final IAtomicComponent dst = graph.getDest(edge);
	// final List<IAtomicComponent> srcs = vertexInstances.get(src);
	// final List<IAtomicComponent> dsts = vertexInstances.get(dst);
	// final int delay = edge.getDelay();
	// int instance = 1;
	// if (delay == 0) {
	// /* Step 2. */
	// for (int k = 0; k < factor; k++) {
	// final IEdge edgeInstance = new EdgeInstance(edge,
	// instance++, edge.getDelay());
	// unfoldedGraph.addEdge(edgeInstance, srcs.get(k),
	// dsts.get(k));
	// }
	// } else if (delay < factor) {
	// /* Step 3a. */
	// for (int q = delay + 1; q <= factor; q++) {
	// // Insert edge with zero registers.
	// final IEdge edgeInstance = new EdgeInstance(edge,
	// instance++, 0);
	// unfoldedGraph.addEdge(edgeInstance,
	// srcs.get(q - delay - 1), dsts.get(q - 1));
	// }
	// for (int q = 1; q <= delay; q++) {
	// // Insert edge with single register.
	// final IEdge edgeInstance = new EdgeInstance(edge,
	// instance++, 1);
	// unfoldedGraph.addEdge(edgeInstance,
	// srcs.get(factor - delay + q - 1), dsts.get(q - 1));
	// }
	// } else if (delay >= factor) {
	// /* Step 3b. */
	// for (int q = 1; q <= factor; q++) {
	// // Insert edge with 'distance' registers.
	// final int distance = (int) ceil((delay - q + 1.0) / factor);
	// final IEdge edgeInstance = new EdgeInstance(edge,
	// instance++, distance);
	// unfoldedGraph.addEdge(edgeInstance,
	// srcs.get(distance * factor - delay + q - 1),
	// dsts.get(q - 1));
	// }
	//
	// } else {
	// assertTrue(false, "Expected delay to be >= 0, but was %s",
	// delay);
	// }
	// }
	// return unfoldedGraph;
	// }
	//
	// public static INetwork transformHomogeneousCausality(INetwork graph) {
	// final INetwork causalGraph = new Network();
	// for (final IAtomicComponent vertex : graph.getVertices()) {
	// causalGraph.addVertex(vertex);
	// final int causality = analyzeCausality(graph, vertex);
	// int instance = 1;
	// int count = 1;
	// for (final IEdge edge : graph.getOutEdges(vertex)) {
	// if (edge.getDelay() > causality) {
	// final IEdge e1 = new EdgeInstance(edge, instance++,
	// causality);
	// final IEdge e2 = new EdgeInstance(edge, instance++,
	// edge.getDelay() - causality);
	// final Vertex id = new Vertex();
	// id.setId(new Object());
	// id.setName(vertex.getName() + "_ID_" + count);
	// id.setExpression(new Expression(EOperator.ASSIGN,
	// new Expression(EOperator.REF, edge.getId()),
	// new Expression(EOperator.REF, edge.getId())));
	// causalGraph.addVertex(id);
	// causalGraph.addEdge(e1, vertex, id);
	// causalGraph.addEdge(e2, id, graph.getDest(edge));
	// count++;
	// } else {
	// causalGraph.addEdge(edge, vertex, graph.getDest(edge));
	// }
	// }
	// }
	// return causalGraph;
	// }
	//
	// /**
	// * This function changes the graph according to the
	// retiming-transformation
	// * algorithm. If the factor is greater than the smallest delay of all
	// * incoming edges then, this method will throw an
	// * {@link IllegalArgumentException}.
	// */
	// public static INetwork transformRetimingVertex(INetwork graph,
	// IAtomicComponent vertex, int factor)
	// throws IllegalArgumentException {
	// if (vertex.getType() == EVertexType.INPUT
	// || vertex.getType() == EVertexType.OUTPUT) {
	// throw new IllegalArgumentException();
	// }
	// for (final IEdge e : graph.getInEdges(vertex)) {
	// if (e.getDelay() < factor) {
	// throw new IllegalArgumentException();
	// }
	// }
	// final HashSet<IEdge> inEdges = new HashSet<IEdge>();
	// inEdges.addAll(graph.getInEdges(vertex));
	// final HashSet<IEdge> outEdges = new HashSet<IEdge>();
	// outEdges.addAll(graph.getOutEdges(vertex));
	//
	// final INetwork retimedGraph = new Network();
	//
	// for (final IAtomicComponent v : graph.getVertices()) {
	// retimedGraph.addVertex(v);
	// }
	// for (final IEdge e : graph.getEdges()) {
	// if (inEdges.contains(e)) {
	// final IEdge edge = new Edge().setName(e.getName())
	// .setId(e.getId()).setDelay(e.getDelay() - factor);
	// retimedGraph
	// .addEdge(edge, graph.getSource(e), graph.getDest(e));
	// } else if (outEdges.contains(e)) {
	// final IEdge edge = new Edge().setName(e.getName())
	// .setId(e.getId()).setDelay(e.getDelay() + factor);
	// retimedGraph
	// .addEdge(edge, graph.getSource(e), graph.getDest(e));
	// } else {
	// retimedGraph.addEdge(e, graph.getSource(e), graph.getDest(e));
	// }
	// }
	//
	// return retimedGraph;
	// }
}
