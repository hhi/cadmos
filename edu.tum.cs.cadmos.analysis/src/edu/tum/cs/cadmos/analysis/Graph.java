package edu.tum.cs.cadmos.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import edu.tum.cs.cadmos.common.Assert;

public class Graph implements Iterable<Node> {

	private final Set<Node> vertices = new LinkedHashSet<>();
	private final Map<Node, Map<Node, Integer>> outgoing = new LinkedHashMap<>();
	private final Map<Node, Map<Node, Integer>> incoming = new LinkedHashMap<>();

	public void addVertex(Node v) {
		Assert.assertNotContainedIn(v, vertices, "v", "vertices");
		vertices.add(v);
	}

	public void addEdge(Node v1, Node v2) {
		Assert.assertContainedIn(v1, vertices, "v1", "vertices");
		Assert.assertContainedIn(v2, vertices, "v2", "vertices");
		addEdge(outgoing, v1, v2);
		addEdge(incoming, v2, v1);
	}

	private static void addEdge(Map<Node, Map<Node, Integer>> src, Node v1,
			Node v2) {
		Map<Node, Integer> dst = src.get(v1);
		if (dst == null) {
			dst = new LinkedHashMap<>();
			src.put(v1, dst);
		}
		Integer multiplicity = dst.get(v2);
		if (multiplicity == null) {
			multiplicity = 0;
		}
		dst.put(v2, multiplicity + 1);
	}

	@Override
	public Iterator<Node> iterator() {
		return vertices.iterator();
	}

	public Set<Node> getVertices() {
		return Collections.unmodifiableSet(vertices);
	}

	public int getVertexCount() {
		return vertices.size();
	}

	public List<Pair<Node, Node>> getEdges() {
		final List<Pair<Node, Node>> result = new ArrayList<>();
		for (final Entry<Node, Map<Node, Integer>> entry : outgoing.entrySet()) {
			for (final Entry<Node, Integer> dsts : entry.getValue().entrySet()) {
				for (int i = 0; i < dsts.getValue(); i++) {
					result.add(Tuples.create(entry.getKey(), dsts.getKey()));
				}
			}
		}
		return result;
	}

	public Map<Node, Integer> getOutgoingEdgesOf(Node v) {
		return getEdges(outgoing, v);
	}

	public Map<Node, Integer> getIncomingEdgesOf(Node v) {
		return getEdges(incoming, v);
	}

	private static Map<Node, Integer> getEdges(
			Map<Node, Map<Node, Integer>> src, Node v) {
		final Map<Node, Integer> dst = src.get(v);
		if (dst == null) {
			return Collections.EMPTY_MAP;
		}
		return Collections.unmodifiableMap(dst);
	}

}
