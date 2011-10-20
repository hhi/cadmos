package edu.tum.cs.cadmos.core.model;

import java.util.Collection;

public interface ICompositeComponent extends IComponent {

	Collection<IComponent> getChildren();

}
