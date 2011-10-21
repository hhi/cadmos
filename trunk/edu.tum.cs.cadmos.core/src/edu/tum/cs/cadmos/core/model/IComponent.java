package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.IListSet;

public interface IComponent extends IElement {

	ICompositeComponent getParent();

	IListSet<IChannel> getIncoming();

	IListSet<IChannel> getOutgoing();

}
