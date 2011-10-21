package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.IListSet;

public interface ICompositeComponent extends IComponent {

	IListSet<IComponent> getChildren();

}
