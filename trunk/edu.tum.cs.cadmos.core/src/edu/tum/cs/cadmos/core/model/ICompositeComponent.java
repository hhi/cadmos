package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.core.IListSet;

public interface ICompositeComponent extends IComponent {

	IListSet<IComponent> getChildren();

}
