package edu.tum.cs.cadmos.core.model;

import static edu.tum.cs.cadmos.commons.Assert.assertNotNull;
import edu.tum.cs.cadmos.commons.IListSet;
import edu.tum.cs.cadmos.commons.ListSet;

public class CompositeComponent extends AbstractComponent implements
		ICompositeComponent {

	private final IListSet<IComponent> children = new ListSet<>();

	public CompositeComponent(String id, String name, ICompositeComponent parent) {
		super(id, name, parent);
	}

	public CompositeComponent(String id, ICompositeComponent parent) {
		this(id, null, parent);
	}

	@Override
	public IListSet<IComponent> getChildren() {
		return children;
	}

	/** {@inheritDoc} */
	@Override
	public IComponent clone(ICompositeComponent newParent) {
		final ICompositeComponent clone = new CompositeComponent(getId(),
				getName(), newParent);
		/* Clone the children components. */
		for (final IComponent child : getChildren()) {
			child.clone(clone);
		}
		/* Rewire the cloned children components. */
		final IListSet<IComponent> cloneChildren = clone.getChildren();
		for (final IComponent child : getChildren()) {
			for (final IChannel c : child.getIncoming()) {
				if (c.getSrc() == null) {
					assertNotNull(c.getDst(), "c.getDst()");
					final IComponent newDst = cloneChildren.get(c.getDst());
					c.clone(null, newDst);
				}
			}
			for (final IChannel c : child.getOutgoing()) {
				final IComponent newSrc = cloneChildren.get(c.getSrc());
				final IComponent newDst;
				if (c.getDst() == null) {
					newDst = null;
				} else {
					newDst = cloneChildren.get(c.getDst());
				}
				c.clone(newSrc, newDst);
			}
		}
		return clone;
	}
}
