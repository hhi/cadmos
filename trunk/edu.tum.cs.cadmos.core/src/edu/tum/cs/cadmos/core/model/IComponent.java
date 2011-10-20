package edu.tum.cs.cadmos.core.model;

import java.util.Set;

public interface IComponent extends IElement {

	ICompositeComponent getParent();

	Set<IChannel> getIncoming();

	Set<IChannel> getOutgoing();

}
