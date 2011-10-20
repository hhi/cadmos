package edu.tum.cs.cadmos.core.model;

public class AtomicComponent extends AbstractComponent implements
		IAtomicComponent {

	public AtomicComponent(Object id, String name, ICompositeComponent parent) {
		super(id, name, parent);
	}

	public AtomicComponent(Object id, ICompositeComponent parent) {
		this(id, null, parent);
	}

}
