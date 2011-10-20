package edu.tum.cs.cadmos.core.machines;

import java.util.Set;

public interface ITransition {

	Set<ICondition> getPreConditions();

	Set<ICondition> getPostConditions();

}
