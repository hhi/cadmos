package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.core.machines.IMachine;

public interface IAtomicComponent extends IComponent {

	IListSet<IVariable> getVariables();

	IMachine getMachine();

}
