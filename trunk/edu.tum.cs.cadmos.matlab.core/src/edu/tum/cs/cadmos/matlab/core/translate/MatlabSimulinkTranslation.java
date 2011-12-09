/*--------------------------------------------------------------------------+
|                                                                          |
| Copyright 2008-2012 Technische Universitaet Muenchen                     |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+--------------------------------------------------------------------------*/

package edu.tum.cs.cadmos.matlab.core.translate;

import java.util.HashMap;
import java.util.Map;

import edu.tum.cs.cadmos.commons.core.Assert;
import edu.tum.cs.cadmos.core.model.AtomicComponent;
import edu.tum.cs.cadmos.core.model.Channel;
import edu.tum.cs.cadmos.core.model.CompositeComponent;
import edu.tum.cs.cadmos.core.model.EPortDirection;
import edu.tum.cs.cadmos.core.model.IChannel;
import edu.tum.cs.cadmos.core.model.IComponent;
import edu.tum.cs.cadmos.core.model.ICompositeComponent;
import edu.tum.cs.cadmos.core.model.IPort;
import edu.tum.cs.cadmos.core.model.Port;
import edu.tum.cs.cadmos.core.types.VoidType;
import edu.tum.cs.simulink.model.SimulinkBlock;
import edu.tum.cs.simulink.model.SimulinkConstants;
import edu.tum.cs.simulink.model.SimulinkInPort;
import edu.tum.cs.simulink.model.SimulinkLine;
import edu.tum.cs.simulink.model.SimulinkModel;
import edu.tum.cs.simulink.model.SimulinkPortBase;

/**
 * A MatlabSimulinkTranslation.
 * 
 * @author
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class MatlabSimulinkTranslation {

	public static Map<SimulinkPortBase, IPort> translatePorts(SimulinkBlock b,
			Map<SimulinkBlock, IComponent> components) {
		final Map<SimulinkPortBase, IPort> portMap = new HashMap<>();

		if (b instanceof SimulinkModel) {

			for (final SimulinkBlock sub : b.getSubBlocks()) {
				if (sub.getType().equals(SimulinkConstants.TYPE_Inport)) {
					portMap.put(sub.getOutPort("1"),
							new Port(sub.getId(), sub.getName(), VoidType.VOID,
									components.get(b), EPortDirection.INBOUND));
				} else if (sub.getType().equals(SimulinkConstants.TYPE_Outport)) {
					portMap.put(sub.getInPort("1"),
							new Port(sub.getId(), sub.getName(), VoidType.VOID,
									components.get(b), EPortDirection.OUTBOUND));
				} else {
					portMap.putAll(translatePorts(sub, components));
					return portMap;
				}
			}

		} else if (!SimulinkUtils.isDelay(b) && !SimulinkUtils.isPort(b)) {

			for (final SimulinkPortBase p : b.getInPorts()) {
				portMap.put(p,
						translateSimulinkPortToPort(p, components.get(b)));
			}
			for (final SimulinkPortBase p : b.getOutPorts()) {
				portMap.put(p,
						translateSimulinkPortToPort(p, components.get(b)));
			}

			for (final SimulinkBlock sub : b.getSubBlocks()) {
				portMap.putAll(translatePorts(sub, components));
			}
		}

		return portMap;
	}

	public static Map<SimulinkBlock, IComponent> translateComponents(
			SimulinkBlock b) {

		final Map<SimulinkBlock, IComponent> map = new HashMap<>();

		for (final SimulinkBlock block : edu.tum.cs.simulink.util.SimulinkUtils
				.listBlocksDepthFirst(b)) {

			if (block instanceof SimulinkModel) {

				map.put(block,
						new CompositeComponent(block.getId(), block.getName(),
								null));

			} else if (block.hasSubBlocks()) {

				final IComponent candidate = map.get(block.getParent());
				Assert.assertTrue(
						candidate instanceof ICompositeComponent,
						"The SimulinkBlock's parent was supposed to be a CompositeComponent but was '%s'",
						candidate);
				map.put(block,
						new CompositeComponent(block.getId(), block.getName(),
								(ICompositeComponent) candidate));

			} else if (!(SimulinkUtils.isDelay(block) || SimulinkUtils
					.isPort(block)))

			{

				final IComponent candidate = map.get(block.getParent());
				Assert.assertTrue(
						candidate instanceof ICompositeComponent,
						"The SimulinkBlock's parent was supposed to be a CompositeComponent but was '%s'",
						candidate);
				map.put(block, new AtomicComponent(block.getId(),
						(ICompositeComponent) candidate));

			}
		}

		return map;

	}

	public static IPort translateSimulinkPortToPort(SimulinkPortBase simPort,
			IComponent component) {

		final EPortDirection dir;

		if (simPort instanceof SimulinkInPort) {
			dir = EPortDirection.INBOUND;
		} else {
			dir = EPortDirection.OUTBOUND;
		}

		return new Port(simPort.getIndex(), simPort.toString(), VoidType.VOID,
				component, dir);

	}

	public static IChannel makeLine(IPort in, IPort out, int delay) {
		if (in.getDirection() == EPortDirection.OUTBOUND
				&& out.getDirection() == EPortDirection.INBOUND) {
			return new Channel(in.getId() + "->" + out.getId(), in, out, delay);
		}
		return null;
	}

	public static void translateChannels(SimulinkBlock b,
			Map<SimulinkPortBase, IPort> ports) {

		for (final SimulinkBlock sub : b.getSubBlocks()) {

			for (final SimulinkLine l : b.getOutLines()) {
				IPort first = null;
				IPort second = null;
				if (sub.getType().equals(SimulinkConstants.TYPE_Inport)) {
					if (b instanceof SimulinkModel) {
						first = ports.get(sub.getOutPort("1"));
					} else {
						first = ports.get(SimulinkUtils.getParentInPort(sub));
					}
				} else {
					first = ports.get(l.getSrcPort());
				}

				if (l.getDstPort().getBlock().getType()
						.equals(SimulinkConstants.TYPE_Outport)) {
					if (b instanceof SimulinkModel) {
						second = ports.get(sub.getInPort("1"));
					} else {
						second = ports.get(SimulinkUtils.getParentOutPort(sub));
					}
				} else {
					second = ports.get(l.getDstPort());
				}
				if (SimulinkUtils.isDelay(l.getSrcPort().getBlock())) {
					// nothing
				} else if (SimulinkUtils.isDelay(l.getDstPort().getBlock())) {
					final Map<SimulinkPortBase, Integer> dsts = SimulinkUtils
							.getDelayPaths(l, 0);

					for (final SimulinkPortBase pb : dsts.keySet()) {
						makeLine(first, ports.get(pb), dsts.get(pb));
					}

				} else {
					makeLine(first, second, 0);
				}

			}

			if (sub.hasSubBlocks()) {
				for (final SimulinkBlock subsub : sub.getSubBlocks()) {
					translateChannels(subsub, ports);
				}
			}

		}

	}

	public static ICompositeComponent translateSimulinkModel(SimulinkModel model) {

		final Map<SimulinkBlock, IComponent> compMap = translateComponents(model);
		final Map<SimulinkPortBase, IPort> portMap = translatePorts(model,
				compMap);
		translateChannels(model, portMap);

		return (ICompositeComponent) compMap.get(model);

	}

}
