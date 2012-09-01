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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.tum.cs.cadmos.commons.core.Assert;
import edu.tum.cs.simulink.model.SimulinkBlock;
import edu.tum.cs.simulink.model.SimulinkConstants;
import edu.tum.cs.simulink.model.SimulinkInPort;
import edu.tum.cs.simulink.model.SimulinkLine;
import edu.tum.cs.simulink.model.SimulinkModel;
import edu.tum.cs.simulink.model.SimulinkOutPort;
import edu.tum.cs.simulink.model.SimulinkPortBase;

/**
 * A SimulinkUtils.
 * 
 * @author
 * @version $Rev$
 * @version $Author$
 * @version $Date$
 * @ConQAT.Rating RED Hash:
 */
public class SimulinkUtils {

	public static List<SimulinkBlock> getFollowingBlocks(SimulinkBlock b) {

		final List<SimulinkBlock> list = new LinkedList<>();

		for (final SimulinkLine l : b.getOutLines()) {
			list.add(l.getDstPort().getBlock());
		}

		return list;
	}

	public static List<SimulinkBlock> getPrecedingBlocks(SimulinkBlock b) {

		final List<SimulinkBlock> list = new LinkedList<>();

		for (final SimulinkLine l : b.getInLines()) {
			list.add(l.getSrcPort().getBlock());
		}

		return list;
	}

	public static boolean isDelay(SimulinkBlock b) {
		if (b.getType().equals(SimulinkConstants.TYPE_UnitDelay)
				|| b.getType().equals(SimulinkConstants.TYPE_Memory)) {
			return true;
		}
		return false;
	}

	public static boolean isPort(SimulinkBlock b) {
		if (b.getType().equals(SimulinkConstants.TYPE_Inport)
				|| b.getType().equals(SimulinkConstants.TYPE_Outport)) {
			return true;
		}

		return false;
	}

	public static int getDelay(SimulinkBlock b) {

		final String st = b.getParameter("SampleTime");
		if (isDelay(b)) {
			if (b.getType().equals(SimulinkConstants.TYPE_Memory)
					|| st.equals("-1")) {
				return 1;
			}
			return Integer.parseInt(st);

		}
		return 0;
	}

	public static Map<SimulinkPortBase, Integer> getDelayPaths(SimulinkLine l,
			int delays) {

		final Map<SimulinkPortBase, Integer> dstPorts = new HashMap<>();

		if (!isDelay(l.getSrcPort().getBlock())
				&& isDelay(l.getDstPort().getBlock())) {
			// beginning of the path
			// go to next blocks
			for (final SimulinkLine outline : l.getDstPort().getBlock()
					.getOutLines()) {
				dstPorts.putAll(getDelayPaths(outline, getDelay(l.getDstPort()
						.getBlock())));
			}

		}

		if (isDelay(l.getSrcPort().getBlock())
				&& isDelay(l.getDstPort().getBlock())) {
			for (final SimulinkLine outline : l.getDstPort().getBlock()
					.getOutLines()) {
				dstPorts.putAll(getDelayPaths(outline, delays
						+ getDelay(l.getDstPort().getBlock())));
			}
		}

		if (isDelay(l.getSrcPort().getBlock())
				&& !isDelay(l.getDstPort().getBlock())) {
			// end of a path

			dstPorts.put(l.getDstPort(), delays);
			return dstPorts;

		}

		return dstPorts;

	}

	public static SimulinkBlock getSubBlockInPortBlock(SimulinkLine l) {

		Assert.assertTrue(
				l.getDstPort().getBlock().hasSubBlocks(),
				"l was supposed to lead to a block containing Sub-Blocks but lead to '%s'",
				l.getDstPort().getBlock());

		if (l.getDstPort().getBlock().hasSubBlocks()) {
			for (final SimulinkBlock b : l.getDstPort().getBlock()
					.getSubBlocks()) {

				if (b.getType().equals(SimulinkConstants.TYPE_Inport)) {

					if (l.getDstPort() == l.getDstPort().getBlock()
							.getInPort(b.getParameter("Port"))) {
						return b;
					}
				}

				if (b.getType().equals("EnablePort")) {
					if (l.getDstPort().toString().startsWith("enable")) {
						return b;
					}
				}
				if (b.getType().equals("TriggerPort")) {
					if (l.getDstPort().toString().startsWith("trigger")) {
						return b;
					}
				}

			}
		}

		return l.getDstPort().getBlock();
	}

	public static SimulinkBlock getSubBlockOutPortBlock(SimulinkLine l) {

		Assert.assertTrue(
				l.getSrcPort().getBlock().hasSubBlocks(),
				"l was supposed to originate from a block containing Sub-Blocks but originates in '%s'",
				l.getSrcPort().getBlock());

		for (final SimulinkBlock b : l.getSrcPort().getBlock().getSubBlocks()) {
			if (b.getType().equals("Outport")
					&& l.getSrcPort().getIndex().equals(b.getParameter("Port"))) {
				return b;
			}
		}
		return l.getSrcPort().getBlock();

	}

	public static SimulinkInPort getParentInPort(SimulinkBlock inport) {
		Assert.assertTrue(inport.getType()
				.equals(SimulinkConstants.TYPE_Inport),
				"inport was supposed to be of the Type Inport but was '%s'",
				inport.getType());

		for (final SimulinkInPort p : inport.getParent().getInPorts()) {
			if (p.getIndex().equals(inport.getParameter("Port"))) {
				return p;
			}
		}

		return null;
	}

	public static SimulinkOutPort getParentOutPort(SimulinkBlock outport) {
		Assert.assertTrue(
				outport.getType().equals(SimulinkConstants.TYPE_Outport),
				"outport was supposed to be of the Type Outport but was '%s'",
				outport.getType());

		for (final SimulinkOutPort p : outport.getParent().getOutPorts()) {
			if (p.getIndex().equals(outport.getParameter("Port"))) {
				return p;
			}
		}

		return null;
	}

	/**
	 * Calculates the complexity of a SimulinkBlock by counting the children
	 * that are not to be removed when unfolding.
	 * 
	 * @param block
	 * @return
	 */
	public static int countComplexity(SimulinkBlock block) {

		int complexity = 0;

		if (block.hasSubBlocks()) {

			for (final SimulinkBlock b : block.getSubBlocks()) {
				if (b.hasSubBlocks()) {
					complexity += countComplexity(b);

				}
				if (b.getType().equals(SimulinkConstants.TYPE_Inport)
						&& b.getParent() instanceof SimulinkModel) {
					complexity++;
				} else if (b.getType().equals(SimulinkConstants.TYPE_Outport)
						&& b.getParent() instanceof SimulinkModel) {
					complexity++;
				} else if (!(b.getType().equals(
						SimulinkConstants.TYPE_UnitDelay)
						|| b.getType().equals(
								SimulinkConstants.TYPE_ZeroOrderHold)
						|| b.getType().equals(SimulinkConstants.TYPE_Memory)
						|| b.getType().equals(SimulinkConstants.TYPE_SubSystem)
						|| b.getType().equals(SimulinkConstants.TYPE_Inport) || b
						.getType().equals(SimulinkConstants.TYPE_Outport))) {
					complexity += 1;
				}
			}

		} else {
			return 1;
		}

		return complexity;
	}

	public static Set<SimulinkBlock> getAllBlocks(SimulinkBlock b) {

		final Set<SimulinkBlock> blocks = new HashSet<>();

		blocks.add(b);

		for (final SimulinkBlock sub : b.getSubBlocks()) {
			blocks.addAll(getAllBlocks(sub));
		}

		return blocks;
	}

	public static Set<SimulinkLine> getAllLines(SimulinkBlock b) {

		final Set<SimulinkLine> lines = new HashSet<>();

		lines.addAll(b.getOutLines());

		for (final SimulinkBlock sub : b.getSubBlocks()) {
			lines.addAll(getAllLines(sub));
		}

		return lines;
	}

	public static boolean hasOnlyPorts(SimulinkBlock b) {

		for (final SimulinkBlock sub : b.getSubBlocks()) {

			if (!sub.getType().contains("Port")) {
				return false;
			}

		}

		return true;
	}
}
