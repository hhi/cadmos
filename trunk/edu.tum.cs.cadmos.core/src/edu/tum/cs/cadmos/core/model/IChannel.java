package edu.tum.cs.cadmos.core.model;


public interface IChannel extends ITypedElement {

	IComponent getSrc();

	IComponent getDst();

	int getDelay();

	int getSrcRate();

	int getDstRate();

}
