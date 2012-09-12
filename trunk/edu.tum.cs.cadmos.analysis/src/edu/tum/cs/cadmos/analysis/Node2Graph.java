package edu.tum.cs.cadmos.analysis;

import org.eclipse.emf.ecore.EObject;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.language.ModelUtils;
import edu.tum.cs.cadmos.language.cadmos.Channel;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.PortDirection;

public class Node2Graph {

	public static Graph translate(Node node) {
		final Graph graph = new Graph();
		translate(node, graph);
		return graph;
	}

	private static void translate(Node node, Graph graph) {
		final EObject obj = node.getSemanticObject();
		if ((obj instanceof Component && ModelUtils.getEmbeddings(
				((Component) obj)).isEmpty())
				|| (obj instanceof Embedding
						&& ((Embedding) obj).getComponent() != null && ModelUtils
						.getEmbeddings(((Embedding) obj).getComponent())
						.isEmpty())) {
			// Component or embedded component does not contain any further
			// embeddings.
			translateLeafComponent(node, graph);
		} else if (obj instanceof Component || obj instanceof Embedding) {
			translateCompositeComponent(node, graph);
		} else {
			Assert.fails(
					"Expected semantic object of 'node' to be Component or Embedding,  but was '%s'",
					obj);
		}
	}

	private static void translateLeafComponent(Node node, Graph graph) {
		graph.addVertex(node);
		for (final Node portNode : node.filterChildren(Port.class)) {
			graph.addVertex(portNode);
			final Port port = (Port) portNode.getSemanticObject();
			if (port.getDirection() == PortDirection.INBOUND) {
				graph.addEdge(portNode, node);
			} else {
				graph.addEdge(node, portNode);
			}
		}
	}

	private static void translateCompositeComponent(Node node, Graph graph) {
		for (final Node child : node.filterChildren(Embedding.class)) {
			translate(child, graph);
		}
		for (final Node child : node.filterChildren(Port.class)) {
			graph.addVertex(child);
		}
		for (final Node child : node.filterChildren(Channel.class)) {
			graph.addEdge(child.getFirstReference(), child.getLastReference());
		}
	}

}
