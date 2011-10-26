package edu.tum.cs.cadmos.matlab;

import java.util.LinkedList;
import java.util.List;

import edu.tum.cs.cadmos.commons.Assert;
import edu.tum.cs.simulink.model.SimulinkBlock;
import edu.tum.cs.simulink.model.SimulinkConstants;
import edu.tum.cs.simulink.model.SimulinkLine;
import edu.tum.cs.simulink.model.SimulinkModel;

public class SimulinkUtils {

	public static List<SimulinkBlock> getFollowingBlocks(SimulinkBlock b) {

		List<SimulinkBlock> list = new LinkedList<>();

		for (SimulinkLine l : b.getOutLines()) {
			list.add(l.getDstPort().getBlock());
		}

		return list;
	}

	public static List<SimulinkBlock> getPrecedingBlocks(SimulinkBlock b) {

		List<SimulinkBlock> list = new LinkedList<>();

		for (SimulinkLine l : b.getInLines()) {
			list.add(l.getSrcPort().getBlock());
		}

		return list;
	}

	public static SimulinkBlock getSubBlockInPort(SimulinkLine l) {

		Assert.assertTrue(
				l.getDstPort().getBlock().hasSubBlocks(),
				"l was supposed to lead to a block containing Sub-Blocks but lead to '%s'",
				l.getDstPort().getBlock());

		if (l.getDstPort().getBlock().hasSubBlocks()) {
			for (SimulinkBlock b : l.getDstPort().getBlock().getSubBlocks()) {

				if (b.getType().equals(SimulinkConstants.TYPE_Inport)) {

					if (l.getDstPort() == l.getDstPort().getBlock()
							.getInPort(b.getParameter("Port"))) {
						return b;
					}
				}

				if (b.getType().equals("EnablePort")) {
					if (l.getDstPort().toString().startsWith("enable"))
						return b;
				}
				if (b.getType().equals("TriggerPort")) {
					if (l.getDstPort().toString().startsWith("trigger"))
						return b;
				}

			}
		}

		return l.getDstPort().getBlock();
	}

	public static SimulinkBlock getSubBlockOutPort(SimulinkLine l) {

		Assert.assertTrue(
				l.getSrcPort().getBlock().hasSubBlocks(),
				"l was supposed to originate from a block containing Sub-Blocks but originates in '%s'",
				l.getSrcPort().getBlock());

		for (SimulinkBlock b : l.getSrcPort().getBlock().getSubBlocks()) {
			if (b.getType().equals("Outport")
					&& l.getSrcPort().getIndex().equals(b.getParameter("Port"))) {
				return b;
			}
		}
		return l.getSrcPort().getBlock();

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

			for (SimulinkBlock b : block.getSubBlocks()) {
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

}