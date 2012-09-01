package edu.tum.cs.cadmos.core.machines;

import edu.tum.cs.cadmos.commons.core.IListSet;

public interface ITransition {

	IListSet<ICondition> getPreConditions();

	IListSet<ICondition> getPostConditions();

}
