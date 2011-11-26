package edu.tum.cs.cadmos.core.model;

import edu.tum.cs.cadmos.commons.core.IListSet;
import edu.tum.cs.cadmos.core.machines.IMachine;

/**
 * An atomic component in a hierarchical data-flow network defines its behavior
 * by a machine that operates over a set of variables and ports.In particular,
 * an atomic component does not have any children components like an
 * {@link ICompositeComponent}.
 * <p>
 * The {@link AtomicComponent} class is a reference implementation of this
 * interface.
 * 
 * @author wolfgang.schwitzer
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 * 
 * @see AtomicComponent
 * @see ICompositeComponent
 */
public interface IAtomicComponent extends IComponent {

	/** Returns the variables that are in the scope of this atomic component. */
	IListSet<IVariable> getVariables();

	/** Returns the machine that defines the behavior of this atomic component. */
	IMachine getMachine();

}
