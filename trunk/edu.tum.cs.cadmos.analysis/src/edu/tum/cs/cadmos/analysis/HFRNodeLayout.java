package edu.tum.cs.cadmos.analysis;

import static java.lang.Math.*;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.xtext.util.Pair;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.language.cadmos.Component;
import edu.tum.cs.cadmos.language.cadmos.Embedding;
import edu.tum.cs.cadmos.language.cadmos.Port;
import edu.tum.cs.cadmos.language.cadmos.PortDirection;

/**
 * Hierachical Fruchterman-Reingold layout for {@link Node}s.
 * 
 * @author wolfgang.schwitzer
 */
public class HFRNodeLayout {

	private static final float EPSILON = 1f / 1_000_000f;
	private static final int MAX_ITERATIONS = 2000;
	private static final float ATTRACTION_MULTIPLIER = 0.2f;
	private static final float REPULSION_MULTIPLIER = 0.2f;
	private static final float TEMPERATURE_MULTIPLIER = 0.1f;

	private final Graph graph;
	final Set<Node> inbound = new LinkedHashSet<>();
	final Set<Node> outbound = new LinkedHashSet<>();
	final Set<Node> innerInboundPorts = new LinkedHashSet<>();
	final Set<Node> innerOutboundPorts = new LinkedHashSet<>();
	final Set<Node> innerComponents = new LinkedHashSet<>();
	private final Map<Node, Vector2D> pos = new HashMap<>();
	private final Map<Node, Vector2D> disp = new HashMap<>();
	private final Vector2D size;
	private int iteration;
	private float temperature;
	private float attractionConstant;
	private float repulsionConstant;

	public HFRNodeLayout(Graph graph, float w, float h) {
		Assert.assertNotNull(graph, "graph");
		Assert.assertTrue(w > 0, "Expected 'w' > 0, but was '%s'", w);
		Assert.assertTrue(h > 0, "Expected 'h' > 0, but was '%s'", h);
		this.graph = graph;
		this.size = new Vector2D(w, h);
		init();
	}

	private void init() {
		initVectors();
		iteration = 0;
		temperature = size.norm() * TEMPERATURE_MULTIPLIER;
		final float forceConstant = (float) (size.norm() / max(1f,
				sqrt(graph.getVertexCount())));
		attractionConstant = forceConstant * ATTRACTION_MULTIPLIER;
		repulsionConstant = forceConstant * REPULSION_MULTIPLIER;
	}

	private void initVectors() {
		// Partition root-level inbound-port nodes, root-level outbound-port
		// nodes, and inner nodes.
		for (final Node node : NodeUtils.filterBySemanticObject(
				graph.getVertices(), Port.class)) {
			final Port p = (Port) node.getSemanticObject();
			Assert.assertNotNull(node.getParent(), "node.getParent()");
			if (node.getParent().getParent() == null) {
				if (p.getDirection() == PortDirection.INBOUND) {
					inbound.add(node);
				} else {
					outbound.add(node);
				}
			} else {
				if (p.getDirection() == PortDirection.INBOUND) {
					innerInboundPorts.add(node);
				} else {
					innerOutboundPorts.add(node);
				}
			}
		}
		innerComponents.addAll(NodeUtils.filterBySemanticObject(
				graph.getVertices(), Embedding.class, Component.class));
		// Initialize pos and disp vectors.
		initVectors(inbound, 0);
		initVectors(outbound, size.x);
		initVectors(innerInboundPorts, 0.25f * size.x);
		initVectors(innerOutboundPorts, 0.75f * size.x);
		initVectors(innerComponents, 0.5f * size.x);
	}

	private void initVectors(Set<Node> nodes, float x) {
		int y = 1;
		final float scale = size.y / (nodes.size() + 1);
		for (final Node node : nodes) {
			pos.put(node, new Vector2D(x, scale * (y++)));
			disp.put(node, new Vector2D(0, 0));
		}
	}

	public void step() {
		iteration++;
		for (final Node v : graph) {
			applyRepulsion(v);
		}
		for (final Pair<Node, Node> e : graph.getEdges()) {
			applyAttraction(e);
		}
		for (final Node v : graph) {
			applyPosition(v);
		}
		cool();
	}

	private void applyRepulsion(Node v1) {
		final Vector2D v1disp = disp.get(v1);
		v1disp.set(0, 0);
		final Vector2D p1 = pos.get(v1);
		for (final Node v2 : graph) {
			if (v1 == v2) {
				continue; // Do not apply self-repulsion.
			}
			final Vector2D p2 = pos.get(v2);
			final Vector2D delta = p1.delta(p2);
			final float length = max(EPSILON, delta.norm());
			final float force = repulsionConstant * repulsionConstant / length;
			v1disp.translate(delta.x / length * force, delta.y / length * force);
		}
	}

	private void applyAttraction(Pair<Node, Node> e) {
		final Node v1 = e.getFirst();
		final Node v2 = e.getSecond();
		final Vector2D p1 = pos.get(v1);
		final Vector2D p2 = pos.get(v2);
		final Vector2D delta = p1.delta(p2);
		final float length = max(EPSILON, delta.norm());
		float force = (length * length) / attractionConstant;
		if (v1.getParent() == v2 || v2.getParent() == v1) {
			force *= 4; // Components highly attract their ports.
		}
		final float dx = delta.x / length * force;
		final float dy = delta.y / length * force;
		disp.get(v1).translate(-dx, -dy);
		disp.get(v2).translate(dx, dy);
	}

	private void applyPosition(Node v) {
		final Vector2D vdisp = disp.get(v);
		final Vector2D p = pos.get(v);
		final float length = max(EPSILON, vdisp.norm());
		final float minlength = min(length, temperature);
		final float dx = vdisp.x / length * minlength;
		final float dy = vdisp.y / length * minlength;
		p.translate(dx, dy);
		p.x = max(0, min(size.x, p.x));
		p.y = max(0, min(size.y, p.y));
		if (inbound.contains(v)) {
			p.x = 0.9f * p.x;
		} else if (outbound.contains(v)) {
			p.x = p.x + 0.1f * (size.x - p.x);
		}
	}

	private void cool() {
		temperature *= (1f - iteration / (float) MAX_ITERATIONS);
	}

	public boolean done() {
		return iteration > MAX_ITERATIONS || temperature < 1f / size.norm();
	}

	public Vector2D get(Node v) {
		return pos.get(v);
	}

	public Graph getGraph() {
		return graph;
	}

	public float getWidth() {
		return size.x;
	}

	public float getHeight() {
		return size.y;
	}

	public boolean isRootLevelInboundPort(Node node) {
		return inbound.contains(node);
	}

	public boolean isRootLevelOutboundPort(Node node) {
		return outbound.contains(node);
	}

}
