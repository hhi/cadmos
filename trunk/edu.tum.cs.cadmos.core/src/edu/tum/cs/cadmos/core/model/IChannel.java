package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.core.types.IType;

public interface IChannel extends IElement {

	IType getType();

	IComponent getSrc();

	IComponent getDst();

	int getDelay();

	int getSrcRate();

	int getDstRate();

}
