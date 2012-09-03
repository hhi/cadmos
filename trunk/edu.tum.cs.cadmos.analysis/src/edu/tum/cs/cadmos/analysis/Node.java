package edu.tum.cs.cadmos.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.language.ModelUtils;

public class Node implements Iterable<Node> {

	private final Node parent;
	private final EObject semanticObject;
	private final int index;
	private final List<Node> children = new ArrayList<>();

	public Node(Node parent, EObject semanticObject, int index) {
		Assert.assertNotNull(semanticObject, "semanticObject");
		Assert.assertWithinRange(index, 0, Integer.MAX_VALUE, "index");
		this.parent = parent;
		this.semanticObject = semanticObject;
		this.index = index;
		if (parent != null) {
			Assert.assertNotContainedIn(this, parent.children, "this",
					"parent.children");
			parent.children.add(this);
		}
	}

	public Node getParent() {
		return parent;
	}

	public String getId() {
		final String localId = ModelUtils.getEObjectName(getSemanticObject())
				+ "[" + getIndex() + "]";
		if (parent == null) {
			return localId;
		}
		return parent.getId() + "." + localId;
	}

	public EObject getSemanticObject() {
		return semanticObject;
	}

	public int getIndex() {
		return index;
	}

	public Node findChild(EObject semanticObject, int index) {
		for (final Node child : children) {
			if (child.getSemanticObject() == semanticObject
					&& child.getIndex() == index) {
				return child;
			}
		}
		return null;
	}

	public Node getFirst() {
		Assert.assertTrue(!children.isEmpty(),
				"Cannot get first element: 'children' are emtpy");
		return children.get(0);
	}

	public Node getLast() {
		Assert.assertTrue(!children.isEmpty(),
				"Cannot get last element: 'children'  are emtpy");
		return children.get(children.size() - 1);
	}

	public boolean isFirst() {
		return parent != null && parent.children.get(0) == this;
	}

	public boolean isLast() {
		return parent != null
				&& parent.children.get(parent.children.size() - 1) == this;
	}

	@Override
	public Iterator<Node> iterator() {
		return children.iterator();
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		toString(this, 0, s, new ArrayList<Boolean>());
		return s.toString();
	}

	private void toString(Node node, int indent, StringBuilder s,
			ArrayList<Boolean> activeLevels) {
		if (activeLevels.size() < indent + 1) {
			activeLevels.add(false);
		}
		for (int i = 0; i < indent - 1; i++) {
			if (activeLevels.get(i)) {
				s.append("│");
			} else {
				s.append(" ");
			}
			s.append(" ");
		}
		if (indent > 0) {
			if (node.isLast()) {
				s.append("└─");
			} else {
				s.append("├─");
			}
		}
		s.append("o ");
		final String text = node.getSemanticObject().toString()
				.replace("edu.tum.cs.cadmos.language.cadmos.impl.", "")
				.replace("Impl@", "@").replaceAll("@[a-f0-9]+", "");
		s.append(text);
		s.append(" [");
		s.append(node.getIndex());
		s.append("]");
		activeLevels.set(indent, true);
		for (final Node child : node) {
			s.append("\n");
			if (child.isLast()) {
				activeLevels.set(indent, false);
			}
			toString(child, indent + 1, s, activeLevels);
		}
	}
}
