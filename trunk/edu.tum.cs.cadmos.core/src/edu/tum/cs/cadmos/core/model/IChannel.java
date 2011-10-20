package edu.tum.cs.cadmos.core.model;


public interface IChannel extends IVariable {

	IComponent getSrc();

	IComponent getDst();

	int getDelay();

	int getSrcRate();

	int getDstRate();

}
