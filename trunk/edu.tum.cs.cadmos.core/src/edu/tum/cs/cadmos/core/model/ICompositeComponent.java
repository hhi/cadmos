package edu.tum.cs.cadmos.core.model;

import java.util.Set;

public interface ICompositeComponent extends IComponent {

	Set<IComponent> getChildren();

}
