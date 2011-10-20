package edu.tum.cs.cadmos.core.model;

import java.util.Set;

import edu.tum.cs.cadmos.core.machines.IMachine;

public interface IAtomicComponent extends IComponent {

	Set<IVariable> getVariables();

	IMachine getMachine();

}
