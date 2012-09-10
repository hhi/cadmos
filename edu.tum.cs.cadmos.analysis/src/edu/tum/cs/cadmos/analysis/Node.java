package edu.tum.cs.cadmos.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import edu.tum.cs.cadmos.common.Assert;
import edu.tum.cs.cadmos.common.ListUtils;
import edu.tum.cs.cadmos.language.ModelUtils;

public class Node implements Iterable<Node> {

	private final Node parent;
	private final EObject semanticObject;
	private final int index;

	/**
	 * List of child nodes, lazily created by first child node in
	 * {@link #Node(Node, EObject, int)}.
	 */
	private List<Node> children;

	/** List of referenced nodes, lazily created in {@link #addReference(Node)}. */
	private List<Node> references;

	/**
	 * List of nodes that reference this node, lazily created by first
	 * referencing node in {@link #addReference(Node)}.
	 */
	private List<Node> referencedBy;

	/** Caches this node's id, initialized in {@link #getId()}. */
	private String id;

	public Node(Node parent, EObject semanticObject, int index) {
		Assert.assertNotNull(semanticObject, "semanticObject");
		Assert.assertWithinRange(index, 0, Integer.MAX_VALUE, "index");
		this.parent = parent;
		this.semanticObject = semanticObject;
		this.index = index;
		if (parent != null) {
			if (parent.children == null) {
				parent.children = new ArrayList<>();
			}
			Assert.assertNotContainedIn(this, parent.children, "this",
					"parent.children");
			parent.children.add(this);
		}
	}

	public Node getParent() {
		return parent;
	}

	public String getId() {
		if (id == null) {
			if (parent == null) {
				id = getLocalId();
			} else {
				id = parent.getId() + "." + getLocalId();
			}
		}
		return id;
	}

	private String getLocalId() {
		return ModelUtils.getEObjectName(getSemanticObject()) + "["
				+ getIndex() + "]";
	}

	public EObject getSemanticObject() {
		return semanticObject;
	}

	public int getIndex() {
		return index;
	}

	public Node findChild(EObject semanticObject, int index) {
		for (final Node child : ListUtils.nullIsEmpty(children)) {
			if (child.getSemanticObject() == semanticObject
					&& child.getIndex() == index) {
				return child;
			}
		}
		return null;
	}

	public Node getFirst() {
		Assert.assertTrue(children != null,
				"Cannot get first element: 'children' are emtpy");
		return children.get(0);
	}

	public Node getLast() {
		Assert.assertTrue(children != null,
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
		return ListUtils.nullIsEmpty(children).iterator();
	}

	public void addReference(Node ref) {
		Assert.assertNotNull(ref, "ref");
		if (references == null) {
			references = new ArrayList<>();
		}
		references.add(ref);
		if (ref.referencedBy == null) {
			ref.referencedBy = new ArrayList<>();
		}
		ref.referencedBy.add(this);
	}

	public List<Node> getReferences() {
		return Collections.unmodifiableList(ListUtils.nullIsEmpty(references));
	}

	public Node getFirstReference() {
		Assert.assertTrue(references != null,
				"Cannot get first element: 'references' are emtpy");
		return references.get(0);

	}

	public Node getLastReference() {
		Assert.assertTrue(references != null,
				"Cannot get last element: 'references' are emtpy");
		return references.get(references.size() - 1);
	}

	public List<Node> getReferencedBy() {
		return Collections
				.unmodifiableList(ListUtils.nullIsEmpty(referencedBy));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Node && this.getId().equals(((Node) obj).getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
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
			s.append(activeLevels.get(i) ? "│ " : "  ");
		}
		if (indent > 0) {
			s.append(node.isLast() ? "└─" : "├─");
		}
		s.append("o ");
		// TODO: remove this alternative node text, which is useful for
		// debugging at the moment.
		//
		// String text = node.getSemanticObject().toString();
		// text = text.replace("edu.tum.cs.cadmos.language.cadmos.impl.", "");
		// text = text.replace("Impl@", "[" + node.getIndex() + "]@");
		// text = text.replaceAll("@[a-f0-9]+", "");
		// s.append(text);
		s.append(node.getLocalId());
		appendIdList(s, "references", node.getReferences());
		appendIdList(s, "referencedBy", node.getReferencedBy());
		activeLevels.set(indent, true);
		for (final Node child : node) {
			s.append("\n");
			activeLevels.set(indent, !child.isLast());
			toString(child, indent + 1, s, activeLevels);
		}
	}

	private static void appendIdList(StringBuilder s, String name,
			List<Node> list) {
		if (list.isEmpty()) {
			return;
		}
		s.append("(");
		s.append(name);
		s.append(": ");
		s.append(list.get(0).getId());
		for (int i = 1; i < list.size(); i++) {
			s.append(", ");
			s.append(list.get(i).getId());
		}
		s.append(") ");
	}
}
