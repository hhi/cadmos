package edu.tum.cs.cadmos.matlab;

import java.util.HashMap;
import java.util.Map;

import edu.tum.cs.cadmos.core.model.AbstractComponent;
import edu.tum.cs.cadmos.core.model.IComponent;
import edu.tum.cs.simulink.model.SimulinkBlock;

public class MatlabSimulinkTranslation {

	public static Map<SimulinkBlock, IComponent> buildGenericMap(SimulinkBlock b) {

		Map<SimulinkBlock, AbstractComponent> map = new HashMap<>();

		if (b.hasSubBlocks()) {

		}

		return null;

	}

}
